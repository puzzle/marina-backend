package ch.puzzle.marinabackend.app;

public class EnvironmentConfiguration {
    
    private String sentryUrlFrontend;
    
    public EnvironmentConfiguration(String sentryUrlFrontend) {
        this.setSentryUrlFrontend(sentryUrlFrontend);
    }

    public String getSentryUrlFrontend() {
        return sentryUrlFrontend;
    }

    public void setSentryUrlFrontend(String sentryUrlFrontend) {
        this.sentryUrlFrontend = sentryUrlFrontend;
    }

}
