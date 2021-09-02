package ch.puzzle.marinabackend;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: K.Tran
 * Extracts and converts Keycloak realm roles to Spring Security compatible {@link GrantedAuthority}
 * <p>
 * Basically takes roles Array and Prepends "ROLE_" to it.
 * <p>
 * Attention: Needs a "User Realm Role" Mapper for the Realm Roles or the "User Client Role" Mapper for the client Roles in the Keycloak Client:
 * Mapper Type: User Realm Role
 * Realm Role prefix: empty
 * Multivalued: ON
 * Token Claim Name: roles | client_roles
 * Claim JSON Type: String
 * Add to ID token: ON
 * Add to access token: ON
 * Add to userinfo: ON
 */
@Component
public class KeycloakRolesExtractor {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String ROLE_USER = ROLE_PREFIX + "USER";

    public List<GrantedAuthority> extractAuthorities(final OidcUser oidcUser) {
        List<GrantedAuthority> roles = new ArrayList<>(AuthorityUtils.createAuthorityList(ROLE_USER));
        roles.addAll(extractRoles(oidcUser.getAttribute("roles")));
        roles.addAll(extractRoles(oidcUser.getAttribute("client_roles")));
        return roles;
    }

    private List<GrantedAuthority> extractRoles(Object rolesObj) {
        if (rolesObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) rolesObj;
            return AuthorityUtils
                    .createAuthorityList(roles.stream().map(s -> ROLE_PREFIX + s.toUpperCase()).toArray(String[]::new));
        }
        return Collections.emptyList();
    }
}
