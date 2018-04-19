package ch.puzzle.marinabackend.employe;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class EmployeResourceDataTest {
	
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private EmployeRepository employeRepository;


	@Test
	public void shouldFindAllEmployees() throws Exception {
	    //given
		Employe employe = new Employe();
		employe.setFirstName("Housi");
		employe.setLastName("Mousi");
		employe.setBruttoSalary(BigDecimal.valueOf(1000.45));
		entityManager.persist(employe);
	    entityManager.flush();

	    //when
	    Iterable<Employe> employees = employeRepository.findAll();

	    //then
	    for(Employe e: employees){
	    	assertEquals(e,employe);
	    }
	}

}
