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

@Route(value = "device", layout = ConsoleContainerView.class)
@RolesAllowed(UserRole.CONSOLE)
public class DeviceView extends VerticalLayout implements ConsoleMenuProvider, HasDynamicTitle {

    public DeviceView() {
        setSpacing(true);
        setPadding(true);

        H1 header = new H1(Msg.get("console.device.title"));

        add(header);
    }

    @Override
    public String getPageTitle() {
        return Msg.get("navi.console.device");
    }

    @Override
    public MenuBar createMenuBar() {
        MenuBar menu = new MenuBar();
        menu.setWidthFull();
        menu.addItem(Msg.get("action.console.device.add"), event -> Notification.show("Przykładowa akcja: dodaj urządzenie"));
        return menu;
    }

}
