package utils;

import entities.Abonat;
import entities.Antrenor;
import entities.Persoana;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    private static List<Persoana> persoane = new ArrayList<>();
    private static Map<String, Boolean> persoaneEmailLoginFlagMap = new HashMap<>();
    private static Map<String, String> persoaneEmailPasswordMap = new HashMap<>();
    private static Map<String, List<String>> antrenorCuAbonatiMap = new HashMap<>();

    public static List<Persoana> getPersoane() {
        return persoane;
    }

    public static Map<String, String> getPersoaneEmailPasswordMap() {
        return persoaneEmailPasswordMap;
    }

    public static Map<String, Boolean> getPersoaneEmailLoginFlagMap() {
        return persoaneEmailLoginFlagMap;
    }

    public static Map<String, List<String>> getAntrenorCuAbonatiMap() {
        return antrenorCuAbonatiMap;
    }

    public static Optional<Persoana> getPersoana(String emailPersoana) {
        return persoane.stream()
                .filter(e -> e.getEmail().equals(emailPersoana))
                .findFirst();
    }

    public static Map.Entry<String, Boolean> isAnyoneLoggedIn(Map<String, Boolean> personEmailLoginFlagMap) {
        Map.Entry<String, Boolean> entry = null;
        for (Map.Entry<String, Boolean> pair : personEmailLoginFlagMap.entrySet()) {
            if (pair.getValue()) {
                entry = pair;
                break;
            }
        }
        return entry;
    }

    public static String verifyCredentials(String password, String confirmationPassword, String emailPersoana) {
        if (!arePasswordsIdentical(password, confirmationPassword)) {
            return "Parole diferite, nu se poate face adaugarea!";
        }
        if (!isPasswordLongEnough(password)) {
            return "Parola prea scurta!";
        }
        if (!isEmailAddressValid(emailPersoana)) {
            return "Adresa de email invalida!";
        }
        Optional<Persoana> persoana = getPersoana(emailPersoana);
        if (persoana.isPresent()) {
            Persoana persoanaCurenta = persoana.get();
            if (persoanaCurenta instanceof Abonat) {
                return "Adresa de email este deja utilizata!\nAbonat deja existent!";
            }
            return "Adresa de email este deja utilizata!\nAntrenor deja existent!";
        }
        return "Credentialele au fost verificate cu succes!";
    }

    public static List<Abonat> getListaAbonati() {
        return persoane.stream()
                .filter(e -> e instanceof Abonat)
                .map(e -> (Abonat) e)
                .collect(Collectors.toList());
    }

    public static List<Antrenor> getListaAntrenori() {
        return persoane.stream()
                .filter(e -> e instanceof Antrenor)
                .map(e -> (Antrenor) e)
                .collect(Collectors.toList());
    }

    public static void setNrAbonatiNou(String email) {
        persoane.stream()
                .filter(el -> el instanceof Antrenor)
                .map(el -> {
                    if (el.getEmail().equals(email)) {
                        ((Antrenor) el).setNrAbonati(((Antrenor) el).getNrAbonati() - 1);
                    }
                    return el;
                })
                .collect(Collectors.toList());
    }

    public static void setProgresNou(String email, int progresNou) {
        persoane.stream()
                .filter(el -> el instanceof Abonat)
                .map(el -> {
                    if (el.getEmail().equals(email)) {
                        ((Abonat) el).setProgres(progresNou);
                    }
                    return el;
                })
                .collect(Collectors.toList());
    }

    private static boolean arePasswordsIdentical(String password, String confirmationPassword) {
        return password.equals(confirmationPassword);
    }

    private static boolean isPasswordLongEnough(String password) {
        return password.length() >= 8;
    }

    private static boolean isEmailAddressValid(String email) {
        String emailRegex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
