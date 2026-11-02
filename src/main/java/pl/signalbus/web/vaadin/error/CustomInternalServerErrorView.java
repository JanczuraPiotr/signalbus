package pl.signalbus.web.vaadin.error;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;
// TODO init

@AnonymousAllowed
@PageTitle("500 - Błąd serwera | SignalBus")
public class CustomInternalServerErrorView extends VerticalLayout implements HasErrorParameter<Exception> {

    public CustomInternalServerErrorView() {
        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 code = new H1("500");
        code.getStyle().set("color", "#e53e3e").set("font-size", "3.5rem").set("margin", "0");

        H2 title = new H2("Wewnętrzny błąd serwera");
        Paragraph desc = new Paragraph("Wystąpił nieoczekiwany błąd podczas ładowania widoku. Spróbuj ponownie później.");

        Button homeButton = new Button("Strona główna", e -> UI.getCurrent().getPage().setLocation("/"));
        Button panelButton = new Button("Panel Użytkownika", e -> UI.getCurrent().navigate("panel"));
        panelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout actions = new HorizontalLayout(homeButton, panelButton);
        actions.setSpacing(true);

        add(code, title, desc, actions);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}
