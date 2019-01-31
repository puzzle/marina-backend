package ch.puzzle.marinabackend.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnvironmentConfigurationResource {

    private static final String SENTRY_ENVIRONMENT = "SENTRY_ENVIRONMENT";
    
    @Value("${application.sentryurlfrontend}")
    private String sentryUrlFrontend;
    
    @GetMapping("/environmentconfiguration")
    public EnvironmentConfiguration getApplicationInfo() {
        return new EnvironmentConfiguration(this.sentryUrlFrontend, System.getenv(SENTRY_ENVIRONMENT));
    }
}
