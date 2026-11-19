package pl.signalbus.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.junit.jupiter.api.Assertions.*;

class TestMsgTest {

    @Test
    void shouldUseTestMessagesWithoutSpringContext() {
        assertEquals("console-readme", Msg.get("console.readme"));
        assertEquals("console-readme", Msg.get("console.readme", "argument"));
    }

    @Test
    void shouldSelectImplementationForActiveSpringProfile() {
        try {
            try (var context = contextFor("dev")) {
                assertEquals(Msg.class, context.getBean(Msg.class).getClass());
                assertEquals("Brak pliku test.txt", Msg.get("_.f.none", "test.txt"));
                assertEquals("Brak pliku {0}", Msg.get("_.f.none"));
            }
        } finally {
            try (var context = contextFor("test")) {
                assertInstanceOf(TestMsg.class, context.getBean(Msg.class));
                assertEquals("_-f-none file-name", Msg.get("_.f.none", "file-name"));
                assertEquals("_-f-none {0}", Msg.get("_.f.none"));
            }
        }
    }

    private AnnotationConfigApplicationContext contextFor(String profile) {
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(profile);
        context.registerBean("messageSource", ResourceBundleMessageSource.class, () -> {
            var source = new ResourceBundleMessageSource();
            source.setBasename("messages");
            source.setDefaultEncoding("UTF-8");
            return source;
        });
        context.register(Msg.class, TestMsg.class);
        context.refresh();
        return context;
    }
}
