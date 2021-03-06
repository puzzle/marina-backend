package ch.puzzle.marinabackend;

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class KeycloakRolesExtractor implements AuthoritiesExtractor {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String ROLE_USER = ROLE_PREFIX + "USER";

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        // adds default Role
        List<GrantedAuthority> result = new ArrayList<>(AuthorityUtils.createAuthorityList(ROLE_USER));

        if (map != null) {
            if (map.containsKey("roles")) {
                Object rolesObj = map.get("roles");
                result.addAll(extractRoles(rolesObj));
            }
            if (map.containsKey("client_roles")) {
                Object rolesObj = map.get("client_roles");
                result.addAll(extractRoles(rolesObj));
            }
        }
        return result;
    }

    private List<GrantedAuthority> extractRoles(Object rolesObj) {
        if (rolesObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) rolesObj;
            return AuthorityUtils
                    .createAuthorityList(roles.stream().map(s -> ROLE_PREFIX + s.toUpperCase()).toArray(String[]::new));
        }
        return null;
    }
}
