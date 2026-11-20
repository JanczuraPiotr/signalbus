package pl.signalbus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.PropertyPlaceholderHelper;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;

@Configuration
public class MessageConfig {

    @Value("${spring.messages.basename:file:./src/main/resources/messages,classpath:messages}")
    private String[] baseNames;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReferencingMessageSource();

        messageSource.setBasenames(baseNames);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setCacheSeconds(0);
        messageSource.setFallbackToSystemLocale(false);

        return messageSource;
    }

    private static class ReferencingMessageSource extends ReloadableResourceBundleMessageSource {
        private final PropertyPlaceholderHelper placeholders = new PropertyPlaceholderHelper("${", "}");

        @Override
        protected String resolveCodeWithoutArguments(String code, Locale locale) {
            String value = super.resolveCodeWithoutArguments(code, locale);
            return value == null ? null : placeholders.replacePlaceholders(value,
                    key -> super.resolveCodeWithoutArguments(key, locale));
        }

        @Override
        protected MessageFormat resolveCode(String code, Locale locale) {
            String value = resolveCodeWithoutArguments(code, locale);
            return value == null ? null : createMessageFormat(value, locale);
        }

    }
}
