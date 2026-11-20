package pl.signalbus.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageConfigTest {
    @Test
    void shouldResolveReferencesToOtherMessages() {
        try (var context = new AnnotationConfigApplicationContext(MessageConfig.class)) {
            MessageSource messages = context.getBean(MessageSource.class);
            assertEquals("Zapisz", messages.getMessage("action.console.settings.save", null, Locale.ROOT));
            assertEquals("Anuluj", messages.getMessage("action.console.settings.cancel", null, Locale.ROOT));
            assertEquals("Zapisz", messages.getMessage("action.console.settings.save", new Object[]{"unused"}, Locale.ROOT));
            assertEquals("Brak pliku test.txt", messages.getMessage("_.f.none", new Object[]{"test.txt"}, Locale.ROOT));
        }
    }
}