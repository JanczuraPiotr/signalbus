package pl.signalbus.web.vaadin.console;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("console-view")
public class ConsoleContainerViewTest {

    private AuthenticationContext authenticationContext;

    @BeforeEach
    void setUp() {
        MockVaadin.setup(new Routes().autoDiscoverViews("pl.signalbus"));
        authenticationContext = mock(AuthenticationContext.class);
        when(authenticationContext.isAuthenticated()).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    void init_happyPath() {

        // WHEN: Tworzymy widok
        ConsoleContainerView consoleContainerView = new ConsoleContainerView(authenticationContext);

        // THEN 1: Weryfikacja paska górnego (slot="navbar")
        List<Component> navbarComponents = consoleContainerView.getChildren()
                                                               .filter(c -> "navbar".equals(c.getElement().getAttribute("slot")))
                                                               .toList();

        assertEquals(3, navbarComponents.size(), "Navbar zalogowanego użytkownika powinien mieć 3 elementy");
        assertInstanceOf(DrawerToggle.class, navbarComponents.get(0));

        Component title = navbarComponents.get(1);
        assertFalse(title.getElement().getText().isBlank());
        assertEquals("0", title.getElement().getStyle().get("margin"));

        Component viewMenu = navbarComponents.get(2);
        assertEquals("1", viewMenu.getElement().getStyle().get("flex"));
        assertEquals("0", viewMenu.getElement().getStyle().get("min-width"));

        assertEquals(1, viewMenu.getChildren().toList().size());

        // THEN 2: Weryfikacja panelu bocznego (slot="drawer") i zagnieżdżenia Scroller -> SideNav
        List<Component> drawerComponents = consoleContainerView.getChildren()
                                                               .filter(c -> "drawer".equals(c.getElement().getAttribute("slot")))
                                                               .toList();

        assertEquals(1, drawerComponents.size(), "Drawer powinien zawierać dokładnie 1 kontener");
        Scroller scroller = assertInstanceOf(Scroller.class, drawerComponents.get(0));
        assertEquals(Scroller.ScrollDirection.VERTICAL, scroller.getScrollDirection());

        SideNav sideNav = assertInstanceOf(SideNav.class, scroller.getContent(),
                                           "Wewnątrz Scrollera musi być zagnieżdżony SideNav");
        assertFalse(sideNav.getItems().isEmpty(), "SideNav powinien zawierać pozycje menu");

        assertEquals(4, consoleContainerView.getChildren().toList().size());
    }

}
