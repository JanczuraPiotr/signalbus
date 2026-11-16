package pl.signalbus.web.thymeleaf;

import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.signalbus.service.ConsoleUUIDService;

import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ConsoleCreatorControllerTest {

    private static final String TEST_UUID = "72a51d70-5ee1-4c88-9ee8-a8b01195da12";

    @Autowired
    WebApplicationContext context;

    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    DSLContext database;

    @MockitoBean
    ConsoleUUIDService consoleUUIDService;

    @Autowired
    org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        when(consoleUUIDService.generateUUID()).thenReturn(TEST_UUID);
    }

    @Test
    void getIndexReturnsIndexView() throws Exception {
        mvc.perform(get("/console-creator"))
                .andExpect(status().isOk())
                .andExpect(view().name("web/console-creator/index"));
    }

    @Test
    void getCreatorReturnsCreatorViewWithGeneratedUUID() throws Exception {
        mvc.perform(get("/console-creator/creator"))
                .andExpect(status().isOk())
                .andExpect(view().name("web/console-creator/creator"))
                .andExpect(model().attribute("uuid", TEST_UUID))
                .andExpect(model().attribute("username", TEST_UUID));
    }

    @Test
    void postCreatorWithEmptyPasswordReturnsError() throws Exception {
        mvc.perform(post("/console-creator/creator")
                        .with(csrf())
                        .param("username", TEST_UUID)
                        .param("password", "")
                        .param("passwordRepeat", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("web/console-creator/creator"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("username", TEST_UUID));

        verifyNoInteractions(database);
    }

    @Test
    void postCreatorWithPasswordMismatchReturnsError() throws Exception {
        mvc.perform(post("/console-creator/creator")
                        .with(csrf())
                        .param("username", TEST_UUID)
                        .param("password", "secret123")
                        .param("passwordRepeat", "different123"))
                .andExpect(status().isOk())
                .andExpect(view().name("web/console-creator/creator"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("username", TEST_UUID));

        verifyNoInteractions(database);
    }

    @Test
    void postCreatorWithInvalidUUIDReturnsError() throws Exception {
        mvc.perform(post("/console-creator/creator")
                        .with(csrf())
                        .param("username", "invalid-uuid")
                        .param("password", "secret123")
                        .param("passwordRepeat", "secret123"))
                .andExpect(status().isOk())
                .andExpect(view().name("web/console-creator/creator"))
                .andExpect(model().attributeExists("error"));

        verifyNoInteractions(database);
    }

    @Test
    void postCreatorWithValidDataInsertsIntoConsoleTableAndRedirectsToLogin() throws Exception {
        mvc.perform(post("/console-creator/creator")
                        .with(csrf())
                        .param("username", TEST_UUID)
                        .param("password", "secret123")
                        .param("passwordRepeat", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        var hash = ArgumentCaptor.forClass(String.class);
        verify(database.insertInto(table(name("console")), field(name("id"), UUID.class),
                field(name("password"), String.class)))
                .values(eq(UUID.fromString(TEST_UUID)), hash.capture());
        assertNotEquals("secret123", hash.getValue());
        assertTrue(passwordEncoder.matches("secret123", hash.getValue()));
        assertFalse(passwordEncoder.matches("wrong", hash.getValue()));
    }
}
