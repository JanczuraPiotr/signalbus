package pl.signalbus.web.vaadin.console;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.signalbus.config.Msg;
import tool.Action;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsoleViewTest {

    private AuthenticationContext authenticationContext;
    private ConsoleView consoleView;

    @BeforeEach
    void setUp() {
        authenticationContext = mock(AuthenticationContext.class);

        UI ui = new UI();
        UI.setCurrent(ui);

        consoleView = new ConsoleView(authenticationContext);
    }

    @Test
    void shouldContainCorrectVisualElements() {

        Optional<H1> title = ComponentUtil.streamDescendants(consoleView)
                                          .filter(H1.class::isInstance)
                                          .map(H1.class::cast)
                                          .findFirst();

        assertTrue(title.isPresent(), "Widok powinien zawierać nagłówek H1");
        assertEquals(Msg.get("console.title"), title.get().getText());
    }

    @Test
    void shouldTriggerLogoutWhenLogoutButtonClicked() {
        SideNavItem logoutNavi = findNaviByText(Msg.get("app.navi.logout"));
        assertNotNull(logoutNavi, "Przycisk '"+Msg.get("app.navi.logout")+"' powinien istnieć");

        Action.click(logoutNavi);

        verify(authenticationContext, times(1)).logout();
    }

    @Test
    void shouldHaveHomeButtonPresent() {
        SideNavItem homeNavi = findNaviByText(Msg.get("app.navi.main"));
        assertNotNull(homeNavi, "Przycisk '"+Msg.get("app.navi.main")+"' powinien istnieć");
    }

    private SideNavItem findNaviByText(String text) {
        return ComponentUtil.streamDescendants(consoleView)
                             .filter(SideNavItem.class::isInstance)
                             .map(SideNavItem.class::cast)
                             .filter(b -> text.equals(b.getLabel()))
                             .findFirst()
                             .orElse(null);
    }


}
