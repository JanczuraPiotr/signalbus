package pl.signalbus.web.vaadin.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.signalbus.config.Msg;

@Tag("console-view")
public class SettingsViewTest {

    private SettingsView settingsView;

    @BeforeEach
    void setUp() {
        UI ui = new UI();
        UI.setCurrent(ui);

        settingsView = new SettingsView();
    }

    @Test
    void init_view_happyPath() {
        var navbar = settingsView.createMenuBar().getItems();
        assertNotNull(navbar);
        assertEquals(1, navbar.size());
        assertEquals(Msg.get("action.console.settings.export"), navbar.getFirst().getText());

        var children = settingsView.getChildren().toList();
        assertEquals(2, children.size());
        assertTrue(children.get(0) instanceof H1, "Widok powinien zawierać nagłówek H1");
        assertEquals(Msg.get("console.settings.title"), ((H1) children.get(0)).getText());
        assertTrue(children.get(1) instanceof FormLayout, "Pod nagłówkiem powinien znajdować się formularz");

        var formChildren = children.get(1).getChildren().toList();
        assertEquals(2, formChildren.size());
        assertTrue(formChildren.get(0) instanceof Button);
        assertEquals(Msg.get("action.console.settings.save"), ((Button) formChildren.get(0)).getText());
        assertTrue(formChildren.get(1) instanceof Button);
        assertEquals(Msg.get("action.console.settings.cancel"), ((Button) formChildren.get(1)).getText());
    }

}
