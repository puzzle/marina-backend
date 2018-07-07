package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.MarinaBackendApplication;
import ch.puzzle.marinabackend.TestConfiguration;
import ch.puzzle.marinabackend.security.SecurityService;
import ch.puzzle.marinabackend.security.SecurityTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MarinaBackendApplication.class, TestConfiguration.class},
        webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = true)
@ActiveProfiles("test")
public class EmployeeResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private EmployeeRepository employeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Employee housi;

    @Before
    public void setup() throws Exception {
        housi = new Employee();
        housi.setId(Long.valueOf(1));
        housi.setFirstName("Housi");
        housi.setLastName("Mousi");
        housi.setBruttoSalary(BigDecimal.valueOf(1000.45));
    }

    @Test
    public void unauthorizedRequestsShouldnotBeAllowed() throws Exception {
        // Redirect to login
        mvc.perform(get("/employees").contentType(APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));

        mvc.perform(get("/employees/1").contentType(APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldFindAllEmployees() throws Exception {
        // given
        List<Employee> allEmployees = singletonList(housi);
        when(employeRepository.findAll()).thenReturn(allEmployees);

        // when then
        mvc.perform(get("/employees").contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is(housi.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(housi.getLastName())))
                .andExpect(jsonPath("$[0].bruttoSalary", is(housi.getBruttoSalary().doubleValue())));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldGetAnEmploye() throws Exception {
        // given
        when(employeRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(housi));

        // when
        // Use the objectMapper to map the json into an object as an alternative to check via jsonpath
        // Employe employe = this.objectMapper.readValue(employeAction.getResponse().getContentAsString(), Employe.class);
//		MockHttpServletRequestBuilder employeRequest = get("/employees/1").accept(MediaTypes.HAL_JSON);
//		MvcResult employeAction = this.mvc.perform(employeRequest)
//				.andExpect(header().string("Content-Type", MediaTypes.HAL_JSON_UTF8_VALUE)).andExpect(status().isOk())
//				.andReturn();

        //assertThat(employe.getFirstName(), is(housi.getFirstName()));

        // then
        mvc.perform(get("/employees/1").contentType(APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("firstName", is(housi.getFirstName())))
                .andExpect(jsonPath("lastName", is(housi.getLastName())))
                .andExpect(jsonPath("bruttoSalary",
                        is(housi.getBruttoSalary().doubleValue())));


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldNotGetAnEmploye_notfound() throws Exception {
        // given
        when(employeRepository.findById(Long.valueOf(5))).thenReturn(Optional.of(housi));
        // when then
        mvc.perform(get("/employees/1").contentType(APPLICATION_JSON)).andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldCreateEmploye() throws Exception {
        // given
        when(employeRepository.save(any(Employee.class))).thenReturn(housi);

        // when then
        mvc.perform(post("/employees").param("id", housi.getId().toString())
                .content(objectMapper.writeValueAsBytes(housi)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("http://*/employees/" + housi.getId().toString()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void shouldCreateEmployeByPrincipal() throws Exception {
        // given
        when(securityService.convertPrincipal(any(Principal.class))).thenReturn(SecurityTestUtils.getTestUser());
        when(employeRepository.save(any(Employee.class))).thenReturn(housi);

        // when then
        mvc.perform(post("/employees/user")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("http://*/employees/" + housi.getId().toString()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void shouldCreateEmployeByPrincipal_notfound() throws Exception {
        // given
        when(securityService.convertPrincipal(any(Principal.class))).thenReturn(null);
        when(employeRepository.save(any(Employee.class))).thenReturn(housi);

        // when then
        mvc.perform(post("/employees/user")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldUpdateEmploye() throws Exception {
        // given
        when(employeRepository.findById(housi.getId())).thenReturn(Optional.of(housi));

        // when then
        mvc.perform(put("/employees/1").param("id", housi.getId().toString())
                .content(objectMapper.writeValueAsBytes(housi)).contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldNotUpdateEmploye_notfound() throws Exception {
        // given
        when(employeRepository.findById(housi.getId() - 1)).thenReturn(Optional.of(housi));

        // when then
        mvc.perform(put("/employees/1").param("id", housi.getId().toString())
                .content(objectMapper.writeValueAsBytes(housi)).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldDeleteEmploye() throws Exception {
        // given
        when(employeRepository.findById(housi.getId())).thenReturn(Optional.of(housi));

        // when then
        mvc.perform(delete("/employees/1").param("id", housi.getId().toString())).andExpect(status().isOk());
    }
}
