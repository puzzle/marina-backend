package ch.puzzle.marinabackend.employee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ch.puzzle.marinabackend.MarinaBackendApplication;
import ch.puzzle.marinabackend.TestConfiguration;
import ch.puzzle.marinabackend.employee.EmployeeRepository;
import ch.puzzle.marinabackend.employee.Employee;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MarinaBackendApplication.class,
		TestConfiguration.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class EmployeeResourceDataTest {
	
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private EmployeeRepository employeRepository;


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
	    for(Employee e: employees){
	    	assertEquals(e,employee);
	    	assertNotNull(e.getCreatedDate());
	    	assertNotNull(e.getModifiedDate());
	    }
	}

}
