package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.MarinaBackendApplication;
import ch.puzzle.marinabackend.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MarinaBackendApplication.class,
        TestConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class EmployeeResourceDataTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EmployeeRepository employeRepository;

    @Autowired
    private EmployeeResource employeeResource;


    @Test
    public void shouldFindAllEmployees() throws Exception {
        //given
        Employee employee = new Employee();
        employee.setFirstName("Housi");
        employee.setLastName("Mousi");
        employee.setEmail("housi.mousi@marina.ch");
        employee.setUsername("hmousi");
        employee.setBruttoSalary(BigDecimal.valueOf(1000.45));
        entityManager.persist(employee);
        entityManager.flush();

        //when
        Iterable<Employee> employees = employeRepository.findAll();

        //then
        Employee result = employees.iterator().next();
        assertEquals(employee, result);
        assertNotNull(result.getCreatedDate());
        assertNotNull(result.getModifiedDate());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldFindEmployeeByEmail() throws Exception {
        //given
        Employee employee = new Employee();
        employee.setFirstName("Housi");
        employee.setLastName("Mousi");
        employee.setEmail("housi.mousi@marina.ch");
        employee.setUsername("hmousi");
        employee.setBruttoSalary(BigDecimal.valueOf(1000.45));
        entityManager.persist(employee);
        entityManager.flush();

        //when
        ResponseEntity<Resource<Employee>> result = employeeResource.getEmployeeByEmail("housi.mousi@marina.ch");

        //then
        assertNotNull(result);
        assertEquals(employee, result.getBody().getContent());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldNotFindEmployeeByEmail() throws Exception {
        //given
        Employee employee = new Employee();
        employee.setFirstName("Housi");
        employee.setLastName("Mousi");
        employee.setEmail("housi.mousi@marina.ch");
        employee.setUsername("hmousi");
        employee.setBruttoSalary(BigDecimal.valueOf(1000.45));
        entityManager.persist(employee);
        entityManager.flush();

        //when
        ResponseEntity<Resource<Employee>> result = employeeResource.getEmployeeByEmail("not.housi.mousi@marina.ch");

        //then
        assertEquals(ResponseEntity.notFound().build(), result);

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldNotFindAgreementOnEmployee() throws Exception {
        //given
        Employee employee = new Employee();
        employee.setFirstName("Housi");
        employee.setLastName("Mousi");
        employee.setEmail("housi.mousi@marina.ch");
        employee.setUsername("hmousi");
        employee.setBruttoSalary(BigDecimal.valueOf(1000.45));

        Agreement a = new Agreement();
        a.setEmployee(employee);
        a.setAgreementPdfPath("a path");
        employee.setAgreement(a);
        entityManager.persist(employee);
        entityManager.persist(a);
        entityManager.flush();

        //when
        ResponseEntity<Resource<Employee>> result = employeeResource.getEmployee(employee.getId());

        //then
        assertEquals(employee, result.getBody().getContent());

    }

}
