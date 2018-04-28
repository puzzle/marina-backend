package ch.puzzle.marinabackend.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ch.puzzle.marinabackend.MarinaBackendApplication;

public class ApplicationInfo {
    private String implementationTitle;
    private String implementationVersion;
    private String implementationTimestamp;
    private String jenkinsBuildnumber;
    private String gitCommit;
    private String gitBranch;
    private String buildJobName;
    private String buildJobUrl;
    
    public ApplicationInfo() {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");

        Properties prop = new Properties();
        try {
            prop.load( is );
        } catch (IOException ex) {
            
        }
        implementationVersion = MarinaBackendApplication.class.getPackage().getImplementationVersion();
        implementationTitle = MarinaBackendApplication.class.getPackage().getImplementationTitle();
        implementationTimestamp = prop.getProperty("Implementation-Timestamp", "none");
        jenkinsBuildnumber = prop.getProperty("Jenkins-buildnumber", "none");
        buildJobName = prop.getProperty("Buildjob-name", "none");
        buildJobUrl = prop.getProperty("Buildjob-url", "none");
        gitCommit = prop.getProperty("Git-commit", "none");
        gitBranch = prop.getProperty("Git-branch", "none");
    }

    public String getImplementationTitle() {
        return implementationTitle;
    }

    public String getImplementationVersion() {
        return implementationVersion;
    }

    public String getImplementationTimestamp() {
        return implementationTimestamp;
    }

    public String getJenkinsBuildnumber() {
        return jenkinsBuildnumber;
    }

    public String getOpenShiftBuildCommit() {
        return gitCommit;
    }
}
