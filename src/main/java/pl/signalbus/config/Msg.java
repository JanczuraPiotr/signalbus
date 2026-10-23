package pl.signalbus.config;


import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Msg {
    private static MessageSource messageSource;

    public Msg(MessageSource messageSource) {
        Msg.messageSource = messageSource;
    }


    public static String get(String key) {
        try {
            return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return key;
        }
    }

    public static String get(String key, Object... args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return key;
        }
    }
}
