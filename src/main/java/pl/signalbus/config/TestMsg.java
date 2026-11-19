package pl.signalbus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Klasa do mokowania dużych wiadomości na czas testów.
// Jeżeli w tekście są symbole zastępcze -> {x}, to po zamokowaniu musi być ich taka sama ilość.
@Component
@Profile("test")
public class TestMsg extends Msg {
    private static final Map<String, String> OVERRIDES = new ConcurrentHashMap<>();

    static {
        reset();
    }

    public TestMsg() {
        super();
    }

    @Autowired
    public TestMsg(MessageSource messageSource, ResourceLoader resourceLoader) {
        super(messageSource, resourceLoader);
    }

    private static void mock(String key, String value) {
        OVERRIDES.put(key, value);
    }

    private static void reset() {
        mock("console.readme", "console-readme");
        mock("_.f.none", "_-f-none {0}");
    }

    @Override
    protected String resolveMessage(String key, Object[] args) {
        String value = OVERRIDES.get(key);
        if (value != null) {
            return (args != null && args.length > 0) ? MessageFormat.format(value, args) : value;
        }
        return super.resolveMessage(key, args);
    }
}
