package ch.puzzle.marinabackend.security;

import ch.puzzle.marinabackend.MarinaBackendApplication;
import ch.puzzle.marinabackend.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.Principal;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MarinaBackendApplication.class,
        TestConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = true)
@ActiveProfiles("test")
public class SecurityResourceTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private SecurityService securityService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldGetUserFromPrincipal() throws Exception {
        //  given
        User u = SecurityTestUtils.getTestUser();
        when(securityService.convertPrincipal(any(Principal.class))).thenReturn(u);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder).andExpect(status().isOk())
                .andExpect(jsonPath("username", is(u.getUsername())))
                .andExpect(jsonPath("id", is(u.getId())))
                .andExpect(jsonPath("firstName", is(u.getFirstName())))
                .andExpect(jsonPath("lastName", is(u.getLastName())))
                .andExpect(jsonPath("email", is(u.getEmail())))
                .andExpect(jsonPath("bearerToken", is(u.getBearerToken())));
    }

    @Test
    public void unauthorizedUserRequestsShouldnotBeAllowed() throws Exception {
        mvc.perform(get("/user").contentType(APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldRedirectToRoot() throws Exception {
        mvc.perform(get("/sso").contentType(APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void unauthorizedRequestsShouldnotBeAllowed() throws Exception {
        mvc.perform(get("/sso").contentType(APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

}
