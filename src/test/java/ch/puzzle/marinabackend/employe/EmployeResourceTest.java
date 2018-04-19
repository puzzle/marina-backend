package ch.puzzle.marinabackend.employe;

import static java.util.Collections.singletonList;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(EmployeResource.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class EmployeResourceTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private EmployeRepository employeRepository;
	
	private Employe housi;
	
	@Before
	public void setup() {
		housi = new Employe();
		housi.setId(Long.valueOf(1));
		housi.setFirstName("Housi");
		housi.setLastName("Mousi");
		housi.setBruttoSalary(BigDecimal.valueOf(1000.45));
	}

	@Test
	public void shouldFindAllEmployees() throws Exception {
		//given
		List<Employe> allEmployees = singletonList(housi);
		when(employeRepository.findAll()).thenReturn(allEmployees);
		
		// when then
		mvc.perform(get("/employees").contentType(APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].firstName", is(housi.getFirstName())))
				.andExpect(jsonPath("$[0].lastName", is(housi.getLastName())))
				.andExpect(jsonPath("$[0].bruttoSalary", is(housi.getBruttoSalary().doubleValue())));

	}
	
	@Test
	public void shouldGetAnEmploye() throws Exception {
		//given
		when(employeRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(housi));
		// when then
		mvc.perform(get("/employees/1").contentType(APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("firstName", is(housi.getFirstName())))
				.andExpect(jsonPath("lastName", is(housi.getLastName())))
				.andExpect(jsonPath("bruttoSalary", is(housi.getBruttoSalary().doubleValue())));

	}
	
	@Test
	@Ignore
	public void shouldCreateEmploye() throws Exception {
		//given

		when(employeRepository.save(housi)).thenReturn(housi);
		// when then
		mvc.perform(post("/employees").content(housi.toString()).contentType(APPLICATION_JSON)).andExpect(status().isOk());
		// TODO: check location

	}

}
