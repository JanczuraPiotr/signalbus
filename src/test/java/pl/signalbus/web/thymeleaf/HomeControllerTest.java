package pl.signalbus.web.thymeleaf;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeControllerTest {

    @Test
    void identifiesAdminLoginFromSavedRequest() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new HomeController(new MockEnvironment())).build();
        for (String path : new String[]{"/app/admin", "/app/admin/", "/app/admin/settings"}) {
            var session = new MockHttpSession();
            var request = new MockHttpServletRequest("GET", path);
            request.setSession(session);
            new HttpSessionRequestCache().saveRequest(request, new MockHttpServletResponse());
            mvc.perform(get("/login").session(session))
                    .andExpect(model().attribute("adminLogin", true));
            mvc.perform(get("/login").param("error", "").session(session))
                    .andExpect(model().attribute("adminLogin", true));
        }
        mvc.perform(get("/login")).andExpect(model().attribute("adminLogin", false));
    }

    @Test
    void exposesDevProfileToHomePage() throws Exception {
        checkProfile("dev", true);
        checkProfile("test", false);
        checkProfile("prod", false);
        checkProfile(null, false);
    }

    private void checkProfile(String profile, boolean expected) throws Exception {
        var context = new AnnotationConfigWebApplicationContext();
        var environment = new MockEnvironment();
        if (profile != null) {
            environment.setActiveProfiles(profile);
        }
        context.setEnvironment(environment);
        context.register(HomeController.class);
        context.refresh();
        try {
            var mvc = MockMvcBuilders.standaloneSetup(context.getBean(HomeController.class)).build();
            mvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("web/index"))
                    .andExpect(model().attribute("isDev", expected));
        } finally {
            context.close();
        }
    }
}