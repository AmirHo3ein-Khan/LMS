package ir.lms.util;

import ir.lms.service.InitializerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {
    private final InitializerService initializerService;


    public Initializer(InitializerService initializerService) {
        this.initializerService = initializerService;
    }

    @Override
    public void run(String... args){
        initializerService.createRolesIfNotExist();
        initializerService.createAdminIfNotExists();
        initializerService.createMajorIfNotExists();
    }
}
