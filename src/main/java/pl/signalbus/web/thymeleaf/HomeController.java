package pl.signalbus.web.thymeleaf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

@RequestMapping("")
@Controller
public class HomeController {

    private final Environment environment;

    public HomeController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping({""})
    public String root(Model model) {
        model.addAttribute("isDev", environment.acceptsProfiles(Profiles.of("dev")));
        return "web/index";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request, HttpServletResponse response) {
        var savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String path = savedRequest == null ? "" : URI.create(savedRequest.getRedirectUrl()).getPath();
        String adminPath = request.getContextPath() + "/app/admin";
        model.addAttribute("adminLogin", path.equals(adminPath) || path.startsWith(adminPath + "/"));
        return "web/login";
    }

}
