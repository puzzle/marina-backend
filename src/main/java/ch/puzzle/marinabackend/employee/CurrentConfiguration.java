package ch.puzzle.marinabackend.employee;

import ch.puzzle.marinabackend.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "current_configuration")
public class CurrentConfiguration extends AbstractEntity {
    private static final BigDecimal MAX_PAYOUT_PERCENTAGE = BigDecimal.valueOf(25);
    private static final BigDecimal INCREMENT = BigDecimal.valueOf(0.05);

    @OneToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference
    private Employee employee;

    @Column(name = "amount_chf")
    private BigDecimal amountChf;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type")
    private WalletType walletType;

    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "bip32_node")
    private String bip32Node;

    @Column(name = "current_address_index")
    private Long currentAddressIndex;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public BigDecimal getAmountChf() {
        return round(amountChf.min(getMaxPayableAmount()));
    }

    private BigDecimal getMaxPayableAmount() {
        return employee.getBruttoSalary()
                .divide(BigDecimal.valueOf(100))
                .multiply(MAX_PAYOUT_PERCENTAGE)
                .setScale(2 , RoundingMode.FLOOR);
    }

    public static BigDecimal round(BigDecimal value) {
        BigDecimal divided = value.divide(INCREMENT, 0, RoundingMode.FLOOR);
        return divided.multiply(INCREMENT);
    }

    public void setAmountChf(BigDecimal amountChf) {
        this.amountChf = amountChf;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getBip32Node() {
        return bip32Node;
    }

    public void setBip32Node(String bip32Node) {
        this.bip32Node = bip32Node;
    }

    public Long getCurrentAddressIndex() {
        return currentAddressIndex;
    }

    public void setCurrentAddressIndex(Long currentAddressIndex) {
        this.currentAddressIndex = currentAddressIndex;
    }

    public BigDecimal getPercentage() {
        return BigDecimal.valueOf(100).divide(employee.getBruttoSalary()).multiply(amountChf);
    }

    public void setAmountChfFromPercentage(BigDecimal currentPercentage) {
        setAmountChf(round(employee.getBruttoSalary()
                        .divide(BigDecimal.valueOf(100))
                        .multiply(currentPercentage.min(MAX_PAYOUT_PERCENTAGE))));
    }
}
