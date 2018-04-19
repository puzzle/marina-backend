package ch.puzzle.marinabackend.employe;

import static java.util.Collections.singletonList;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	public void shouldNotGetAnEmploye_notfound() throws Exception {
		//given
		when(employeRepository.findById(Long.valueOf(5))).thenReturn(Optional.of(housi));
		// when then
		mvc.perform(get("/employees/1").contentType(APPLICATION_JSON)).andExpect(status().isNotFound());

	}
	
	@Test
	public void shouldCreateEmploye() throws Exception {
		//given
		when(employeRepository.save(any(Employe.class))).thenReturn(housi);
		
		// when then
		mvc.perform(post("/employees").param("id", housi.getId().toString())
				.content(convertObjectToJsonBytes(housi)).contentType(APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andExpect(redirectedUrlPattern("http://*/employees/"+ housi.getId().toString()));
	}
	
	@Test
	public void shouldUpdateEmploye() throws Exception {
		//given
		when(employeRepository.findById(housi.getId())).thenReturn(Optional.of(housi));
		
		// when then
		mvc.perform(put("/employees/1").param("id", housi.getId().toString())
				.content(convertObjectToJsonBytes(housi)).contentType(APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}
	
	@Test
	public void shouldNotUpdateEmploye_notfound() throws Exception {
		//given
		when(employeRepository.findById(housi.getId()-1)).thenReturn(Optional.of(housi));
		
		// when then
		mvc.perform(put("/employees/1").param("id", housi.getId().toString())
				.content(convertObjectToJsonBytes(housi)).contentType(APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
	
	@Test
	public void shouldDeleteEmploye() throws Exception {
		//given
		when(employeRepository.findById(housi.getId())).thenReturn(Optional.of(housi));
		
		// when then
		mvc.perform(delete("/employees/1").param("id", housi.getId().toString()))
		.andExpect(status().isOk());
	}
	
	 
    private byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(object);
    }
}
