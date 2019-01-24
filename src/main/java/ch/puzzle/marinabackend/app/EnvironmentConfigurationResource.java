package ch.puzzle.marinabackend.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnvironmentConfigurationResource {

    @Value("${application.sentryUrl}")
    private String sentryUrl;
    
    @GetMapping("/environmentconfiguration")
    public EnvironmentConfiguration getApplicationInfo() {
        return new EnvironmentConfiguration(this.sentryUrl);
    }
}
