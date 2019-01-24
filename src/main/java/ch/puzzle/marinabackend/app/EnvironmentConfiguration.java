package ch.puzzle.marinabackend.app;

public class EnvironmentConfiguration {
    
    private String sentryUrl;
    
    public EnvironmentConfiguration(String sentryUrl) {
        this.setSentryUrl(sentryUrl);
    }

    public String getSentryUrl() {
        return sentryUrl;
    }

    public void setSentryUrl(String sentryUrl) {
        this.sentryUrl = sentryUrl;
    }

}
