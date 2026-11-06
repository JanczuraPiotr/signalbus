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
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;
import pl.signalbus.config.Msg;

@AnonymousAllowed
public class CustomNotFoundView extends VerticalLayout implements HasErrorParameter<NotFoundException>, HasDynamicTitle {

    private final Paragraph errorMessage;

    public CustomNotFoundView() {
        setSpacing(true);
        setPadding(true);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 code = new H1("404");
        code.getStyle().set("color", "#dd6b20").set("font-size", "3.5rem").set("margin", "0");

        H2 title = new H2(Msg.get("http.404.title"));
        Paragraph desc = new Paragraph(Msg.get("http.404.description"));
        errorMessage = new Paragraph();
        errorMessage.getStyle().set("color", "#718096").set("font-size", "0.9rem");

        Button homeButton = new Button(Msg.get("app.navi.main"), e -> UI.getCurrent().getPage().setLocation("/"));
        Button panelButton = new Button(Msg.get("app.navi.back"), e -> UI.getCurrent().getPage().getHistory().back());
        panelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout actions = new HorizontalLayout(homeButton, panelButton);
        actions.setSpacing(true);

        add(code, title, desc, errorMessage, actions);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        if (parameter.hasCustomMessage()) {
            errorMessage.setText(parameter.getCustomMessage());
        }
        return HttpServletResponse.SC_NOT_FOUND;
    }

    @Override
    public String getPageTitle() {
        return getTranslation("error.404.title") + " | " + Msg.get("app.name");
    }
}
