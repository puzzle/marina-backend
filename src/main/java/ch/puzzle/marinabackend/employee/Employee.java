package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.AbstractEntity;
import ch.puzzle.marinabackend.LocalDateTimeSerializer;
import ch.puzzle.marinabackend.security.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "employee")
public class Employee extends AbstractEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "brutto_salary")
    private BigDecimal bruttoSalary;

    @Column(name = "validated_at_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime validatedAt;

    @OneToOne(cascade = ALL, mappedBy = "employee", orphanRemoval = true)
    @RestResource(path = "employeeAgreement", rel = "agreement")
    private Agreement agreement;

    @OneToOne(cascade = ALL, mappedBy = "employee")
    @RestResource(path = "employeeCurrentConfiguration", rel = "currentConfiguration")
    private CurrentConfiguration currentConfiguration;

    @OneToMany(mappedBy = "employee", fetch = EAGER)
    private List<MonthlyPayout> monthlyPayouts;

    @Column(name = "social_security_number", unique = true)
    private String socialSecurityNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    public Employee() {
    }

    public Employee(User u) {
        firstName = u.getFirstName();
        lastName = u.getLastName();
        email = u.getEmail();
        username = u.getUsername();
        status = EmployeeStatus.ACTIVE;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getBruttoSalary() {
        return bruttoSalary;
    }

    public void setBruttoSalary(BigDecimal bruttoSalary) {
        this.bruttoSalary = bruttoSalary;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public Agreement getAgreement() {
        return agreement;
    }

    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
    }

    public CurrentConfiguration getCurrentConfiguration() {
        return currentConfiguration;
    }

    public void setCurrentConfiguration(CurrentConfiguration currentConfiguration) {
        this.currentConfiguration = currentConfiguration;
    }

    public List<MonthlyPayout> getMonthlyPayouts() {
        return monthlyPayouts;
    }

    public void setMonthlyPayouts(List<MonthlyPayout> monthlyPayouts) {
        this.monthlyPayouts = monthlyPayouts;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }
}
