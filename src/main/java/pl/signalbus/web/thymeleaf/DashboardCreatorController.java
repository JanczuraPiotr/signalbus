package pl.signalbus.web.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/uuid-creator")
public class DashboardCreatorController {

    @GetMapping({"", "/"})
    public String index() {
        return "web/uuid-creator/index";
    }
}
