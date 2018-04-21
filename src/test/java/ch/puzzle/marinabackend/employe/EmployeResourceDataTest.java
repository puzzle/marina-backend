package ch.puzzle.marinabackend.employe;

import ch.puzzle.marinabackend.MarinaBackendApplication;
import ch.puzzle.marinabackend.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MarinaBackendApplication.class,
        TestConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class EmployeResourceDataTest {

    @Autowired
    private EntityManager entityManager;

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
        for (Employe e : employees) {
            assertEquals(e, employe);
        }
    }

}
