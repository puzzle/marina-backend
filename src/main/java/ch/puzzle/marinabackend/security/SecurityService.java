package ch.puzzle.marinabackend.security;

import java.security.Principal;
import java.util.HashMap;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {

    public User convertPrincipal(Principal principal) {
        
        if (principal instanceof OAuth2Authentication) {
            User u = new User();
            OAuth2Authentication auth = (OAuth2Authentication) principal;
            if (auth.getUserAuthentication() instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken userAuthentication = (UsernamePasswordAuthenticationToken) auth
                        .getUserAuthentication();
                if (userAuthentication.getDetails() instanceof HashMap<?, ?>) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, String> details = (HashMap<String, String>) userAuthentication.getDetails();
                    u.setId(details.get("sub"));

                    u.setUsername(details.get("preferred_username"));
                    u.setEmail(details.get("email"));
                    u.setFirstName(details.get("given_name"));
                    u.setLastName(details.get("family_name"));
                }
            }

            if (auth.getDetails() instanceof OAuth2AuthenticationDetails) {
                OAuth2AuthenticationDetails authDetails = (OAuth2AuthenticationDetails) auth.getDetails();
                u.setBearerToken(authDetails.getTokenValue());
            }
            return u;
        }
        return null;
    }
}
