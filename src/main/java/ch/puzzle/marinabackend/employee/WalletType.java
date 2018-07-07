package ch.puzzle.marinabackend.employee;

public enum WalletType {
    MANUAL("manual"),
    BIP_32("bip32");

    private String name;

    private WalletType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
