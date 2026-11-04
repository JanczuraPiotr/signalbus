package pl.templates.error;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.signalbus.config.Msg;
import pl.templates.ThymeleafTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThymeleafErrorTests extends ThymeleafTestBase {

    @BeforeEach
    void setUp() {
        beforeEach();
    }

    @Test
    void error403() {
        String html = templateEngine.process("error/403", context);
        Document doc = Jsoup.parse(html);

        Elements buttonLinks = doc.select(".error-container .content .buttonGroup a");
        assertEquals(2, buttonLinks.size(), "Grupa przycisków powinna zawierać dokładnie 2 elementy linków");
        assertEquals("/", buttonLinks.get(0).attr("href"));
        assertEquals("/app/console", buttonLinks.get(1).attr("href"));

        assertEquals("403", doc.select(".error-container .content .error h1.code").text());
        assertEquals(Msg.get("http.403.title"), doc.select(".error-container .content .error h2.title").text());
        assertEquals(Msg.get("http.403.description"), doc.select(".error-container .content .error p.description").text());
    }

    @Test
    void error404() {
        String html = templateEngine.process("error/404", context);
        Document doc = Jsoup.parse(html);

        Elements buttonLinks = doc.select(".error-container .content .buttonGroup a");
        assertEquals(2, buttonLinks.size(), "Grupa przycisków powinna zawierać dokładnie 2 elementy linków");
        assertEquals("/", buttonLinks.get(0).attr("href"));
        assertEquals("/app/console", buttonLinks.get(1).attr("href"));

        assertEquals("404", doc.select(".error-container .content .error h1.code").text());
        assertEquals(Msg.get("http.404.title"), doc.select(".error-container .content .error h2.title").text());
        assertEquals(Msg.get("http.404.description"), doc.select(".error-container .content .error p.description").text());
    }

    @Test
    void error500() {
        String html = templateEngine.process("error/500", context);
        Document doc = Jsoup.parse(html);

        Elements buttonLinks = doc.select(".error-container .content .buttonGroup a");
        assertEquals(2, buttonLinks.size(), "Grupa przycisków powinna zawierać dokładnie 2 elementy linków");
        assertEquals("/", buttonLinks.get(0).attr("href"));
        assertEquals("/app/console", buttonLinks.get(1).attr("href"));

        assertEquals("500", doc.select(".error-container .content .error h1.code").text());
        assertEquals(Msg.get("http.500.title"), doc.select(".error-container .content .error h2.title").text());
        assertEquals(Msg.get("http.500.description"), doc.select(".error-container .content .error p.description").text());
    }

}
