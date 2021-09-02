package ch.puzzle.marinabackend.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityTestUtils {

    public static Authentication getTestOAuth2Authentication() {
        Authentication auth = mock(Authentication.class);
        UsernamePasswordAuthenticationToken userauthtoken = mock(UsernamePasswordAuthenticationToken.class);
        OidcUser oidcUser = mock(OidcUser.class);
//        HashMap<String, String> details = new LinkedHashMap<String, String>();
//        details.put("preferred_username", "mhousi");
//        details.put("email", "housi.mousi@marina.ch");
//        details.put("given_name", "housi");
//        details.put("family_name", "mousi");
//        details.put("sub", "thisistheid");

        // define return value for method getUniqueId()
//        when(auth.getUserAuthentication()).thenReturn(userauthtoken);
        when(auth.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getAttribute(eq("preferred_username"))).thenReturn("mhousi");
        when(oidcUser.getAttribute(eq("email"))).thenReturn("housi.mousi@marina.ch");
        when(oidcUser.getAttribute(eq("given_name"))).thenReturn("housi");
        when(oidcUser.getAttribute(eq("family_name"))).thenReturn("mousi");
        when(oidcUser.getAttribute(eq("sub"))).thenReturn("thisistheid");

        OidcIdToken oidcIdToken = mock(OidcIdToken.class);
        when(oidcIdToken.getTokenValue()).thenReturn("baererToken");
        when(oidcUser.getIdToken()).thenReturn(oidcIdToken);
        return auth;
    }

    public static User getTestUser() {
        User u = new User();
        u.setId("id");
        u.setFirstName("firstname");
        u.setLastName("lastname");
        u.setEmail("house.mousi@marina.ch");
        u.setBearerToken("bearerTocken");
        u.setUsername("hmousi");
        return u;
    }
}
