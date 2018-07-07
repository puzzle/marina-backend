package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.AbstractEntity;
import ch.puzzle.marinabackend.LocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_payout")
public class MonthlyPayout extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference
    private Employee employee;

    @Column(name = "year")
    private Integer year;

    @Column(name = "month")
    private Integer month;

    @Column(name = "amount_chf")
    private BigDecimal amountChf;

    @Column(name = "amount_btc")
    private Long amountBtc;

    @Column(name = "rate_chf")
    private BigDecimal rateChf;

    @Column(name = "payment_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime paymentDate;

    @Column(name = "public_address")
    private String publicAddress;

    @Column(name = "address_index")
    private Long addressIndex;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public BigDecimal getAmountChf() {
        return amountChf;
    }

    public void setAmountChf(BigDecimal amountChf) {
        this.amountChf = amountChf;
    }

    public Long getAmountBtc() {
        return amountBtc;
    }

    public void setAmountBtc(Long amountBtc) {
        this.amountBtc = amountBtc;
    }

    public BigDecimal getRateChf() {
        return rateChf;
    }

    public void setRateChf(BigDecimal rateChf) {
        this.rateChf = rateChf;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public void setPublicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
    }

    public Long getAddressIndex() {
        return addressIndex;
    }

    public void setAddressIndex(Long addressIndex) {
        this.addressIndex = addressIndex;
    }
}
