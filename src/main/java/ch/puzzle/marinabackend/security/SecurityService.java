package ch.puzzle.marinabackend.security;

import ch.puzzle.marinabackend.KeycloakRolesExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityService {

    @Autowired
    private KeycloakRolesExtractor rolesExtractor;

    @SuppressWarnings("unchecked")
    public User convertPrincipal(Principal principal) {

        if (principal instanceof OAuth2Authentication) {
            User u = new User();
            OAuth2Authentication auth = (OAuth2Authentication) principal;
            if (auth.getUserAuthentication() instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken userAuthentication = (UsernamePasswordAuthenticationToken) auth
                        .getUserAuthentication();
                if (userAuthentication.getDetails() instanceof Map<?, ?>) {
                    Map<String, Object> details = (Map<String, Object>) userAuthentication.getDetails();

                    u.setId(String.format("%s", details.get("sub")));
                    u.setUsername(String.format("%s", details.get("preferred_username")));
                    u.setEmail(String.format("%s", details.get("email")));
                    u.setFirstName(String.format("%s", details.get("given_name")));
                    u.setLastName(String.format("%s", details.get("family_name")));
                    List<GrantedAuthority> authorities = rolesExtractor.extractAuthorities(details);
                    if (authorities != null) {
                        Set<String> stringAuthorities = authorities
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());
                        u.setAuthorities(stringAuthorities);
                    }
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
