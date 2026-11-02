package pl.signalbus;

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
import pl.signalbus.security.UserRole;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// TODO init

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
        assertTrue(authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals(UserRole.ROLE_ADMIN)));
        assertFalse(authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals(UserRole.ROLE_CONSOLE)));
    }

    @Test
    void consoleUsesDatabasePasswordAndconsoleRole() {
        var authentication = provider().authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(Pattern.CONSOLE_UUID.toString(), "console-secret"));
        assertTrue(authentication.isAuthenticated());
        assertEquals(Pattern.CONSOLE_UUID.toString(), authentication.getName());
        assertTrue(authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals(UserRole.ROLE_CONSOLE)));
        assertFalse(authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals(UserRole.ROLE_ADMIN)));
    }

    @Test
    void wrongPasswordsAreRejectedForBothPanels() {
        for (String username : new String[]{Pattern.ADMIN_NAME, Pattern.CONSOLE_UUID.toString()}) {
            assertThrows(BadCredentialsException.class, () -> provider().authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(username, "wrong")));
        }
    }

    @Test
    void missingconsoleAndMalformedUuidAreRejected() {
        for (String username : new String[]{UUID.randomUUID().toString(), "invalid", "1-1-1-1-1"}) {
            assertThrows(BadCredentialsException.class, () -> provider().authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(username, "console-secret")));
        }
    }
}
