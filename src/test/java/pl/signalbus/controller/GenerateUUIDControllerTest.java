package pl.signalbus.controller;

import org.junit.jupiter.api.Test;
import pl.signalbus.service.ConsoleUUIDService;
import pl.signalbus.service.ConsoleUUIDServiceImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GenerateUUIDControllerTest {

    @Test
    void shouldGenerateUUID() {
        ConsoleUUIDService service = new ConsoleUUIDServiceImpl();
        GenerateUUIDController controller = new GenerateUUIDController(service);

        String result = controller.generateUUID();

        assertNotNull(result);
        assertDoesNotThrow(() -> UUID.fromString(result));
    }

    @Test
    void shouldUseConsoleUUIDService() {
        ConsoleUUIDService mockService = () -> "test-uuid-value";
        GenerateUUIDController controller = new GenerateUUIDController(mockService);

        assertEquals("test-uuid-value", controller.generateUUID());
    }
}
