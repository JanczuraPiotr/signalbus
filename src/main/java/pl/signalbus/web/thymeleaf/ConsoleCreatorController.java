package pl.signalbus.web.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/console-creator")
public class ConsoleCreatorController {

    @GetMapping({"", "/"})
    public String index() {
        return "web/console-creator/index";
    }
}
