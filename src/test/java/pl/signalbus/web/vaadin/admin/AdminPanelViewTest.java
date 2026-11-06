package pl.signalbus.web.vaadin.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.signalbus.config.Msg;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminPanelViewTest {

    private AuthenticationContext authenticationContext;
    private AdminPanelView adminPanelView;

    @BeforeEach
    void setUp() {
        authenticationContext = mock(AuthenticationContext.class);

        UI ui = new UI();
        UI.setCurrent(ui);

        adminPanelView = new AdminPanelView(authenticationContext);
    }

    @Test
    void shouldContainCorrectVisualElements() {
        Optional<H2> title = adminPanelView.getChildren()
                                           .filter(H2.class::isInstance)
                                           .map(H2.class::cast)
                                           .findFirst();

        assertTrue(title.isPresent(), "Widok powinien zawierać nagłówek H2");
        assertEquals(Msg.get("admin.title"), title.get().getText());

        Optional<Paragraph> desc = adminPanelView.getChildren()
                                                 .filter(Paragraph.class::isInstance)
                                                 .map(Paragraph.class::cast)
                                                 .findFirst();

        assertTrue(desc.isPresent(), "Widok powinien zawierać paragraf z opisem");
        assertEquals(Msg.get("admin.description"), desc.get().getText());
    }

    @Test
    void shouldTriggerLogoutWhenLogoutButtonClicked() {
        Button logoutButton = findButtonByText(Msg.get("app.navi.logout"));
        assertNotNull(logoutButton, "Przycisk '"+Msg.get("app.navi.logout")+"' powinien istnieć");

        logoutButton.click();

        verify(authenticationContext, times(1)).logout();
    }

    @Test
    void shouldHaveHomeButtonPresent() {
        Button homeButton = findButtonByText(Msg.get("app.navi.main"));
        assertNotNull(homeButton, "Przycisk powrotu powinien istnieć");
    }

    private Button findButtonByText(String text) {
        return adminPanelView.getChildren()
                             .flatMap(component -> {
                                 if (component instanceof com.vaadin.flow.component.HasComponents) {
                                     return ((com.vaadin.flow.component.HasComponents) component).getChildren();
                                 }
                                 return java.util.stream.Stream.of(component);
                             })
                             .filter(Button.class::isInstance)
                             .map(Button.class::cast)
                             .filter(b -> text.equals(b.getText()))
                             .findFirst()
                             .orElse(null);
    }
}
