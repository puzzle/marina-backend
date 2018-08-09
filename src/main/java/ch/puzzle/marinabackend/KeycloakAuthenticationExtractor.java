package ch.puzzle.marinabackend;

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

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
public class KeycloakAuthenticationExtractor implements UserAuthenticationConverter {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String ROLE_USER = ROLE_PREFIX + "USER";
    private static final String KEY_CLIENT_ROLES = "client_roles";
    private static final String KEY_USERNAME = "preferred_username";
    private static final String KEY_EXPIRATION = "exp";

    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(KEY_USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }

    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(KEY_USERNAME)) {
            Object principal = map.get(KEY_USERNAME);
            Collection<? extends GrantedAuthority> authorities = extractAuthorities(map);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
            token.setDetails(map);
            return token;
        }
        return null;
    }

    public List<GrantedAuthority> extractAuthorities(Map<String, ?> map) {
        // adds default roles
        List<GrantedAuthority> result = new ArrayList<>(AuthorityUtils.createAuthorityList(ROLE_USER));
        if (map != null) {
            if (map.containsKey(KEY_CLIENT_ROLES)) {
                Object rolesObj = map.get(KEY_CLIENT_ROLES);
                result.addAll(extractRoles(rolesObj));
            }
        }
        return result;
    }

    public Instant extractExpiration(Map<String, ?> map) {
        if (map.containsKey(KEY_EXPIRATION)) {
            long timestamp = Long.parseLong(map.get(KEY_EXPIRATION).toString());
            return Instant.ofEpochSecond(timestamp);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<GrantedAuthority> extractRoles(Object rolesObj) {
        if (rolesObj instanceof List) {
            List<String> roles = (List<String>) rolesObj;
            return AuthorityUtils
                    .createAuthorityList(roles.stream().map(s -> ROLE_PREFIX + s.toUpperCase()).toArray(String[]::new));
        }
        return null;
    }
}
