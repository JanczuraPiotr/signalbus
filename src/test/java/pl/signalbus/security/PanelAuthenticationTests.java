package pl.signalbus.security;

import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import pattern.Pattern;
import pl.signalbus.config.SecurityConfig;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PanelAuthenticationTests {

    private DaoAuthenticationProvider provider() {
        var properties = new SecurityProperties();
        properties.getUser().setName(Pattern.ADMIN_NAME);
        properties.getUser().setPassword(Pattern.ADMIN_PASSWORD);
        var passwordField = DSL.field(DSL.name(Pattern.ADMIN_PASSWORD), String.class);
        var connection = new MockConnection(context -> {
            var dsl = DSL.using(SQLDialect.POSTGRES);
            var result = dsl.newResult(passwordField);
            if (Pattern.CONSOLE_UUID.equals(context.bindings()[0])) {
                result.add(dsl.newRecord(passwordField).value1("console-secret"));
            }
            return new MockResult[]{new MockResult(result.size(), result)};
        });
        var config = new SecurityConfig();
        return config.panelAuthenticationProvider(config.panelUsers(properties, DSL.using(connection, SQLDialect.POSTGRES)));
    }

    @Test
    void adminUsesConfiguredCredentialsAndAdminRole() {
        var authentication = provider().authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(Pattern.ADMIN_NAME, Pattern.ADMIN_PASSWORD));
        assertTrue(authentication.isAuthenticated());
        assertTrue(authentication.getAuthorities().stream()
                                 .anyMatch(role -> UserRole.ROLE_ADMIN.equals(role.getAuthority())));
        assertFalse(authentication.getAuthorities().stream()
                                  .anyMatch(role -> UserRole.ROLE_CONSOLE.equals(role.getAuthority())));
    }

    @Test
    void consoleUsesDatabasePasswordAndConsoleRole() {
        var authentication = provider().authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(Pattern.CONSOLE_UUID.toString(), "console-secret"));
        assertTrue(authentication.isAuthenticated());
        assertEquals(Pattern.CONSOLE_UUID.toString(), authentication.getName());
        assertFalse(authentication.getAuthorities().stream()
                                  .anyMatch(role -> UserRole.ROLE_ADMIN.equals(role.getAuthority())));
        assertTrue(authentication.getAuthorities().stream()
                                 .anyMatch(role -> UserRole.ROLE_CONSOLE.equals(role.getAuthority())));
    }

    @Test
    void wrongPasswordsAreRejectedForBothPanels() {
        for (String username : new String[]{Pattern.ADMIN_NAME, Pattern.CONSOLE_UUID.toString()}) {
            assertThrows(BadCredentialsException.class, () -> provider().authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(username, "wrong")));
        }
    }

    @Test
    void missingConsoleAndMalformedUuidAreRejected() {
        for (String username : new String[]{UUID.randomUUID().toString(), "invalid", "1-1-1-1-1"}) {
            assertThrows(BadCredentialsException.class, () -> provider().authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(username, "console-secret")));
        }
    }
}
