package ch.puzzle.marinabackend.security;

import ch.puzzle.marinabackend.KeycloakRolesExtractor;
import ch.puzzle.marinabackend.MarinaBackendApplication;
import ch.puzzle.marinabackend.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MarinaBackendApplication.class, TestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc()
@ActiveProfiles("test")
public class SecurityServiceTest {

    @MockBean
    private KeycloakRolesExtractor rolesExtractor;

    @Autowired
    private SecurityService service;

    private Authentication principal;

    @BeforeEach
    public void setup() {
        principal = SecurityTestUtils.getTestOAuth2Authentication();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCastPrincipal() {
        // given
        OidcUser oidcUser = (OidcUser) principal.getPrincipal();

        // when
        User u = service.convertPrincipal(principal);

        // then
        assertEquals(oidcUser.getAttribute("preferred_username"), u.getUsername());
        assertEquals(oidcUser.getAttribute("sub"), u.getId());
        assertEquals(oidcUser.getAttribute("given_name"), u.getFirstName());
        assertEquals(oidcUser.getAttribute("family_name"), u.getLastName());
        assertEquals(oidcUser.getAttribute("email"), u.getEmail());
        assertEquals("baererToken", u.getBearerToken());
    }

    @Test
    public void shouldNotCastPrincipal() {
        // given
        Principal p = mock(Principal.class);

        // when
        User u = service.convertPrincipal(p);

        // then
        assertNull(u);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCastPrincipalWithoutBearerToken() {
        // given
        OidcUser oidcUser = (OidcUser) principal.getPrincipal();
        when(oidcUser.getIdToken()).thenReturn(null);

        // when
        User u = service.convertPrincipal(principal);

        // then
        assertEquals(oidcUser.getAttribute("preferred_username"), u.getUsername());
        assertEquals(oidcUser.getAttribute("sub"), u.getId());
        assertEquals(oidcUser.getAttribute("given_name"), u.getFirstName());
        assertEquals(oidcUser.getAttribute("family_name"), u.getLastName());
        assertEquals(oidcUser.getAttribute("email"), u.getEmail());
        assertNull(u.getBearerToken());
    }

    @Test
    public void shouldCastPrincipalOnlyBearerToken() {
        // given
        String token = "bearerToken";
        OidcIdToken oidcIdToken = mock(OidcIdToken.class);
        when(oidcIdToken.getTokenValue()).thenReturn(token);
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getIdToken()).thenReturn(oidcIdToken);
        when(principal.getPrincipal()).thenReturn(oidcUser);

        // when
        User u = service.convertPrincipal(principal);

        // then
        assertNull(u.getUsername());
        assertNull(u.getId());
        assertNull(u.getFirstName());
        assertNull(u.getLastName());
        assertNull(u.getEmail());
        assertEquals("bearerToken", u.getBearerToken());
    }
}
