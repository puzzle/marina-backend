package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.MarinaBackendApplication;
import ch.puzzle.marinabackend.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

;

@ExtendWith(SpringExtension.class)
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
        ResponseEntity<EntityModel<Employee>> result = employeeResource.getEmployeeByEmail("housi.mousi@marina.ch");

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
        ResponseEntity<EntityModel<Employee>> result = employeeResource.getEmployeeByEmail("not.housi.mousi@marina.ch");

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
        ResponseEntity<EntityModel<Employee>> result = employeeResource.getEmployee(employee.getId());

        //then
        assertEquals(employee, result.getBody().getContent());

    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldUpdateCurrentConfigurationAmountWhenBruttoUpdatedDown() {
        //given
        Employee employee = new Employee();
        employee.setFirstName("Housi");
        employee.setLastName("Mousi");
        employee.setEmail("housi.mousi@marina.ch");
        employee.setUsername("hmousi");
        employee.setBruttoSalary(BigDecimal.valueOf(10000.00).setScale(2));

        CurrentConfiguration cc = new CurrentConfiguration();
        employee.setCurrentConfiguration(cc);
        cc.setEmployee(employee);
        cc.setWalletType(WalletType.MANUAL);
        cc.setAmountChfFromPercentage(BigDecimal.valueOf(30.00)); // Will be set to 25%, that's the allowed maximum

        entityManager.persist(employee);
        entityManager.flush();

        Employee originalEmployee = entityManager.find(Employee.class, employee.getId());
        assertThat(originalEmployee.getCurrentConfiguration().getAmountChf(), is(BigDecimal.valueOf(2500.00).setScale(2)));

        Employee updatedEmployee = new Employee();
        updatedEmployee.setFirstName("Housi");
        updatedEmployee.setLastName("Mousi");
        updatedEmployee.setEmail("housi.mousi@marina.ch");
        updatedEmployee.setUsername("hmousi");
        updatedEmployee.setBruttoSalary(BigDecimal.valueOf(4000.10).setScale(2));

        employeeResource.updateEmployee(updatedEmployee, employee.getId());

        Employee employeeOptional = entityManager.find(Employee.class, employee.getId());

        assertThat(employeeOptional.getCurrentConfiguration().getAmountChf(), is(BigDecimal.valueOf(1000.00).setScale(2)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldUpdateCurrentConfigurationAmountWhenBruttoUpdatedUp() {
        //given
        Employee employee = new Employee();
        employee.setFirstName("Housi");
        employee.setLastName("Mousi");
        employee.setEmail("housi.mousi@marina.ch");
        employee.setUsername("hmousi");
        employee.setBruttoSalary(BigDecimal.valueOf(4000.00).setScale(2));

        CurrentConfiguration cc = new CurrentConfiguration();
        employee.setCurrentConfiguration(cc);
        cc.setEmployee(employee);
        cc.setWalletType(WalletType.MANUAL);
        cc.setAmountChfFromPercentage(BigDecimal.valueOf(30.00)); // Will be set to 25%, that's the allowed maximum

        entityManager.persist(employee);
        entityManager.flush();

        Employee originalEmployee = entityManager.find(Employee.class, employee.getId());
        assertThat(originalEmployee.getCurrentConfiguration().getAmountChf(), is(BigDecimal.valueOf(1000.00).setScale(2)));

        Employee updatedEmployee = new Employee();
        updatedEmployee.setFirstName("Housi");
        updatedEmployee.setLastName("Mousi");
        updatedEmployee.setEmail("housi.mousi@marina.ch");
        updatedEmployee.setUsername("hmousi");
        updatedEmployee.setBruttoSalary(BigDecimal.valueOf(20000.10).setScale(2));

        employeeResource.updateEmployee(updatedEmployee, employee.getId());

        Employee employeeOptional = entityManager.find(Employee.class, employee.getId());

        assertThat(employeeOptional.getCurrentConfiguration().getAmountChf(), is(BigDecimal.valueOf(5000.00).setScale(2)));
    }

    @Test
    public void shouldPreventDuplicateEmail() {
        //given
        Employee employee = new Employee();
        employee.setFirstName("Housi");
        employee.setLastName("Mousi");
        employee.setEmail("housi.mousi@marina.ch");
        employee.setSocialSecurityNumber("000.0000.0000.00");
        employee.setUsername("hmousi");
        employee.setBruttoSalary(BigDecimal.valueOf(1000.45));
        entityManager.persist(employee);
        entityManager.flush();

        //when
        Employee duplicate = new Employee();
        duplicate.setFirstName(employee.getFirstName());
        duplicate.setLastName(employee.getLastName());
        duplicate.setEmail(employee.getEmail());
        duplicate.setUsername(employee.getUsername() + "different");
        duplicate.setUsername(employee.getSocialSecurityNumber() + "different");
        duplicate.setBruttoSalary(employee.getBruttoSalary());
        entityManager.persist(duplicate);

        assertThrows(PersistenceException.class, () -> entityManager.flush());
    }

    @Test
    public void shouldPreventDuplicateUsername() {
        //given
        Employee employee = new Employee();
        employee.setFirstName("Housi");
        employee.setLastName("Mousi");
        employee.setEmail("housi.mousi@marina.ch");
        employee.setSocialSecurityNumber("000.0000.0000.00");
        employee.setUsername("hmousi");
        employee.setBruttoSalary(BigDecimal.valueOf(1000.45));
        entityManager.persist(employee);
        entityManager.flush();

        //when
        Employee duplicate = new Employee();
        duplicate.setFirstName(employee.getFirstName());
        duplicate.setLastName(employee.getLastName());
        duplicate.setEmail(employee.getEmail() + "different");
        duplicate.setUsername(employee.getUsername());
        duplicate.setSocialSecurityNumber(employee.getSocialSecurityNumber() + "different");
        duplicate.setBruttoSalary(employee.getBruttoSalary());
        entityManager.persist(duplicate);

        assertThrows(PersistenceException.class, () -> entityManager.flush());
    }

    @Test
    public void shouldPreventDuplicateSocialSecurityNumber() {
        //given
        Employee employee = new Employee();
        employee.setFirstName("Housi");
        employee.setLastName("Mousi");
        employee.setEmail("housi.mousi@marina.ch");
        employee.setSocialSecurityNumber("000.0000.0000.00");
        employee.setUsername("hmousi");
        employee.setBruttoSalary(BigDecimal.valueOf(1000.45));
        entityManager.persist(employee);
        entityManager.flush();

        //when
        Employee duplicate = new Employee();
        duplicate.setFirstName(employee.getFirstName());
        duplicate.setLastName(employee.getLastName());
        duplicate.setEmail(employee.getEmail() + "different");
        duplicate.setUsername(employee.getUsername() + "different");
        duplicate.setSocialSecurityNumber(employee.getSocialSecurityNumber());
        duplicate.setBruttoSalary(employee.getBruttoSalary());
        entityManager.persist(duplicate);

        assertThrows(PersistenceException.class, () -> entityManager.flush());
    }
}
