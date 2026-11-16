package pl.signalbus.service;

import org.springframework.stereotype.Service;

@Service
public class ConsoleUUIDServiceImpl implements ConsoleUUIDService {

    @Override
    public String generateUUID() {
        return java.util.UUID.randomUUID().toString();
    }

}
