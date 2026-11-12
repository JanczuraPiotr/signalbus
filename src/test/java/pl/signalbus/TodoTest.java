package pl.signalbus;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import pl.signalbus.config.Msg;

public class TodoTest {

    protected void todo(String message) {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        System.err.println(AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, message));
        Assumptions.abort("TODO: " + message);
    }

    protected void todo(String message, String... reasons) {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        System.err.println(AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, message));

        if (reasons != null) {
            int i = 1;
            for (String reason : reasons) {
                System.err.println(AnsiOutput.toString(AnsiColor.GREEN, "\t" + reason));
            }
        }

        Assumptions.abort("TODO: " + message);
    }

    @Test
    public void generowanieKonsoli() {
        todo("Generowanie UUID dla konsoli",
             "Ograniczenie ilości tworzonych konsoli z tego samego IP",
             "Ograniczenie ilości tworzonych konsoli w jednostce czasu"
        );
    }

    @Test
    public void generowanieMagistrali() {
        todo("Utwórz generowanie UUID dla magistrali",
             "Ograniczenie ilości tworzonych magistrali w jednostce czasu",
             "Ograniczenie ilości tworzonych magistrali z tej samej konsoli"
             );
    }

    @Test
    public void przypisanieZmiennychDoMagistrali() {
        todo("Ograniczenie ilości zmiennych dla magistrali w jednostce czasu",
             "Ograniczenie ilości zmiennych dla magistrali w tej samej konsoli"
             );
    }

    @Test
    public void usuwaniePustejKonsoli() {
        todo("Automatyczne usuwanie pustej konsoli.",
             "Automatycznie można usunąć tylko konsolę, do której nie ma przypiętej magistrali",
             "Uwzględniany musi być czas od usunięcia ostatniej magistrali"
        );
    }

    @Test
    public void usuwaniaNieaktywnejMagistrali() {
        todo("Czas usuwania nieaktywnej magistrali 1 miesiąc?",
             "Uwzględniany musi być odczyt i zapis",
             "Nigdy nie utworzono dla niej zmiennych"
        );
    }

    @Test
    public void usuwanieNieaktywnegoUrzadzenia() {
        todo("Usuwanie nieaktywnego urządzenia",
             "Czas usuwania nieaktywnego urządzenia 1 miesiąc?",
             "Uwzględniany musi być odczyt i zapis"
        );
    }

    @Test
    public void rules_basic_000() {
        todo("rules.basic.000", Msg.get("rules.basic.000"));
    }

    @Test
    public void rules_basic_001() {
        todo("rules.basic.001", Msg.get("rules.basic.001"));
    }

    @Test
    public void rules_basic_002() {
        todo("rules.basic.002", Msg.get("rules.basic.002"));
    }

    @Test
    public void rules_basic_003() {
        todo("rules.basic.003", Msg.get("rules.basic.003"));
    }

    @Test
    public void rules_console_001() {
        todo("rules.console.001", Msg.get("rules.console.001"));
    }

    @Test
    public void rules_console_002() {
        todo("rules.console.002", Msg.get("rules.console.002"));
    }

    @Test
    public void rules_console_003() {
        todo("rules.console.003", Msg.get("rules.console.003"));
    }

    @Test
    public void rules_console_004() {
        todo("rules.console.004", Msg.get("rules.console.004"));
    }

    @Test
    public void rules_console_creator_001() {
        todo("rules.console.creator.001", Msg.get("rules.console.creator.001"));
    }

    @Test
    public void rules_console_creator_002() {
        todo("rules.console.creator.002" ,Msg.get("rules.console.creator.002"));
    }

    @Test
    public void rules_console_creator_003() {
        todo("rules.console.creator.003", Msg.get("rules.console.creator.003"));
    }

    @Test
    public void rules_console_creator_004() {
        todo("rules.console.creator.004", Msg.get("rules.console.creator.004"));
    }

    @Test
    public void rules_console_creator_005() {
        todo("rules.console.creator.005", Msg.get("rules.console.creator.005"));
    }

    @Test
    public void rules_console_creator_006() {
        todo("rules.console.creator.006", Msg.get("rules.console.creator.006"));
    }

    @Test
    public void rules_magistrala_001() {
        todo("rules.magistrala.001", Msg.get("rules.magistrala.001"));
    }

    @Test
    public void rules_magistrala_002() {
        todo("rules.magistrala.002", Msg.get("rules.magistrala.002"));
    }

    @Test
    public void rules_magistrala_003() {
        todo("rules.magistrala.003", Msg.get("rules.magistrala.003"));
    }

    @Test
    public void rules_magistrala_004() {
        todo("rules.magistrala.004", Msg.get("rules.magistrala.004"));
    }

    @Test
    public void rules_magistrala_005() {
        todo("rules.magistrala.005", Msg.get("rules.magistrala.005"));
    }

    @Test
    public void rules_magistrala_006() {
        todo("rules.magistrala.006", Msg.get("rules.magistrala.006"));
    }


}
