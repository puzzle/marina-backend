package ch.puzzle.marinabackend.employee;

import java.math.BigDecimal;

public class MonthlyPayoutVM {
    
    private Long employeeId;
    private BigDecimal amountChf;
    private Long amountBtc;
    private BigDecimal rateChf;
    private String publicAddress;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public String getPublicAddress() {
        return publicAddress;
    }

    public void setPublicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
    }
}
