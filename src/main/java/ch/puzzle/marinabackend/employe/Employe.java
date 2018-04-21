package ch.puzzle.marinabackend.employe;

import ch.puzzle.marinabackend.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class Employe extends AbstractEntity {
    public Employe() {
    }

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

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

    public BigDecimal getBruttoSalary() {
        return bruttoSalary;
    }

    public void setBruttoSalary(BigDecimal bruttoSalary) {
        this.bruttoSalary = bruttoSalary;
    }
}
