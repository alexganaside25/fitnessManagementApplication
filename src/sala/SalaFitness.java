package sala;

import comparators.AbonatiComparator;
import comparators.AntrenoriComparator;
import entities.Abonat;
import entities.Antrenor;
import entities.Persoana;
import sala.newsletter.Newsletter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;

import static utils.Utils.*;

public class SalaFitness {

    private Newsletter newsletter = new Newsletter();

    private List<Persoana> persoaneInSala = new ArrayList<>();
    private Queue<Persoana> persoaneInAsteptare = new LinkedList<>();

    private static SalaFitness salaFitness = new SalaFitness();

    private static final int MAX_PERSOANE_IN_SALA = 10;
    private static final String ABONAT = "abonat";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/examen_java1p";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private SalaFitness() {
    }

    public static SalaFitness getSalaFitness() {
        return salaFitness;
    }

    //INCREMENT_PROGRES
    public String incrementProgres(int value) {
        Entry<String, Boolean> entry = isAnyoneLoggedIn(getPersoaneEmailLoginFlagMap());
        if (entry == null) {
            return "Nu exista nici un abonat logat!";
        } else {
            String emailCurent = entry.getKey();
            Optional<Persoana> persoanaCurenta = getPersoana(emailCurent);
            if (persoanaCurenta.get() instanceof Antrenor) {
                return "Nu exista un abonat logat! Un antrenor este logat!";
            }
            Abonat abonatCurent = (Abonat) persoanaCurenta.get();
            int progresCurent = abonatCurent.getProgres();
            int sum = progresCurent + value;
            if (sum > 10) {
                return String.format("Nu se poate face incrementarea. Progresul total ar fi: %s", sum);
            } else {
                setProgresNou(emailCurent, sum);
            }
            return String.format("Progresul abonatului %s a fost actualizat!", emailCurent);
        }
    }

    //DECREMENT_PROGRES
    public String decrementProgres(int value) {
        Entry<String, Boolean> entry = isAnyoneLoggedIn(getPersoaneEmailLoginFlagMap());
        if (entry == null) {
            return "Nu exista nici un abonat logat!";
        } else {
            String emailCurent = entry.getKey();
            Optional<Persoana> persoanaCurenta = getPersoana(emailCurent);
            if (persoanaCurenta.get() instanceof Antrenor) {
                return "Nu exista un abonat logat! Un antrenor este logat!";
            }
            Abonat abonatCurent = (Abonat) persoanaCurenta.get();
            int progresCurent = abonatCurent.getProgres();
            int difference = progresCurent - value;
            if (difference < 0) {
                return String.format("Nu se poate face decrementarea. Progresul total ar fi: %s", difference);
            } else {
                setProgresNou(emailCurent, difference);
            }
            return String.format("Progresul abonatului %s a fost actualizat!", emailCurent);
        }
    }

    //ADAUGA_ANTRENOR
    public String adaugaAntrenor(String email) {
        Entry<String, Boolean> entry = isAnyoneLoggedIn(getPersoaneEmailLoginFlagMap());
        if (entry == null) {
            return "Nu exista nici un abonat logat!";
        } else if (getPersoana(entry.getKey()).get() instanceof Antrenor) {
            return "Un antrenor este logat, nu un abonat!";
        }
        Optional<Persoana> persoanaCurenta = getPersoana(email);
        if (persoanaCurenta.isEmpty()) {
            return String.format("Nu exista antrenorul cu emailul %s", email);
        } else {
            Persoana persoana = persoanaCurenta.get();
            if (persoana instanceof Abonat) {
                return "Adresa de email din comanda apartine unui abonat, nu a unui antrenor!";
            }
            StringBuilder result = new StringBuilder();
            Antrenor antrenorCurent = (Antrenor) persoana;
            if (antrenorCurent.getNrAbonati() > 0) {
                List<String> abonati;
                if (getAntrenorCuAbonatiMap().get(email) == null) {
                    abonati = new ArrayList<>();
                } else {
                    abonati = getAntrenorCuAbonatiMap().get(email);
                    String emailAbonat = entry.getKey();
                    if (abonati.stream()
                            .anyMatch(e -> e.equals(emailAbonat))) {
                        return String.format("Abonatul curent il are deja asignat pe antrenorul %s", email);
                    }
                }
                result.append(verificaDacaAbonatulAreDejaUnAntrenorDiferitAlocat(entry.getKey()));
                abonati.add(entry.getKey());
                getAntrenorCuAbonatiMap().put(email, abonati);
                setNrAbonatiNou(email);
                return result.append(String.format("Antrenorul %s il are acum ca abonat pe %s!", email, entry.getKey())).toString();
            }
            return "Antrenorul nu mai are locuri libere!";
        }
    }

    private String verificaDacaAbonatulAreDejaUnAntrenorDiferitAlocat(String email) {
        boolean gasit = false;
        for (Entry<String, List<String>> entry : getAntrenorCuAbonatiMap().entrySet()) {
            String emailAntrenorCurent = entry.getKey();
            Optional<String> abonat = entry.getValue().stream()
                    .filter(e -> e.equals(email))
                    .findFirst();
            if (abonat.isPresent()) {
                String emailAbonat = abonat.get();
                entry.getValue().remove(emailAbonat);
                Antrenor antrenorCurent = (Antrenor) getPersoana(emailAntrenorCurent).get();
                antrenorCurent.setNrAbonati(antrenorCurent.getNrAbonati() + 1);
                gasit = true;
                break;
            }
        }
        if (gasit) {
            return "Abonatul are un antrenor alocat pe care urmeaza sa il schimbe!\n";
        }
        return "Abonatul nu are inca un antrenor alocat!\n";
    }

    //VIZUALIZARE_ABONATII_MEI
    public String vizualizareAbonatiiMei() {
        Entry<String, Boolean> entry = isAnyoneLoggedIn(getPersoaneEmailLoginFlagMap());
        if (entry == null) {
            return "Nu exista nici un antrenor logat!";
        } else if (getPersoana(entry.getKey()).get() instanceof Abonat) {
            return "Un abonat este logat, nu un antrenor!";
        }
        String emailAntrenorCurent = entry.getKey();
        List<String> abonatiiMei = getAntrenorCuAbonatiMap().get(emailAntrenorCurent);
        if (abonatiiMei != null) {
            StringBuilder result = new StringBuilder("Abonatii mei sunt:");
            for (String abonat : abonatiiMei) {
                result.append(" ").append(abonat).append(",");
            }
            return result.toString().replaceFirst(".$", "");
        }
        return "Antrenorul nu are abonati!";
    }

    //SUBSCRIBE_ABONAT & SUBSCRIBE_ANTRENOR
    public String subscribePersoana(String tipPersoana) {
        if (tipPersoana.equalsIgnoreCase(ABONAT)) {
            return subscribeAbonat();
        }
        return subscribeAntrenor();
    }

    private String subscribeAbonat() {
        Entry<String, Boolean> entry = isAnyoneLoggedIn(getPersoaneEmailLoginFlagMap());
        if (entry == null) {
            return "Nu exista nici un abonat logat!";
        } else if (getPersoana(entry.getKey()).get() instanceof Antrenor) {
            return "Un antrenor este logat, nu un abonat!";
        }
        String emailAbonatCurent = entry.getKey();
        Optional<Persoana> persoana = getPersoana(emailAbonatCurent);

        boolean esteAbonat = persoana.get().subscribe(newsletter);
        if (esteAbonat) {
            return String.format("Abonatul %s urmareste deja newsletter-ul!", emailAbonatCurent);
        }
        return String.format("Abonatul cu adresa de email %s a fost abonat la newsletter!", emailAbonatCurent);
    }

    private String subscribeAntrenor() {
        Entry<String, Boolean> entry = isAnyoneLoggedIn(getPersoaneEmailLoginFlagMap());
        if (entry == null) {
            return "Nu exista nici un antrenor logat!";
        } else if (getPersoana(entry.getKey()).get() instanceof Abonat) {
            return "Un abonat este logat, nu un antrenor!";
        }
        String emailAntrenorCurent = entry.getKey();
        Optional<Persoana> persoana = getPersoana(emailAntrenorCurent);

        boolean esteAbonat = persoana.get().subscribe(newsletter);
        if (esteAbonat) {
            return String.format("Antrenorul %s urmareste deja newsletter-ul!", emailAntrenorCurent);
        }
        return String.format("Antrenorul cu adresa de email %s a fost abonat la newsletter!", emailAntrenorCurent);
    }

    //ADAUGA_NEWS
    public String adaugaNews(String mesaj) {
        newsletter.setNews(mesaj);
        StringBuilder result = new StringBuilder("A fost adaugata stirea cu mesajul: " + mesaj + "\n");
        return result.append(newsletter.notifyObservers()).toString().trim();
    }

    //INTRA_IN_SALA
    public String intraInSala(String email) {
        Optional<Persoana> persoanaCurenta = getPersoana(email);
        if (persoanaCurenta.isPresent()) {
            Persoana persoana = persoanaCurenta.get();
            return verificaDacaPersoanaPoateIntraInSala(email, persoana);
        }
        return String.format("Nu exista abonat sau antrenor cu adresa de email %s", email);
    }

    private String verificaDacaPersoanaPoateIntraInSala(String email, Persoana persoana) {
        if (getPersoanaInSala(email).isPresent()) {
            return String.format("Persoana cu adresa de email %s este deja in sala!", email);
        }
        if (esteSalaDisponibila()) {
            List<LocalDateTime> persoanaDateTimeList = persoana.getDate();
            persoanaDateTimeList.add(LocalDateTime.now());
            persoana.setDate(persoanaDateTimeList);
            persoaneInSala.add(persoana);
            return String.format("Persoana cu adresa de email %s a intrat in sala!", email);
        }
        persoaneInAsteptare.add(persoana);
        return "Sala este deja plina! Sunteti pus in coada de asteptare!";
    }

    //IESE_DIN_SALA
    public String ieseDinSala(String email) {
        Optional<Persoana> persoanaCurenta = getPersoana(email);
        if (persoanaCurenta.isPresent()) {
            return verificaDacaAbonatulPoateSaIasaDinSala(email);
        }
        return String.format("Nu exista abonat sau antrenor cu adresa de email %s", email);
    }

    private String verificaDacaAbonatulPoateSaIasaDinSala(String email) {
        Optional<Persoana> persoanaCurenta = getPersoanaInSala(email);
        if (persoanaCurenta.isPresent()) {
            Persoana persoana = persoanaCurenta.get();
            persoaneInSala.remove(persoana);
            StringBuilder result = new StringBuilder();
            if (persoana instanceof Abonat) {
                result.append(String.format("Abonatul cu adresa de email %s a iesit din sala!\n", email));
            } else {
                result.append(String.format("Antrenorul cu adresa de email %s a iesit din sala!\n", email));
            }
            return adaugaInSalaOPersoanaDinCoada(result);
        }
        return String.format("Persoana cu adresa de email %s nu este in sala!", email);
    }

    private String adaugaInSalaOPersoanaDinCoada(StringBuilder result) {
        if (persoaneInAsteptare.size() > 0) {
            Persoana persoana = persoaneInAsteptare.peek();
            persoaneInAsteptare.remove(persoana);
            List<LocalDateTime> persoanaDateTimeList = persoana.getDate();
            persoanaDateTimeList.add(LocalDateTime.now());
            persoana.setDate(persoanaDateTimeList);
            persoaneInSala.add(persoana);
            if (persoana instanceof Abonat) {
                result.append(String.format("Abonatul cu adresa de email %s a intrat in sala!", persoana.getEmail()));
            } else {
                result.append(String.format("Antrenorul cu adresa de email %s a intrat in sala!", persoana.getEmail()));
            }
        } else {
            result.append("Nu exista persoane in asteptare!");
        }
        return result.toString();
    }

    //VIZUALIZARE_PERSOANA_CU_ANTRENOR
    public String vizualizarePersoaneCuAntrenor() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : getAntrenorCuAbonatiMap().entrySet()) {
            result.append(entry.getKey()).append(":");
            for (String emailAbonat : entry.getValue()) {
                result.append(" ").append(emailAbonat).append(",");
            }
            result = new StringBuilder(result.toString().replaceFirst("[^:]$", "")).append("\n");
        }
        return result.toString().trim();
    }

    //VIZUALIZARE_ABONATI
    public String vizualizareAbonati() {
        List<Abonat> abonatiList = getListaAbonati();
        abonatiList.sort(new AbonatiComparator());

        StringBuilder result = new StringBuilder();
        for (Abonat abonat : abonatiList) {
            result.append(abonat.toString()).append('\n');
        }
        if (result.length() == 0) {
            return "Nu exista abonati!";
        }
        return result.toString().trim();
    }

    //VIZUALIZARE_ANTRENORI
    public String vizualizareAntrenori() {
        List<Antrenor> antrenoriList = getListaAntrenori();
        antrenoriList.sort(new AntrenoriComparator());

        StringBuilder result = new StringBuilder();
        for (Antrenor antrenor : antrenoriList) {
            result.append(antrenor.toString()).append('\n');
        }
        if (result.length() == 0) {
            return "Nu exista antrenori!";
        }
        return result.toString().trim();
    }

    //PERSISTA_ABONATI
    public String persistaAbonati() {
        List<Abonat> abonatiList = getListaAbonati();

        String inserareAbonati = "INSERT IGNORE INTO abonati SELECT(SELECT MAX(id)+1 from abonati),?,?,?,?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(inserareAbonati)) {
            for (Abonat abonat : abonatiList) {
                preparedStatement.setString(1, abonat.getEmail());
                preparedStatement.setString(2, abonat.getNume());
                preparedStatement.setString(3, getPersoaneEmailPasswordMap().get(abonat.getEmail()));
                preparedStatement.setInt(4, abonat.getProgres());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "Abonatii au fost salvati in baza de date!";
    }

    //PERSISTA_ANTRENORI
    public String persistaAntrenori() {
        List<Antrenor> antrenoriList = getListaAntrenori();

        String inserareAntrenori = "INSERT IGNORE INTO antrenori SELECT(SELECT MAX(id)+1 from antrenori),?,?,?,?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(inserareAntrenori)) {
            for (Antrenor antrenor : antrenoriList) {
                preparedStatement.setString(1, antrenor.getEmail());
                preparedStatement.setString(2, antrenor.getNume());
                preparedStatement.setString(3, getPersoaneEmailPasswordMap().get(antrenor.getEmail()));
                preparedStatement.setInt(4, antrenor.getNrAbonati());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return "Antrenorii au fost salvati in baza de date!";
    }

    private boolean esteSalaDisponibila() {
        return persoaneInSala.size() + 1 <= MAX_PERSOANE_IN_SALA;
    }

    private Optional<Persoana> getPersoanaInSala(String emailPersoana) {
        return persoaneInSala.stream()
                .filter(e -> e.getEmail().equals(emailPersoana))
                .findFirst();
    }
}
