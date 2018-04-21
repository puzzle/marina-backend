package ch.puzzle.marinabackend.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public class SecurityTestUtils {
    
    public static OAuth2Authentication getTestOAuth2Authentication() {
        OAuth2Authentication auth = mock(OAuth2Authentication.class);
        UsernamePasswordAuthenticationToken userauthtoken = mock(UsernamePasswordAuthenticationToken.class);
        OAuth2AuthenticationDetails authDetails = mock(OAuth2AuthenticationDetails.class);
        HashMap<String,String> details = new LinkedHashMap<String, String>();
        details.put("preferred_username", "mhousi");
        details.put("email", "housi.mousi@marina.ch");
        details.put("given_name", "housi");
        details.put("family_name", "mousi");
        details.put("sub", "thisistheid");
        
        // define return value for method getUniqueId()
        when(auth.getUserAuthentication()).thenReturn(userauthtoken);
        when(auth.getDetails()).thenReturn(authDetails);
        when(userauthtoken.getDetails()).thenReturn(details);
        when(authDetails.getTokenValue()).thenReturn("baererToken");
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
