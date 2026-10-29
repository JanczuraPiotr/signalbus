package pl.signalbus.scurity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pattern.Pattern;
import pl.signalbus.security.UserRole;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminLoginTest {
    // NOTE AI

    @LocalServerPort
    private int port;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final String INCORRECT_ID = "zle-id-uzytkownika-123";

    @BeforeEach
    void setUpSecurity() {
        UserDetails adminDetails = User.withUsername(Pattern.ADMIN_NAME)
                                       .password("{noop}" + Pattern.ADMIN_PASSWORD)
                                       .roles(UserRole.ADMIN)
                                       .build();

        when(userDetailsService.loadUserByUsername(Pattern.ADMIN_NAME))
                .thenReturn(adminDetails);

        when(userDetailsService.loadUserByUsername(INCORRECT_ID))
                .thenThrow(new UsernameNotFoundException("User not found"));
    }

    @Test
    void shouldAllowAccessForAdminUser() throws Exception {
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        HttpClient client = HttpClient.newBuilder()
                                      .cookieHandler(cookieManager)
                                      .followRedirects(HttpClient.Redirect.NEVER)
                                      .build();

        // 1. Visit /app/admin anonymously -> redirects to /login and saves target
        HttpRequest initialReq = HttpRequest.newBuilder()
                                            .uri(URI.create("http://localhost:" + port + "/app/admin"))
                                            .GET()
                                            .build();
        HttpResponse<String> initialResp = client.send(initialReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, initialResp.statusCode());
        assertTrue(initialResp.headers().firstValue("Location").orElse("").endsWith("/login"));

        // 2. Fetch /login page to get CSRF token
        HttpRequest loginPageReq = HttpRequest.newBuilder()
                                              .uri(URI.create("http://localhost:" + port + "/login"))
                                              .GET()
                                              .build();
        HttpResponse<String> loginPageResp = client.send(loginPageReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, loginPageResp.statusCode());

        String csrfToken = "";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("name=\"_csrf\"\\s+value=\"([^\"]+)\"")
                                                                  .matcher(loginPageResp.body());
        if (matcher.find()) {
            csrfToken = matcher.group(1);
        }

        // 3. Perform form login with admin credentials and CSRF
        String formData = "username=" + URLEncoder.encode(Pattern.ADMIN_NAME, StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(Pattern.ADMIN_PASSWORD, StandardCharsets.UTF_8)
                + "&_csrf=" + URLEncoder.encode(csrfToken, StandardCharsets.UTF_8);

        HttpRequest loginReq = HttpRequest.newBuilder()
                                          .uri(URI.create("http://localhost:" + port + "/login"))
                                          .header("Content-Type", "application/x-www-form-urlencoded")
                                          .POST(HttpRequest.BodyPublishers.ofString(formData))
                                          .build();
        HttpResponse<String> loginResp = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, loginResp.statusCode());
        assertTrue(loginResp.headers().firstValue("Location").orElse("").endsWith("/app/admin"));

        // 4. Access /app/admin as authenticated admin -> status 200 OK
        HttpRequest adminReq = HttpRequest.newBuilder()
                                          .uri(URI.create("http://localhost:" + port + "/app/admin"))
                                          .GET()
                                          .build();
        HttpResponse<String> adminResp = client.send(adminReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, adminResp.statusCode());
    }

    @Test
    void shouldRedirectAnonymousUserToLogin() throws Exception {
        HttpClient client = HttpClient.newBuilder()
                                      .followRedirects(HttpClient.Redirect.NEVER)
                                      .build();

        HttpRequest req = HttpRequest.newBuilder()
                                     .uri(URI.create("http://localhost:" + port + "/app/admin"))
                                     .GET()
                                     .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, resp.statusCode());
        assertTrue(resp.headers().firstValue("Location").orElse("").endsWith("/login"));
    }
}
