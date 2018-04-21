package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class Employee extends AbstractEntity {
    public Employee() {
    }

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "username")
    private String username;

    @Column(name = "brutto_salary")
    private BigDecimal bruttoSalary;

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
}
