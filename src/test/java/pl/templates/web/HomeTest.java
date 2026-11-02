package pl.templates.web;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import pl.signalbus.config.Msg;
import pl.templates.ThymeleafTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HomeTest extends ThymeleafTestBase {

    @BeforeEach
    void setUp() {
        beforeEach();
    }

    @Test
    void index_hp() {

        String html = templateEngine.process("web/index", context);
        Document doc = Jsoup.parse(html);

        Elements navLinks = doc.select("header nav a.nav-item");
        assertEquals(3, navLinks.size(), "Nawigacja powinna zawierać dokładnie 3 elementy linków");
        assertEquals("/console-creator", navLinks.get(0).attr("href"));
        assertEquals("/app/console", navLinks.get(1).attr("href"));
        assertEquals("/app/admin", navLinks.get(2).attr("href"));

        assertEquals(Msg.get("home.greeting"), doc.select("main h1").text());
        assertEquals("", doc.select("footer").text());
    }

}
