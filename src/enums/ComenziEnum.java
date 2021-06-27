package enums;

import java.util.Arrays;

public enum ComenziEnum {
    SIGNUP_ABONAT,
    SIGNUP_ANTRENOR,
    LOGIN_ABONAT,
    LOGIN_ANTRENOR,
    LOGOUT_ABONAT,
    LOGOUT_ANTRENOR,
    INCREMENT_PROGRES,
    DECREMENT_PROGRES,
    ADAUGA_ANTRENOR,
    VIZUALIZARE_ABONATII_MEI,
    SUBSCRIBE_ABONAT,
    SUBSCRIBE_ANTRENOR,

    ADAUGA_NEWS,
    INTRA_IN_SALA,
    IESE_DIN_SALA,
    VIZUALIZARE_PERSOANE_CU_ANTRENOR,
    VIZUALIZARE_ABONATI,
    VIZUALIZARE_ANTRENORI,
    PERSISTA_ABONATI,
    PERSISTA_ANTRENORI;

    public static ComenziEnum getCommand(String command) {
        return Arrays.stream(ComenziEnum.values())
                .filter(p -> p.name().equals(command))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Comanda " + command + " este incorecta!"));
    }
}
