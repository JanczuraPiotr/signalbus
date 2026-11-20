package pl.signalbus.web.vaadin.console;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;
import pl.signalbus.config.Msg;
import pl.signalbus.security.UserRole;

@Route(value = "", layout = ConsoleContainerView.class)
@RouteAlias(value = "panel", absolute = true, layout = ConsoleContainerView.class)
@RolesAllowed(UserRole.CONSOLE)
public class ConsoleDashboardView extends VerticalLayout implements ConsoleMenuProvider, HasDynamicTitle {

    public ConsoleDashboardView() {
        setSpacing(true);
        setPadding(true);

        var header = new H1(Msg.get("console.dashboard.title"));

        var mdConsole = new Markdown(Msg.get("console.readme"));

        add(header, mdConsole);
    }

    @Override
    public String getPageTitle() {
        return Msg.get("navi.console.dashboard");
    }

    @Override
    public MenuBar createMenuBar() {
        MenuBar menu = new MenuBar();

        return menu;
    }
}
