package pl.signalbus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource(@Value("${spring.messages.basename:file:./src/main/resources/messages,classpath:messages}") String basename) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        String[] basenames = Arrays.stream(basename.split(","))
                                   .map(String::trim)
                                   .filter(s -> !s.isEmpty())
                                   .toArray(String[]::new);

        messageSource.setBasenames(basenames);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setCacheSeconds(0);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}