package pl.signalbus.web.vaadin.console;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import pl.signalbus.config.Msg;
import pl.signalbus.security.UserRole;

@Route(value = "monitoring", layout = ConsoleContainerView.class)
@RolesAllowed(UserRole.CONSOLE)
public class MonitoringView extends VerticalLayout implements ConsoleMenuProvider, HasDynamicTitle{

    public MonitoringView() {
        setSpacing(true);
        setPadding(true);

        H1 header = new H1(Msg.get("console.monitoring.title"));

        add(header);
    }

    @Override
    public String getPageTitle() {
        return Msg.get("navi.console.monitoring");
    }

    @Override
    public MenuBar createMenuBar() {
        MenuBar menu = new MenuBar();
        menu.setWidthFull();
        menu.addItem(Msg.get("navi.console.monitoring.device"), event -> Notification.show("Pokaż widok monitora urządzenia"));
        menu.addItem(Msg.get("navi.console.monitoring.magistrala"), event -> Notification.show("Pokaż widok monitora magistrali"));
        menu.addItem(Msg.get("navi.console.monitoring.variable"), event -> Notification.show("Pokaż widok monitora magistrali"));
        return menu;
    }

}
