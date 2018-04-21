package ch.puzzle.marinabackend.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class SecurityServiceTest {

    @Test
    public void shouldCastPrincipal() {
        // given
        SecurityService service = new SecurityService();
        OAuth2Authentication p = SecurityTestUtils.getTestOAuth2Authentication();
        @SuppressWarnings("unchecked")
        HashMap<String, String> details = (HashMap<String, String>)  p.getUserAuthentication()
                .getDetails();

        // when
        User u = service.convertPrincipal(p);

        // then
        assertEquals(details.get("preferred_username"), u.getUsername());
        assertEquals(details.get("sub"), u.getId());
        assertEquals(details.get("given_name"), u.getFirstName());
        assertEquals(details.get("family_name"), u.getLastName());
        assertEquals(details.get("email"), u.getEmail());
        assertEquals("baererToken", u.getBearerToken());
    }
    
    @Test
    public void shouldNotCastPrincipal() {
        // given
        SecurityService service = new SecurityService();
        Authentication p = mock(Authentication.class);
        
        // when
        User u = service.convertPrincipal(p);

        // then
        assertNull(u);
        
    }
    
    @Test
    public void shouldCastPrincipalWithoutBearerToken() {
        // given
        SecurityService service = new SecurityService();
        OAuth2Authentication p = SecurityTestUtils.getTestOAuth2Authentication();
        WebAuthenticationDetails authdetails = mock(WebAuthenticationDetails.class);
        when(p.getDetails()).thenReturn(authdetails);
        @SuppressWarnings("unchecked")
        HashMap<String, String> details = (HashMap<String, String>)  p.getUserAuthentication()
                .getDetails();

        // when
        User u = service.convertPrincipal(p);

        // then
        assertEquals(details.get("preferred_username"), u.getUsername());
        assertEquals(details.get("sub"), u.getId());
        assertEquals(details.get("given_name"), u.getFirstName());
        assertEquals(details.get("family_name"), u.getLastName());
        assertEquals(details.get("email"), u.getEmail());
        assertNull(u.getBearerToken());
    }
    
    @Test
    public void shouldCastPrincipalOnlyBearerToken() {
        // given
        SecurityService service = new SecurityService();
        OAuth2Authentication p = SecurityTestUtils.getTestOAuth2Authentication();
        Authentication a = mock(AnonymousAuthenticationToken.class);
        when(p.getUserAuthentication()).thenReturn(a);

        // when
        User u = service.convertPrincipal(p);

        // then
        assertNull(u.getUsername());
        assertNull(u.getId());
        assertNull(u.getFirstName());
        assertNull(u.getLastName());
        assertNull(u.getEmail());
        assertEquals("baererToken", u.getBearerToken());
    }
    
    @Test
    public void shouldCastPrincipalOnlyBearerToken_UserPasswordToken() {
        // given
        SecurityService service = new SecurityService();
        OAuth2Authentication p = SecurityTestUtils.getTestOAuth2Authentication();
        
        when(p.getUserAuthentication().getDetails()).thenReturn(new Object());


        // when
        User u = service.convertPrincipal(p);

        // then
        assertNull(u.getUsername());
        assertNull(u.getId());
        assertNull(u.getFirstName());
        assertNull(u.getLastName());
        assertNull(u.getEmail());
        assertEquals("baererToken", u.getBearerToken());
    }

}
