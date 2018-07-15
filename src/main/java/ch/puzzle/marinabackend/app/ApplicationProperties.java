package ch.puzzle.marinabackend.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application")
public class ApplicationProperties {
    
    private String persistentFilePath;

    public String getPersistentFilePath() {
        return persistentFilePath;
    }

    public void setPersistentFilePath(String persistentFilePath) {
        this.persistentFilePath = persistentFilePath;
    }
}
