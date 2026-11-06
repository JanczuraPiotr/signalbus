package pl.signalbus.web.vaadin.console;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import pl.signalbus.config.Msg;
import pl.signalbus.security.UserRole;


@Route("console")
@RouteAlias("panel")
@RolesAllowed(UserRole.CONSOLE)
public class ConsoleView extends VerticalLayout implements HasDynamicTitle {

    public ConsoleView(AuthenticationContext authenticationContext) {

        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 title = new H2(Msg.get("console.title"));
        Paragraph desc = new Paragraph(Msg.get("console.description"));


        Button homeButton = new Button(Msg.get("app.menu.main"), e -> UI.getCurrent().getPage().setLocation("/"));
        Button logoutButton = new Button(Msg.get("app.menu.logout"), e -> authenticationContext.logout());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout actions = new HorizontalLayout(homeButton, logoutButton);
        actions.setSpacing(true);

        add(title, desc, actions);
        addClassName("console-view");
    }

    @Override
    public String getPageTitle() {
        return Msg.get("app.menu.console") + " | " + Msg.get("app.title");
    }

}
