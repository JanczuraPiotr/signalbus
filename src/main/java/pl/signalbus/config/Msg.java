package pl.signalbus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Profiles;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("!test")
public class Msg {
    private final MessageSource messageSource;
    private final ResourceLoader resourceLoader;
    private final Map<String, String> fileCache = new ConcurrentHashMap<>();
    private static volatile Msg instance = new StandardEnvironment().acceptsProfiles(Profiles.of("test"))
            ? new TestMsg() : new Msg();

    protected Msg() {
        ResourceBundleMessageSource defaultSource = new ResourceBundleMessageSource();
        defaultSource.setBasename("messages");
        defaultSource.setDefaultEncoding("UTF-8");
        this.messageSource = defaultSource;
        this.resourceLoader = new DefaultResourceLoader();
    }

    @Autowired
    public Msg(MessageSource messageSource, ResourceLoader resourceLoader) {
        this.messageSource = messageSource;
        this.resourceLoader = resourceLoader;
        instance = this;
    }

    public static String get(String key) {
        return instance.resolveMessage(key, null);
    }

    public static String get(String key, Object... args) {
        return instance.resolveMessage(key, args);
    }

    protected String resolveMessage(String key, Object[] args) {
        try {
            String value = messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
            return resolveIfFile(value);
        } catch (NoSuchMessageException e) {
            return key;
        }
    }

    private String resolveIfFile(String value) {
        if (value != null && (value.startsWith("classpath:") || value.startsWith("file:"))) {
            return fileCache.computeIfAbsent(value, this::readResourceContent);
        }
        return value;
    }

    private String readResourceContent(String location) {
        try {
            Resource resource = resourceLoader.getResource(location);
            if (!resource.exists() && location.startsWith("classpath:")) {
                String path = location.substring("classpath:".length());
                resource = new org.springframework.core.io.ClassPathResource(path, Msg.class.getClassLoader());
            }
            if (!resource.exists()) {
                return get("_.f.none", location);
            }
            try (InputStream is = resource.getInputStream()) {
                return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas odczytu pliku zasobu: " + location, e);
        }
    }

}
