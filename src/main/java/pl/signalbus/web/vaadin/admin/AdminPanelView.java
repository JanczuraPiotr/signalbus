package pl.signalbus.web.vaadin.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import pl.signalbus.config.Msg;
import pl.signalbus.security.UserRole;


@Route("admin")
@RolesAllowed(UserRole.ADMIN)
public class AdminPanelView extends VerticalLayout implements HasDynamicTitle {

    public AdminPanelView(AuthenticationContext authenticationContext) {

        setSpacing(true);
        setPadding(true);
        setSizeFull();

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 title = new H2(Msg.get("admin.title"));
        Paragraph desc = new Paragraph(Msg.get("admin.description"));

        Button homeButton = new Button(Msg.get("app.navi.main"), e -> UI.getCurrent().getPage().setLocation("/"));
        Button logoutButton = new Button(Msg.get("app.navi.logout"), e -> authenticationContext.logout());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout actions = new HorizontalLayout(homeButton, logoutButton);
        actions.setSpacing(true);

        add(title, desc, actions);
        addClassName("admin-panel-view");

    }

    @Override
    public String getPageTitle() {
        return Msg.get("admin.title") + " | " + Msg.get("app.title");
    }
}
