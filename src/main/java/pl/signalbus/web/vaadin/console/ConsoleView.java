package pl.signalbus.web.vaadin.console;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import pl.signalbus.security.UserRole;
// TODO init
@Route("console")
@RouteAlias("panel")
@PageTitle("Panel Użytkownika | SignalBus")
@RolesAllowed(UserRole.CONSOLE)
public class ConsoleView extends VerticalLayout {

    public ConsoleView(AuthenticationContext authenticationContext) {
        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 title = new H2("Konsola (Vaadin)");
        Paragraph desc = new Paragraph("Konsola");

        Button homeButton = new Button("Wróć do strony głównej", e -> UI.getCurrent().getPage().setLocation("/"));
        Button logoutButton = new Button("Wyloguj", e -> authenticationContext.logout());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout actions = new HorizontalLayout(homeButton, logoutButton);
        actions.setSpacing(true);

        add(title, desc, actions);
    }
}
