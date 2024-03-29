package ch.puzzle.marinabackend;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MarinaBackendApplication.class,
        TestConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MarinaBackendApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void contextLoadsAndAcuratorOk() throws Exception {
        mvc.perform(get("/actuator/health").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void swaggerApiDocsShouldBeAvailable() throws Exception {
        mvc.perform(get("/v2/api-docs").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(get("/swagger-ui/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(get("/swagger-resources/configuration/ui").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(get("/swagger-resources/configuration/security").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void loginShouldAskForOAuthClient() throws Exception {
        mvc.perform(get("/login").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string(containsString("oauth2/authorization/keycloak")));
    }
}
