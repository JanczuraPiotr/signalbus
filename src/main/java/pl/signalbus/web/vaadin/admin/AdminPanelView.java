package pl.signalbus.web.vaadin.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import pl.signalbus.security.UserRole;
// TODO init
@Route("admin")
@PageTitle("Panel Administratora | SignalBus")
@RolesAllowed(UserRole.ADMIN)
public class AdminPanelView extends VerticalLayout {

    public AdminPanelView(AuthenticationContext authenticationContext) {
        setSpacing(true);
        setPadding(true);
        setSizeFull();

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 title = new H2("Panel Administratora (Vaadin)");
        Paragraph desc = new Paragraph("Strefa chroniona. Ten panel oraz wszystkie elementy w tym pakiecie wymagają zalogowania.");

        Button homeButton = new Button("Strona główna", e -> UI.getCurrent().getPage().setLocation("/"));
        Button logoutButton = new Button("Wyloguj", e -> authenticationContext.logout());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout actions = new HorizontalLayout(homeButton, logoutButton);
        actions.setSpacing(true);

        add(title, desc, actions);
    }
}
