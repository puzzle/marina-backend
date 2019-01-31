package ch.puzzle.marinabackend.app;

public class EnvironmentConfiguration {
    
    private String sentryUrlFrontend;
    private String sentryEnvironment;
    
    public EnvironmentConfiguration(String sentryUrlFrontend, String sentryEnvironment) {
        this.setSentryUrlFrontend(sentryUrlFrontend);
        this.setSentryEnvironment(sentryEnvironment);
    }

    public String getSentryUrlFrontend() {
        return sentryUrlFrontend;
    }

    public void setSentryUrlFrontend(String sentryUrlFrontend) {
        this.sentryUrlFrontend = sentryUrlFrontend;
    }

    public String getSentryEnvironment() {
        return sentryEnvironment;
    }

    public void setSentryEnvironment(String sentryEnvironment) {
        this.sentryEnvironment = sentryEnvironment;
    }

}
