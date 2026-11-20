package pl.signalbus.web.vaadin.console;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import pl.signalbus.config.Msg;
import pl.signalbus.security.UserRole;

@Route(value = "settings", layout = ConsoleContainerView.class)
@RolesAllowed(UserRole.CONSOLE)
public class SettingsView extends VerticalLayout implements ConsoleMenuProvider, HasDynamicTitle {

    public SettingsView() {
        FormLayout form = new FormLayout();
        form.add(new Button(Msg.get("action.console.settings.save"), event -> Notification.show("Przykładowa akcja: zapisz ustawienia")),
                 new Button(Msg.get("action.console.settings.cancel"), event -> Notification.show("Przykładowa akcja: anuluj ustawienia")));

        var header = new H1(Msg.get("console.settings.title"));
        add(header, form);
    }

    @Override
    public String getPageTitle() {
        return Msg.get("navi.console.settings");
    }

    @Override
    public MenuBar createMenuBar() {
        MenuBar menu = new MenuBar();
        menu.addItem(Msg.get("action.console.settings.export"), event -> Notification.show("Przykładowa akcja: eksportuj ustawienia"));
        return menu;
    }
}
