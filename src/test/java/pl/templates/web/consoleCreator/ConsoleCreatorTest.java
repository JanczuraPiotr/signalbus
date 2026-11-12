package pl.templates.web.consoleCreator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.signalbus.config.Msg;
import pl.templates.ThymeleafTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ConsoleCreatorTest extends ThymeleafTestBase {

    @BeforeEach
    void setUp() {
        beforeEach();
    }

    @Test
    void shouldRenderConsoleCreatorPageWithWarnings() {
        String html = templateEngine.process("web/console-creator/index", context);
        Document doc = Jsoup.parse(html);

        var main = doc.select("main");

        var greeting = main.select("h1");
        assertEquals(Msg.get("rules.console.creator.greeting"), greeting.text(), "Brak tekstu powitalnego");

        var description = main.select("p#description");
        assertEquals(Msg.get("rules.console.creator.description"), description.text(), "Brak tekstu opisu");

        var rules = main.select("div#rules");
        assertNotEquals(0, rules.size(), "Brak opisu zasad konsoli");

        var rulesConsole = rules.select("div#rules-console");
        assertNotEquals(0, rulesConsole.size(), "Brak zasad konsoli");
        assertEquals(Msg.get("rules.title"), rulesConsole.select("h3").text(), "Brak nagłówka zasad konsoli");
        assertNotEquals(0, rulesConsole.select("ul li").size(), "Brak zasad konsoli");


        var rulesConsoleCreator = rules.select("div#rules-console-creator");
        assertNotEquals(0, rulesConsoleCreator.size(), "Brak zasad kreatora konsoli");
        assertEquals(Msg.get("rules.console.creator.title"),
                     rulesConsoleCreator.select("h3").text(),
                     "Brak nagłówka zasad kreatora konsoli");
        assertNotEquals(0, rulesConsoleCreator.select("ul li").size(), "Brak zasad kreatora konsoli");

    }

}
