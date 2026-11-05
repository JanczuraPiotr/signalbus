package pl.signalbus.web.vaadin.error;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.signalbus.SignalbusApplication;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@Tag("vaadin")
@SpringBootTest(classes = SignalbusApplication.class)
public class ErrorHandlingTests {

    @Test
    void vaadinNotFoundViewReturns404StatusCode() {
        CustomNotFoundView view = new CustomNotFoundView();
        BeforeEnterEvent event = Mockito.mock(BeforeEnterEvent.class);
        ErrorParameter<NotFoundException> parameter = new ErrorParameter<>(
                NotFoundException.class,
                new NotFoundException("Page not found"),
                "Custom not found message"
        );

        int statusCode = view.setErrorParameter(event, parameter);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCode);
    }

    @Test
    void vaadinInternalServerErrorViewReturns500StatusCode() {
        CustomInternalServerErrorView view = new CustomInternalServerErrorView();
        BeforeEnterEvent event = Mockito.mock(BeforeEnterEvent.class);
        ErrorParameter<Exception> parameter = new ErrorParameter<>(
                Exception.class,
                new RuntimeException("Something failed"),
                "Internal error"
        );

        int statusCode = view.setErrorParameter(event, parameter);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, statusCode);
    }
}
