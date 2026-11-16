package pl.signalbus.web.vaadin.console;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
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
public class ConsoleView extends AppLayout implements HasDynamicTitle {

    private final AuthenticationContext authenticationContext;

    public ConsoleView(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;

        var toggle = new DrawerToggle();
        var title = new H1(Msg.get("console.title"));

        Scroller scroller = new Scroller(createSideNav());
        scroller.setSizeFull();
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);

        addToDrawer(scroller);
        addToNavbar(toggle, title);
    }


    @Override
    public String getPageTitle() {
        return Msg.get("app.navi.console") + " | " + Msg.get("app.title");
    }


    private SideNav createSideNav() {

        var nav = new SideNav();

        var itemHome = new SideNavItem(Msg.get("app.navi.main"), "/");
        itemHome.getElement(). addEventListener("click",  e -> UI.getCurrent().getPage().setLocation("/"));
        nav.addItem(itemHome);

        var itemLogout = new SideNavItem(Msg.get("app.navi.logout"));
        itemLogout.getElement(). addEventListener("click",  e -> authenticationContext.logout());
        nav.addItem(itemLogout);

        return nav;
    };
}
