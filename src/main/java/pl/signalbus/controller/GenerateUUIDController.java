package pl.signalbus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.signalbus.service.ConsoleUUIDService;

@RestController
@RequestMapping("/api/v1/console")
public class GenerateUUIDController {

    private final ConsoleUUIDService consoleUUIDService;

    GenerateUUIDController(ConsoleUUIDService consoleUUIDService) {
        this.consoleUUIDService = consoleUUIDService;
    }

    @RequestMapping("/generate-uuid")
    public String generateUUID() {
        return consoleUUIDService.generateUUID();
    }

}
