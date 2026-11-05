package pl.signalbus;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@StyleSheet("context://web/css/vaadin.css")
@SpringBootApplication
public class SignalbusApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(SignalbusApplication.class, args);
    }
}
