package sala;

import entities.Abonat;
import entities.Antrenor;
import entities.Persoana;

import java.util.Map.Entry;
import java.util.Optional;

import static enums.PersoaneEnum.ABONAT;
import static utils.Utils.*;

public class PlatformaAutentificare {

    private static final String CORRECT_CREDENTIALS = "Credentialele au fost verificate cu succes!";

    private static PlatformaAutentificare platformaDeAutentificare = new PlatformaAutentificare();

    private PlatformaAutentificare() {
    }

    public static PlatformaAutentificare getPlatformaDeAutentificare() {
        return platformaDeAutentificare;
    }

    public String signUpPersoana(Persoana persoana, String password, String confirmationPassword) {
        String emailPersoana = persoana.getEmail();
        String message = verifyCredentials(password, confirmationPassword, emailPersoana);
        if (CORRECT_CREDENTIALS.equalsIgnoreCase(message)) {
            getPersoane().add(persoana);
            getPersoaneEmailPasswordMap().putIfAbsent(emailPersoana, password);
            getPersoaneEmailLoginFlagMap().putIfAbsent(emailPersoana, false);
            if (persoana instanceof Abonat) {
                return String.format("Abonatul %s a fost adaugat!", persoana.getNume());
            } else {
                return String.format("Antrenorul %s a fost adaugat!", persoana.getNume());
            }
        }
        return message;
    }

    //O singura persoana poate fi conectata in aplicatie (fie ca este abonat sau antrenor)
    public String loginPersoana(String email, String password, String tipPersoana) {
        //verifica daca exista persoana cu adresa de email
        Optional<Persoana> persoanaCurenta = getPersoana(email);
        if (persoanaCurenta.isEmpty()) {
            if (tipPersoana.equalsIgnoreCase(ABONAT.getTipPersoana())) {
                return "Abonatul nu exista!";
            }
            return "Antrenorul nu exista!";
        }
        //in caz afirmativ se verifica validitatea comenzii
        //ex: LOGIN_ABONAT si restul datelor apartin de exemplu un antrenor
        Persoana persoana = persoanaCurenta.get();
        if (tipPersoana.equalsIgnoreCase(ABONAT.getTipPersoana())) {
            if (persoana instanceof Antrenor) {
                return "Adresa de email nu apartine unui abonat!";
            }
            //se verifica datele abonatului
            return loginAbonat(email, password);
        } else {
            if (persoana instanceof Abonat) {
                return "Adresa de email nu apartine unui antrenor!";
            }
            //se verifica datele antrenorului
            return loginAntrenor(email, password);
        }
    }

    private String loginAbonat(String email, String password) {
        if (!getPersoaneEmailPasswordMap().get(email).equals(password)) {
            return "Parola incorecta!";
        }
        //se verifica daca este cineva deja conectat; daca da acesta poate fi ori un abonat, ori un antrenor
        Entry<String, Boolean> entry = isAnyoneLoggedIn(getPersoaneEmailLoginFlagMap());
        if (entry != null) {
            return verificaCineEsteDejaConectatInAplicatie(entry.getKey());
        } else {
            getPersoaneEmailLoginFlagMap().put(email, true);
            return String.format("Abonatul cu adresa %s este acum logat!", email);
        }
    }

    private String loginAntrenor(String email, String password) {
        if (!getPersoaneEmailPasswordMap().get(email).equals(password)) {
            return "Parola incorecta!";
        }
        //se verifica daca este cineva deja conectat; daca da acesta poate fi ori un abonat, ori un antrenor
        Entry<String, Boolean> entry = isAnyoneLoggedIn(getPersoaneEmailLoginFlagMap());
        if (entry != null) {
            return verificaCineEsteDejaConectatInAplicatie(entry.getKey());
        } else {
            getPersoaneEmailLoginFlagMap().put(email, true);
            return String.format("Antrenorul cu adresa %s este acum logat!", email);
        }
    }

    private String verificaCineEsteDejaConectatInAplicatie(String email) {
        Persoana persoanaConectata = getPersoana(email).get();
        if (persoanaConectata instanceof Antrenor) {
            return "Alt antrenor este deja conectat!";
        }
        return "Alt abonat este deja conectat!";
    }

    public String logoutPersoana(String email, String tipPersoana) {
        if (tipPersoana.equalsIgnoreCase(ABONAT.getTipPersoana())) {
            return logoutAbonat(email);
        }
        return logoutAntrenor(email);
    }

    private String logoutAbonat(String email) {
        Optional<Persoana> persoanaCurenta = getPersoana(email);
        //daca adresa de email nu exista in sistem
        if (persoanaCurenta.isEmpty()) {
            return "Adresa de email nu exista!";
        }
        Persoana persoana = persoanaCurenta.get();
        //daca adresa de email exista dar apartine unui antrenor si noi vrem sa dam logout unui abonat
        if (persoana instanceof Antrenor) {
            return "Adresa de email apartine unui antrenor si nu a unui abonat!";
        }
        //daca adresa de email apartine unui abonat, dar nu este cel logat
        if (!getPersoaneEmailLoginFlagMap().get(email)) {
            return "Abonatul nu era conectat!";
        }
        //abonatul este deconectat cu succes
        getPersoaneEmailLoginFlagMap().put(email, false);
        return String.format("Abonatul %s a fost deconectat!", email);
    }

    private String logoutAntrenor(String email) {
        Optional<Persoana> persoanaCurenta = getPersoana(email);
        //daca adresa de email nu exista in sistem
        if (persoanaCurenta.isEmpty()) {
            return "Adresa de email nu exista!";
        }
        Persoana persoana = persoanaCurenta.get();
        //daca adresa de email exista dar apartine unui abonat si noi vrem sa dam logout unui antrenor
        if (persoana instanceof Abonat) {
            return "Adresa de email apartine unui abonat si nu a unui antrenor!";
        }
        //daca adresa de email apartine unui antrenor, dar nu este cel logat
        if (!getPersoaneEmailLoginFlagMap().get(email)) {
            return "Antrenorul nu era conectat!";
        }
        //antrenorul este deconectat cu succes
        getPersoaneEmailLoginFlagMap().put(email, false);
        return String.format("Antrenorul %s a fost deconectat!", email);
    }
}
