package ch.puzzle.marinabackend.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.MatcherAssert.assertThat;
class CurrentConfigurationTest {

    CurrentConfiguration testConfiguration;

    @BeforeEach
    void setUp() {
        Employee employee = new Employee();
        employee.setBruttoSalary(BigDecimal.valueOf(9100));
        testConfiguration = new CurrentConfiguration();
        testConfiguration.setEmployee(employee);
    }

    @Test
    void shouldGetPercentage_Amount_91_from_9100() {
        // given
        testConfiguration.setAmountChf(BigDecimal.valueOf(91));

        // when
        BigDecimal result = testConfiguration.getPercentage();

        // then
        assertThat(result, is(BigDecimal.valueOf(1)));
    }

    @Test
    void shouldGetPercentage_Amount_910_from_9100() {
        // given
        testConfiguration.setAmountChf(BigDecimal.valueOf(910));

        // when
        BigDecimal result = testConfiguration.getPercentage();

        // then
        assertThat(result, is(BigDecimal.valueOf(10)));
    }

    @Test
    void shouldGetPercentage_Amount_9100_from_9100() {
        // given
        testConfiguration.setAmountChf(BigDecimal.valueOf(9100));

        // when
        BigDecimal result = testConfiguration.getPercentage();

        // then
        assertThat(result, is(BigDecimal.valueOf(100)));
    }

    @Test
    void shouldGetPercentage_Amount_100_from_10000() {
        // given
        testConfiguration.getEmployee().setBruttoSalary(BigDecimal.valueOf(10000));
        testConfiguration.setAmountChf(BigDecimal.valueOf(100));

        // when
        BigDecimal result = testConfiguration.getPercentage();

        // then
        assertThat(result, is(BigDecimal.valueOf(1)));
    }
}