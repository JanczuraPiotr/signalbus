package pl.templates.error;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pattern.Pattern;
import pl.signalbus.SignalbusApplication;
import pl.signalbus.config.Msg;
import pl.signalbus.security.UserRole;
import pl.signalbus.web.vaadin.error.CustomInternalServerErrorView;
import pl.signalbus.web.vaadin.error.CustomNotFoundView;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// TODO init

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
    void shouldReturn404WhenRouteIsUnknown() throws Exception {
        mockMvc.perform(get("/non-existent-page"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRenderLoginPage() throws Exception {
        String html = mockMvc.perform(get("/login"))
                             .andExpect(status().isOk())
//                             .andExpect(view().name("web/login"))
                             .andReturn()
                             .getResponse()
                             .getContentAsString();

        Document doc = Jsoup.parse(html);
        assertEquals(Msg.get("login.title"), doc.select("main .apla form h1").text());


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
    void shouldRender403TemplateContent() throws Exception {
        String html = mockMvc.perform(get("/error")
                                              .accept(MediaType.TEXT_HTML)
                                              .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                             .andExpect(status().isForbidden())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();
        Document doc = Jsoup.parse(html);

        assertEquals("403", doc.select(".error-container .content .error h1.code").text());
        assertEquals(Msg.get("http.403.title"), doc.select(".error-container .content .error h2.title").text());
        assertEquals(Msg.get("http.403.description"), doc.select(".error-container .content .error p.description").text());
        assertEquals("/", doc.select(".error-container .content .buttonGroup a.action-main").attr("href"));
        assertEquals("/app/console", doc.select(".error-container .content .buttonGroup a.action-console").attr("href"));
    }

    @Test
    void shouldRender404TemplateContent() throws Exception {
        String html = mockMvc.perform(get("/error")
                                              .accept(MediaType.TEXT_HTML)
                                              .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404))
                             .andExpect(status().isNotFound())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();
        Document doc = Jsoup.parse(html);

        assertEquals("404", doc.select(".error-container .content .error h1.code").text());
        assertEquals(Msg.get("http.404.title"), doc.select(".error-container .content .error h2.title").text());
        assertEquals(Msg.get("http.404.description"), doc.select(".error-container .content .error p.description").text());
        assertEquals("/", doc.select(".error-container .content .buttonGroup a.action-main").attr("href"));
        assertEquals("/app/console", doc.select(".error-container .content .buttonGroup a.action-console").attr("href"));
    }

    @Test
    void shouldRender500TemplateContent() throws Exception {
        String html = mockMvc.perform(get("/error")
                                              .accept(MediaType.TEXT_HTML)
                                              .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500))
                             .andExpect(status().isInternalServerError())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();
        Document doc = Jsoup.parse(html);

        assertEquals("500", doc.select(".error-container .content .error h1.code").text());
        assertEquals(Msg.get("http.500.title"), doc.select(".error-container .content .error h2.title").text());
        assertEquals(Msg.get("http.500.description"), doc.select(".error-container .content .error p.description").text());
        assertEquals("/", doc.select(".error-container .content .buttonGroup a.action-main").attr("href"));
        assertEquals("/app/console", doc.select(".error-container .content .buttonGroup a.action-console").attr("href"));
    }

    @Test
    void catch403() throws Exception {
        String html = mockMvc.perform(get("/app/admin")
                                              .with(SecurityMockMvcRequestPostProcessors
                                                            .user(Pattern.CONSOLE_UUID.toString())
                                                            .roles(UserRole.CONSOLE)
                                              ).accept(MediaType.TEXT_HTML))
                             .andExpect(status().isForbidden())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();
    }

    @Test
    void vaadinNotFoundViewReturns404StatusCode() {
        CustomNotFoundView view = new CustomNotFoundView();
        BeforeEnterEvent event = Mockito.mock(BeforeEnterEvent.class);
        ErrorParameter<NotFoundException> parameter = new ErrorParameter<>(
                NotFoundException.class,
                new NotFoundException("Page not found"),
                "Custom not found message"
        );

        int statusCode = view.setErrorParameter(event, parameter);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCode);
    }

    @Test
    void vaadinInternalServerErrorViewReturns500StatusCode() {
        CustomInternalServerErrorView view = new CustomInternalServerErrorView();
        BeforeEnterEvent event = Mockito.mock(BeforeEnterEvent.class);
        ErrorParameter<Exception> parameter = new ErrorParameter<>(
                Exception.class,
                new RuntimeException("Something failed"),
                "Internal error"
        );

        int statusCode = view.setErrorParameter(event, parameter);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, statusCode);
    }
}
