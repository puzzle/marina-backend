package ch.puzzle.marinabackend.security;

import java.util.HashMap;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public class User {
    
    private String id;
    private String bearerToken;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    
    public User(OAuth2Authentication auth) {
        super();
        UsernamePasswordAuthenticationToken userAuthentication = (UsernamePasswordAuthenticationToken)auth.getUserAuthentication();
        OAuth2AuthenticationDetails authDetails = (OAuth2AuthenticationDetails)auth.getDetails();
        HashMap<String,String> details = (HashMap<String,String>)userAuthentication.getDetails();
        this.id = details.get("sub");
        this.bearerToken = authDetails.getTokenValue();
        this.username = details.get("preferred_username");
        this.email = details.get("email");;
        this.firstName = details.get("given_name");;
        this.lastName = details.get("family_name");;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

}
