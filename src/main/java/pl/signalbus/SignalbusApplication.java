package pl.signalbus;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@StyleSheet("context://web/css/error.css")
@StyleSheet("context://web/css/vaadin.css")
@StyleSheet(Lumo.STYLESHEET)
@SpringBootApplication
public class SignalbusApplication implements AppShellConfigurator {

    static void main(String[] args) {
        SpringApplication.run(SignalbusApplication.class, args);
    }
}
