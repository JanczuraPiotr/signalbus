package pl.signalbus;

import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SignalbusApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(SignalbusApplication.class, args);
    }

}
