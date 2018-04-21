package ch.puzzle.marinabackend;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MarinaBackendApplication.class,
		TestConfiguration.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
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
		mvc.perform(get("/swagger-ui.html").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		mvc.perform(get("/swagger-resources/configuration/ui").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		mvc.perform(get("/swagger-resources/configuration/security").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	@Test
    public void loginShouldRedirectToSso() throws Exception {
        mvc.perform(get("/login").accept(MediaType.APPLICATION_JSON)).andExpect(status().isFound())
        .andExpect(redirectedUrlPattern("https://*/auth**http://**"));
    }

}
