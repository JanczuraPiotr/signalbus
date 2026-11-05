package pl.signalbus.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StartupConfigLoggerTest {

    @Autowired
    private StartupConfigLogger startupConfigLogger;

    @Test
    void shouldInitializeWithoutErrors() {
        assertDoesNotThrow(() -> startupConfigLogger.init());
    }
}
