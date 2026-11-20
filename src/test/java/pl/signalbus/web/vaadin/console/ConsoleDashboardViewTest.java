package pl.signalbus.web.vaadin.console;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.markdown.Markdown;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.signalbus.config.Msg;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("console-view")
class ConsoleDashboardViewTest {

    private ConsoleDashboardView consoleDashboardView;

    @BeforeEach
    void setUp() {
        MockVaadin.setup(new Routes().autoDiscoverViews("pl.signalbus"));

        consoleDashboardView = new ConsoleDashboardView();
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    void init_view_happyPath() {
        var navbar = consoleDashboardView.createMenuBar().getItems();
        assertNotNull(navbar);
        assertEquals(0, navbar.size());

        Optional<H1> title = consoleDashboardView.getChildren()
                                                 .filter(H1.class::isInstance)
                                                 .map(H1.class::cast)
                                                 .findFirst();

        assertTrue(title.isPresent(), "Widok powinien zawierać nagłówek H1");
        assertEquals(Msg.get("console.dashboard.title"), title.get().getText());

        Optional<Markdown> markdown = consoleDashboardView.getChildren()
                                                          .filter(Markdown.class::isInstance)
                                                          .map(Markdown.class::cast)
                                                          .findFirst();

        assertTrue(markdown.isPresent(), "Widok powinien zawierać komponent Markdown");
        assertEquals(Msg.get("console.readme"), markdown.get().getContent());

        assertEquals(2, consoleDashboardView.getChildren().toList().size());
    }

}
