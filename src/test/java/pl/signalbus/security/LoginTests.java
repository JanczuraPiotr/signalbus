package pl.signalbus.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import pattern.Pattern;
import pl.signalbus.config.SecurityConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// TODO init

class LoginTests {

    @Test
    void directLoginRedirectsToAdminPanel() throws Exception {
        assertRedirect(UserRole.ROLE_ADMIN, "/app/admin");
    }

    @Test
    void directconsoleLoginRedirectsToConsole() throws Exception {
        assertRedirect(UserRole.ROLE_CONSOLE, "/app/console");
    }

    private void assertRedirect(String role, String target) throws Exception {
        var request = new MockHttpServletRequest("POST", "/login");
        var response = new MockHttpServletResponse();
        // Jako user może pojawić się UUID consoleu lub login pasujący do danych uwierzytelniających administratora
        new SecurityConfig().authenticationSuccessHandler()
                            .onAuthenticationSuccess(request,
                                                     response,
                                                     new TestingAuthenticationToken("user", null, role));
        assertEquals(target, response.getRedirectedUrl());
    }

    @Test
    void consoleLoginDiscardsSavedAdminRequest() throws Exception {
        var request = new MockHttpServletRequest("GET", "/app/admin");
        var response = new MockHttpServletResponse();
        var cache = new HttpSessionRequestCache();
        cache.saveRequest(request, response);
        request.setMethod("POST");
        request.setRequestURI("/login");

        new SecurityConfig().authenticationSuccessHandler().onAuthenticationSuccess(
                request, response, new TestingAuthenticationToken("console", null, UserRole.ROLE_CONSOLE));

        assertEquals("/app/console", response.getRedirectedUrl());
        assertNull(cache.getRequest(request, response));
    }

    @Test
    void redirectIncludesContextPath() throws Exception {
        var request = new MockHttpServletRequest("POST", "/signalbus/login");
        request.setContextPath("/signalbus");
        var response = new MockHttpServletResponse();
        new SecurityConfig().authenticationSuccessHandler().onAuthenticationSuccess(
                request, response, new TestingAuthenticationToken(Pattern.ADMIN_NAME, null, UserRole.ROLE_ADMIN));
        assertEquals("/signalbus/app/admin", response.getRedirectedUrl());
    }
}
