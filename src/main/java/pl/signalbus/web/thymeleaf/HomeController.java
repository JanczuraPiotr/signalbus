package pl.signalbus.web.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("")
@Controller
public class HomeController {

    @GetMapping({""})
    public String root() {
        return "web/index";
    }

    @GetMapping("/login")
    public String login() {
        return "web/login";
    }

}
