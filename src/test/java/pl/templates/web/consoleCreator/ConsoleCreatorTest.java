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
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void shouldRenderCreatorFormPage() {
        String testUuid = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d";
        context.setVariable("uuid", testUuid);

        String html = templateEngine.process("web/console-creator/creator", context);
        Document doc = Jsoup.parse(html);

        var form = doc.select("main .apla form");
        assertNotEquals(0, form.size(), "Brak formularza");
        assertEquals("/console-creator/creator", form.attr("action"));
        assertEquals("post", form.attr("method").toLowerCase());

        assertEquals(Msg.get("console.creator.title"), form.select("h1").text());

        var usernameInput = form.select("input[name=username]");
        assertEquals(1, usernameInput.size(), "Brak pola na username/UUID");
        assertEquals(testUuid, usernameInput.val());

        var passwordInput = form.select("input[name=password][type=password]");
        assertEquals(1, passwordInput.size(), "Brak pierwszego pola na hasło");

        var passwordRepeatInput = form.select("input[name=passwordRepeat][type=password]");
        assertEquals(1, passwordRepeatInput.size(), "Brak drugiego pola na powtórzenie hasła");

        var submitButton = form.select("button[type=submit]");
        assertEquals(1, submitButton.size(), "Brak przycisku wysyłającego formularz");
        assertEquals(Msg.get("console.creator.submit"), submitButton.text());

        var script = doc.select("main script");
        assertEquals(1, script.size(), "Brak skryptu do zapisu poświadczeń");
        assertTrue(script.html().contains("PasswordCredential"));
    }

    @Test
    void shouldRenderCreatorFormPageWithError() {
        String testUuid = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d";
        String errorMsg = Msg.get("console.creator.error.passwordMismatch");
        context.setVariable("uuid", testUuid);
        context.setVariable("error", errorMsg);

        String html = templateEngine.process("web/console-creator/creator", context);
        Document doc = Jsoup.parse(html);

        var errorP = doc.select("main .apla form p.error-msg");
        assertEquals(1, errorP.size(), "Brak komunikatu o błędzie");
        assertEquals(errorMsg, errorP.text());
    }

}
