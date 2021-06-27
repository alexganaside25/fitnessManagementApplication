package entities;

import java.time.LocalDateTime;
import java.util.List;

public class Antrenor extends Persoana {
    private int nrAbonati;

    public Antrenor(String email, String name, List<LocalDateTime> date, int id, int nrAbonati) {
        super(email, name, date, id);
        this.nrAbonati = nrAbonati;
    }

    public int getNrAbonati() {
        return nrAbonati;
    }

    public void setNrAbonati(int nrAbonati) {
        this.nrAbonati = nrAbonati;
    }

    @Override
    public String toString() {
        return "Antrenor{" +
                "nrAbonati=" + nrAbonati + " " +
                "email=" + getEmail() + " " +
                "nume=" + getNume() + " " +
                "date=" + getDate() + " " +
                "id=" + getId() + " " +
                '}';
    }
}
