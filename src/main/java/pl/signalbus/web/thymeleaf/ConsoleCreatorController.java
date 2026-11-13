package pl.signalbus.web.thymeleaf;

import org.jooq.DSLContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.signalbus.config.Msg;
import pl.signalbus.service.ConsoleUUIDService;

import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;

@Controller
@RequestMapping("/console-creator")
public class ConsoleCreatorController {

    private final ConsoleUUIDService consoleUUIDService;
    private final DSLContext database;

    public ConsoleCreatorController(ConsoleUUIDService consoleUUIDService, DSLContext database) {
        this.consoleUUIDService = consoleUUIDService;
        this.database = database;
    }

    @GetMapping({"", "/"})
    public String index() {
        return "web/console-creator/index";
    }

    @GetMapping({"/creator"})
    public String creator(Model model) {
        String uuid = consoleUUIDService.generateUUID();
        model.addAttribute("uuid", uuid);
        model.addAttribute("username", uuid);
        return "web/console-creator/creator";
    }

    @PostMapping({"/creator"})
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("passwordRepeat") String passwordRepeat,
                           Model model) {
        if (password == null || password.isBlank()) {
            model.addAttribute("username", username);
            model.addAttribute("error", Msg.get("console.creator.error.passwordEmpty"));
            return "web/console-creator/creator";
        }

        if (!password.equals(passwordRepeat)) {
            model.addAttribute("username", username);
            model.addAttribute("error", Msg.get("console.creator.error.passwordMismatch"));
            return "web/console-creator/creator";
        }

        UUID consoleId;
        try {
            consoleId = UUID.fromString(username);
        } catch (IllegalArgumentException e) {
            model.addAttribute("username", username);
            model.addAttribute("error", Msg.get("console.creator.error.invalidUUID"));
            return "web/console-creator/creator";
        }

        database.insertInto(table(name("console")), field(name("id"), UUID.class), field(name("password"), String.class))
                .values(consoleId, password)
                .execute();

        return "redirect:/login";
    }

}
