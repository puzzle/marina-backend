package ch.puzzle.marinabackend.security;

import ch.puzzle.marinabackend.KeycloakRolesExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityService {

    @Autowired
    private KeycloakRolesExtractor rolesExtractor;

    @SuppressWarnings("unchecked")
    public User convertPrincipal(Principal principal) {
        if (!(principal instanceof Authentication)) {
            return null;
        }
        User u = new User();
        Authentication auth = (Authentication) principal;
        if (!(auth.getPrincipal() instanceof OidcUser)) {
            return u;
        }
        OidcUser oidcUser = (OidcUser) auth.getPrincipal();

        Object id = oidcUser.getAttribute("sub");
        if (id != null) {
            u.setId(String.format("%s", id));
            final Object username = oidcUser.getAttribute("preferred_username");
            u.setUsername(String.format("%s", username));
            u.setEmail(String.format("%s", Optional.ofNullable(oidcUser.getAttribute("email")).orElse(username)));
            u.setFirstName(String.format("%s", Optional.ofNullable(oidcUser.getAttribute("given_name")).orElse(username)));
            u.setLastName(String.format("%s", Optional.ofNullable(oidcUser.getAttribute("family_name")).orElse(username)));
        }
        List<GrantedAuthority> authorities = rolesExtractor.extractAuthorities(oidcUser);
        if (authorities != null) {
            Set<String> stringAuthorities = authorities
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            u.setAuthorities(stringAuthorities);
        }
        if (oidcUser.getIdToken() != null) {
            u.setBearerToken(String.format("%s", oidcUser.getIdToken().getTokenValue()));
        }
        return u;
    }
}
