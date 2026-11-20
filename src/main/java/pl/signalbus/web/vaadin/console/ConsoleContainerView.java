package pl.signalbus.web.vaadin.console;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RoutePrefix;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import pl.signalbus.config.Msg;
import pl.signalbus.security.UserRole;


@RoutePrefix("console")
@RolesAllowed(UserRole.CONSOLE)
public class ConsoleContainerView extends AppLayout implements AfterNavigationObserver {

    private static final String NAVBAR_TITLE_FONT_SIZE = "var(--lumo-font-size-l)";

    private final AuthenticationContext authenticationContext;
    private final Div viewMenu = new Div();
    private final H1 title = new H1();

    public ConsoleContainerView(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;

        var toggle = new DrawerToggle();
        title.setText(Msg.get("console.title"));
        title.getStyle()
                .set("font-size", NAVBAR_TITLE_FONT_SIZE)
                .set("margin", "0");

        Scroller scroller = new Scroller(createSideNav());
        scroller.setSizeFull();
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);

        addToDrawer(scroller);
        viewMenu.getStyle()
                .set("margin-inline-start", "var(--lumo-space-l)")
                .set("flex", "1")
                .set("min-width", "0");
        viewMenu.add(new MenuBar());

        addToNavbar(toggle, title, viewMenu);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        title.setText(getContent() instanceof HasDynamicTitle titledView
                ? titledView.getPageTitle()
                : Msg.get("console.title"));
        viewMenu.removeAll();
        if (getContent() instanceof ConsoleMenuProvider menuProvider) {
            viewMenu.add(menuProvider.createMenuBar());
        }
    }


    private SideNav createSideNav() {

        var nav = new SideNav();

        nav.addItem(new SideNavItem(Msg.get("navi.console.dashboard"),
                                    ConsoleDashboardView.class,
                                    VaadinIcon.DASHBOARD.create()));

        var itemMonitoring = new SideNavItem(Msg.get("navi.console.monitoring"),
                                             MonitoringView.class,
                                             VaadinIcon.DESKTOP.create());
        nav.addItem(itemMonitoring);

        var itemDevices = new SideNavItem(Msg.get("navi.console.device"),
                                          DeviceView.class,
                                          VaadinIcon.COGS.create());
        nav.addItem(itemDevices);

        var itemMagistrala = new SideNavItem(Msg.get("navi.console.magistrala"),
                                             MagistralaView.class,
                                             VaadinIcon.SPLIT.create());
        nav.addItem(itemMagistrala);

        var itemSettings = new SideNavItem(Msg.get("navi.console.settings"),
                                           SettingsView.class,
                                           VaadinIcon.COG.create());
        nav.addItem(itemSettings);

        var itemHome = new SideNavItem(Msg.get("navi.main"),
                                       "/",
                                       VaadinIcon.HOME.create());
        itemHome.getElement(). addEventListener("click",  e -> UI.getCurrent().getPage().setLocation("/"));
        nav.addItem(itemHome);

        var itemLogout = new SideNavItem(Msg.get("navi.logout"));
        itemLogout.setPrefixComponent(VaadinIcon.SIGN_OUT.create());
        itemLogout.getElement(). addEventListener("click",  e -> authenticationContext.logout());
        nav.addItem(itemLogout);

        return nav;
    }
}
