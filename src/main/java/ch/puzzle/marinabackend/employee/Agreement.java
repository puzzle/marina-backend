package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "agreement")
public class Agreement extends AbstractEntity {
    
    @OneToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference
    private Employee employee;
    
    @Column(name = "agreement_pdf_path")
    private String agreementPdfPath;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getAgreementPdfPath() {
        return agreementPdfPath;
    }

    public void setAgreementPdfPath(String agreementPdfPath) {
        this.agreementPdfPath = agreementPdfPath;
    }
}
