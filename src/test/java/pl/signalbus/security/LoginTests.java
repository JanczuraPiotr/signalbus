package pl.signalbus.security;

import org.junit.jupiter.api.Test;
import org.jooq.DSLContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import pattern.Pattern;
import pl.signalbus.config.SecurityConfig;

import java.util.UUID;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class LoginTests {

    @Test
    void directLoginRedirectsToAdminPanel() throws Exception {
        assertRedirect(UserRole.ROLE_ADMIN, "/app/admin");
    }

    @Test
    void directConsoleLoginRedirectsToConsole() throws Exception {
        assertRedirect(UserRole.ROLE_CONSOLE, "/app/console");
    }

    private void assertRedirect(String role, String target) throws Exception {
        var request = new MockHttpServletRequest("POST", "/login");
        var response = new MockHttpServletResponse();
        // Jako user może pojawić się UUID konsoli lub login pasujący do danych uwierzytelniających administratora
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

    @Test
    void consoleLoginUpdatesLastLogin() throws Exception {
        var database = mock(DSLContext.class, RETURNS_DEEP_STUBS);
        var request = new MockHttpServletRequest("POST", "/login");
        var response = new MockHttpServletResponse();
        var consoleId = UUID.randomUUID();

        new SecurityConfig().authenticationSuccessHandler(database)
                .onAuthenticationSuccess(request, response,
                        new TestingAuthenticationToken(consoleId.toString(), null, UserRole.ROLE_CONSOLE));

        verify(database).update(table(name("console")));
    }

    @Test
    void adminLoginDoesNotUpdateConsoleLastLogin() throws Exception {
        var database = mock(DSLContext.class, RETURNS_DEEP_STUBS);
        var request = new MockHttpServletRequest("POST", "/login");
        var response = new MockHttpServletResponse();

        new SecurityConfig().authenticationSuccessHandler(database)
                .onAuthenticationSuccess(request, response,
                        new TestingAuthenticationToken("admin", null, UserRole.ROLE_ADMIN));

        verifyNoInteractions(database);
    }
}
