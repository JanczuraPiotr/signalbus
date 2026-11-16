package pl.templates.web;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.signalbus.config.Msg;
import pl.templates.ThymeleafTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ThymeleafHomeTest extends ThymeleafTestBase {

    @BeforeEach
    void setUp() {
        beforeEach();
    }

    @Test
    void index_hp() {

        String html = templateEngine.process("web/index", context);
        Document doc = Jsoup.parse(html);

        Elements navLinks = doc.select("header nav a.nav-item");
        assertEquals(1, navLinks.size(), "Nawigacja powinna zawierać dokładnie 3 elementy linków");
        assertEquals("/app/console", navLinks.get(0).attr("href"));

        var main = doc.select("main");

        assertEquals(Msg.get("home.greeting"), main.select("h1").text());

        assertEquals(Msg.get("home.privacy_policy"), main.select("p#privacy_policy").text());
        assertEquals(Msg.get("home.cookies"), main.select("p#cookies").text());
        assertEquals(Msg.get("home.prototyp"), main.select("p#prototyp").text());


        var description  = main.select("div#description");
        assertEquals(Msg.get("home.description"), description.select("h2").text());

        var  descriptionLi = description.select("ul li");
        assertEquals(Msg.get("home.description.1"), descriptionLi.get(0).text());
        assertEquals(Msg.get("home.description.2"), descriptionLi.get(1).text());

        Elements rules = main.select("div#rules");
        assertNotEquals(0, rules.size(), "Brak informacji kreatora konsoli");
        assertEquals(Msg.get("rules.title"), rules.select("h2").text(), "Brak tytułu bloku zasad");
        assertEquals(Msg.get("rules.basic"), rules.select("h4").text(), "Brak informacji kreatora konsoli");

        var rulesLi = rules.select("ul li");
        assertNotEquals(0, rulesLi.size(), "Brak informacji kreatora konsoli");

        assertEquals(Msg.get("home.write.me"), main.select("h4#write-me").text());

        assertEquals("", doc.select("footer").text());
    }

}
