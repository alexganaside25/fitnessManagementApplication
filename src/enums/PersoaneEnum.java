package enums;

public enum PersoaneEnum {
    ABONAT("abonat"),
    ANTRENOR("antrenor");

    private String tipPersoana;

    PersoaneEnum(String tipPersoana) {
        this.tipPersoana = tipPersoana;
    }

    public String getTipPersoana() {
        return tipPersoana;
    }
}
