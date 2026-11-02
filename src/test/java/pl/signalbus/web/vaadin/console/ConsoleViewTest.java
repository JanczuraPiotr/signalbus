package pl.signalbus.web.vaadin.console;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// TODO init

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
        // Weryfikacja obecności nagłówka H2
        Optional<H2> title = consoleView.getChildren()
                                        .filter(H2.class::isInstance)
                                        .map(H2.class::cast)
                                        .findFirst();

        assertTrue(title.isPresent(), "Widok powinien zawierać nagłówek H2");
        assertEquals("Konsola (Vaadin)", title.get().getText());

        // Weryfikacja obecności opisu Paragraph
        Optional<Paragraph> desc = consoleView.getChildren()
                                              .filter(Paragraph.class::isInstance)
                                              .map(Paragraph.class::cast)
                                              .findFirst();

        assertTrue(desc.isPresent(), "Widok powinien zawierać paragraf z opisem");
        assertEquals("Konsola", desc.get().getText());
    }

    @Test
    void shouldTriggerLogoutWhenLogoutButtonClicked() {
        // Znalezienie przycisku wylogowania po tekście
        Button logoutButton = findButtonByText("Wyloguj");
        assertNotNull(logoutButton, "Przycisk 'Wyloguj' powinien istnieć");

        // Symulacja kliknięcia przycisku
        logoutButton.click();

        // Weryfikacja, czy metoda logout() została wywołana na kontekście bezpieczeństwa
        verify(authenticationContext, times(1)).logout();
    }

    @Test
    void shouldHaveHomeButtonPresent() {
        // Znalezienie przycisku powrotu
        Button homeButton = findButtonByText("Wróć do strony głównej");
        assertNotNull(homeButton, "Przycisk powrotu powinien istnieć");
    }

    private Button findButtonByText(String text) {
        return consoleView.getChildren()
                          .flatMap(component -> {
                              // Jeśli komponent ma dzieci (np. HorizontalLayout), pobierz je
                              if (component instanceof com.vaadin.flow.component.HasComponents) {
                                  return ((com.vaadin.flow.component.HasComponents) component).getChildren();
                              }
                              // W przeciwnym razie zwróć strumień z samym komponentem
                              return java.util.stream.Stream.of(component);
                          })
                          .filter(Button.class::isInstance)
                          .map(Button.class::cast)
                          .filter(b -> text.equals(b.getText()))
                          .findFirst()
                          .orElse(null);
    }

}
