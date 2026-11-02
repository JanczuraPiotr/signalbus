package pl.signalbus;

import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.signalbus.security.UserRole;

import java.util.UUID;

import static org.jooq.impl.DSL.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO init

@ActiveProfiles("test")
@SpringBootTest(properties = {"spring.security.user.name=admin", "spring.security.user.password=password"})
class PanelLoginIntegrationTests {
    private static final UUID CONSOLE_ID = UUID.fromString("72a51d70-5ee1-4c88-9ee8-a8b01195da12");

    @Autowired
    WebApplicationContext context;

    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    DSLContext database;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        when(database.select(field(name("password"), String.class))
                .from(table(name("console")))
                .where(field(name("id"), UUID.class).eq(CONSOLE_ID))
                .fetchOne(field(name("password"), String.class))).thenReturn("console-secret");
    }

    @Test
    void adminFormLoginRedirectsToAdminHome() throws Exception {
        mvc.perform(formLogin().user("admin").password("password"))
                .andExpect(authenticated().withRoles(UserRole.ADMIN))
                .andExpect(redirectedUrl("/app/admin"));
    }

    @Test
    void consoleFormLoginRedirectsToconsoleHome() throws Exception {
        mvc.perform(formLogin().user(CONSOLE_ID.toString()).password("console-secret"))
                .andExpect(authenticated().withRoles(UserRole.CONSOLE))
                .andExpect(redirectedUrl("/app/console"));
    }

    @Test
    void wrongCredentialsReturnToLoginWithError() throws Exception {
        for (String username : new String[]{"admin", CONSOLE_ID.toString(), "invalid-uuid"}) {
            mvc.perform(formLogin().user(username).password("wrong"))
                    .andExpect(unauthenticated())
                    .andExpect(redirectedUrl("/login?error"));
        }
    }

    @Test
    void anonymousPanelRequestsRedirectToLogin() throws Exception {
        for (String path : new String[]{"/app/admin", "/app/console"}) {
            mvc.perform(get(path)).andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }
    }

    @Test
    void panelRolesCannotAccessEachOthersViews() throws Exception {
        mvc.perform(get("/app/admin").with(user("console").roles(UserRole.CONSOLE)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/app/console").with(user("admin").roles(UserRole.ADMIN)))
                .andExpect(status().isForbidden());
    }

    @Test
    void consoleLoginRejectsAdminCredentialsAndAllowsconsoleRetry() throws Exception {
        for (String path : new String[]{"/app/console", "/app/console/"}) {
            var session = startPanelLogin(path);
            mvc.perform(post("/login").session(session).with(csrf())
                            .param("username", "admin").param("password", "password"))
                    .andExpect(unauthenticated())
                    .andExpect(redirectedUrl("/login?error"));
            // The target remains console even after a failed attempt and rendering the error page.
            mvc.perform(get("/login?error").session(session)).andExpect(status().isOk());
            mvc.perform(post("/login").session(session).with(csrf())
                            .param("username", "admin").param("password", "password"))
                    .andExpect(unauthenticated())
                    .andExpect(redirectedUrl("/login?error"));
            mvc.perform(post("/login").session(session).with(csrf())
                                      .param("username", CONSOLE_ID.toString()).param("password", "console-secret"))
                    .andExpect(authenticated().withRoles(UserRole.CONSOLE))
                    .andExpect(redirectedUrl("/app/console"));
        }
    }

    @Test
    void adminLoginRejectsconsoleCredentialsAndAllowsAdminRetry() throws Exception {
        var session = startPanelLogin("/app/admin");
        mvc.perform(post("/login").session(session).with(csrf())
                                  .param("username", CONSOLE_ID.toString()).param("password", "console-secret"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
        mvc.perform(post("/login").session(session).with(csrf())
                        .param("username", "admin").param("password", "password"))
                .andExpect(authenticated().withRoles(UserRole.ADMIN))
                .andExpect(redirectedUrl("/app/admin"));
    }

    private MockHttpSession startPanelLogin(String path) throws Exception {
        return (MockHttpSession) mvc.perform(get(path))
                .andExpect(redirectedUrl("/login"))
                .andReturn().getRequest().getSession(false);
    }

}
