package pl.signalbus.web.vaadin.console;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.signalbus.config.Msg;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("console-view")
class MagistralaViewTest {

    private MagistralaView magistralaView;

    @BeforeEach
    void setUp() {
        UI ui = new UI();
        UI.setCurrent(ui);

        magistralaView = new MagistralaView();
    }

    @Test
    void init_view_happyPath() {
        var navbar = magistralaView.createMenuBar().getItems();
        assertNotNull(navbar);
        assertEquals(1, navbar.size());
        assertEquals(Msg.get("console.magistrala.add"), navbar.get(0).getText());


        Optional<H1> title = ComponentUtil.streamDescendants(magistralaView)
                                          .filter(H1.class::isInstance)
                                          .map(H1.class::cast)
                                          .findFirst();

        assertTrue(title.isPresent(), "Widok powinien zawierać nagłówek H1");
        assertEquals(Msg.get("console.magistrala.title"), title.get().getText());

        assertEquals(1, magistralaView.getChildren().toList().size());
    }

}
