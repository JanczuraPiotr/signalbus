package pl.signalbus.web.vaadin.console;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.signalbus.config.Msg;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("console-view")
public class MonitoringViewTest {

    private MonitoringView monitoringView;

    @BeforeEach
    void setUp() {
        MockVaadin.setup(new Routes().autoDiscoverViews("pl.signalbus"));

        monitoringView = new MonitoringView();
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void init_happyPath() {
        var navbar = monitoringView.createMenuBar().getItems();
        assertNotNull(navbar);
        assertEquals(3, navbar.size());
        assertEquals(Msg.get("navi.console.monitoring.device"), navbar.get(0).getText());
        assertEquals(Msg.get("navi.console.monitoring.magistrala"), navbar.get(1).getText());
        assertEquals(Msg.get("navi.console.monitoring.variable"), navbar.get(2).getText());

        Optional<H1> title = monitoringView.getChildren()
                                       .filter(H1.class::isInstance)
                                       .map(H1.class::cast)
                                       .findFirst();
        assertTrue(title.isPresent(), "Widok powinien zawierać nagłówek H1");
        assertEquals(Msg.get("console.monitoring.title"), title.get().getText());


        assertEquals(1, monitoringView.getChildren().toList().size());

    }
}
