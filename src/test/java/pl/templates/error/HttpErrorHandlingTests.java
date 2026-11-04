package pl.templates.error;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.signalbus.SignalbusApplication;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Tag("integration")
@ActiveProfiles("test")
@SpringBootTest(classes = SignalbusApplication.class)
class HttpErrorHandlingTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldRenderCreatorPage() throws Exception {
        for (String path : new String[]{"/console-creator", "/console-creator/"}) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(view().name("web/console-creator/index"))
                    .andExpect(content().string(containsString("/web/css/home.css")));
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldForwardTo403WhenConsole() throws Exception {
        mockMvc.perform(get("/app/console"))
               .andExpect(status().isForbidden())
               .andExpect(forwardedUrl("/error"));
        mockMvc.perform(get("/error").accept(MediaType.TEXT_HTML)
                                     .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
               .andExpect(status().isForbidden())
               .andExpect(view().name("error/403"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldForwardTo403WhenAdmin() throws Exception {
        mockMvc.perform(get("/app/admin"))
               .andExpect(status().isForbidden())
               .andExpect(forwardedUrl("/error"));
        mockMvc.perform(get("/error").accept(MediaType.TEXT_HTML)
                                     .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
               .andExpect(status().isForbidden())
               .andExpect(view().name("error/403"));
    }

    @Test
    void shouldRender404TemplateContent() throws Exception {
        mockMvc.perform(get("/non-existent-page"))
               .andExpect(status().isNotFound());

        mockMvc.perform(get("/error").accept(MediaType.TEXT_HTML)
                                     .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404))
               .andExpect(status().isNotFound())
               .andExpect(view().name("error/404"));
    }

    @Test
    void shouldRender500TemplateContent() throws Exception {
        mockMvc.perform(get("/error").accept(MediaType.TEXT_HTML)
                                     .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500))
               .andExpect(status().isInternalServerError())
               .andExpect(view().name("error/500"));
    }

}
