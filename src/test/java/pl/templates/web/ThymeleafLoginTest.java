package pl.templates.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.signalbus.config.Msg;
import pl.templates.ThymeleafTestBase;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThymeleafLoginTest extends ThymeleafTestBase {

    @BeforeEach
    void setUp() {
        beforeEach();
    }

    @Test
    void view_hp() {
        context.setVariable("param", Map.of());
        String html = templateEngine.process("web/login", context);
        Document doc = Jsoup.parse(html);

        assertEquals(Msg.get("login.title"), doc.select("main .apla form h1").text());
        assertTrue(doc.select("main .apla form p").isEmpty());
        assertEquals(Msg.get("login.submit"), doc.select("main .apla form button[type=submit]").text());
    }

    @Test
    void view_with_error() {
        context.setVariable("param", Map.of("error", new String[]{"true"}));

        String html = templateEngine.process("web/login", context);
        Document doc = Jsoup.parse(html);

        assertEquals(Msg.get("login.title"), doc.select("main .apla form h1").text());
        assertEquals(Msg.get("login.error"), doc.select("main .apla form p").text());
        assertEquals(Msg.get("login.submit"), doc.select("main .apla form button[type=submit]").text());
    }

    @Test
    void view_with_logout() {
        context.setVariable("param", Map.of("logout", new String[]{"true"}));

        String html = templateEngine.process("web/login", context);
        Document doc = Jsoup.parse(html);

        assertEquals(Msg.get("login.title"), doc.select("main .apla form h1").text());
        assertEquals(Msg.get("login.logout"), doc.select("main .apla form p").text());
        assertEquals(Msg.get("login.submit"), doc.select("main .apla form button[type=submit]").text());
    }
}
