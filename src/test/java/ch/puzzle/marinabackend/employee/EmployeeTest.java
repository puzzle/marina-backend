package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.security.SecurityTestUtils;
import ch.puzzle.marinabackend.security.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeTest {

    @Test
    public void shouldCreateEmployeeInstanceByUser() {
        // given
        User u = SecurityTestUtils.getTestUser();

        // when
        Employee e = new Employee(u);

        // then
        assertEquals(e.getUsername(), u.getUsername());
        assertEquals(e.getId(), null);
        assertEquals(e.getFirstName(), u.getFirstName());
        assertEquals(e.getLastName(), u.getLastName());
        assertEquals(e.getEmail(), u.getEmail());
        assertEquals(e.getBruttoSalary(), null);
        assertEquals(e.getCreatedDate(), null);
        assertEquals(e.getModifiedDate(), null);
    }

    @Test
    public void shouldRoundPayoutToFloor5Cts() {

        CurrentConfiguration cc = new CurrentConfiguration();
        cc.setAmountChf(BigDecimal.valueOf(1000.49));
        Employee e = new Employee();
        e.setBruttoSalary(BigDecimal.valueOf(5000));
        cc.setEmployee(e);

        assertThat(cc.getAmountChf(), is(BigDecimal.valueOf(1000.45)));

    }


    @Test
    public void shouldRoundPayoutToFloor25PercentBruttoSalary() {

        CurrentConfiguration cc = new CurrentConfiguration();
        cc.setAmountChf(BigDecimal.valueOf(2000.49));
        Employee e = new Employee();
        e.setBruttoSalary(BigDecimal.valueOf(4000.10));
        cc.setEmployee(e);

        assertThat(cc.getAmountChf(), is(BigDecimal.valueOf(1000.00).setScale(2, RoundingMode.FLOOR)));

    }
}
