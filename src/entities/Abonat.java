package entities;

import java.time.LocalDateTime;
import java.util.List;

public class Abonat extends Persoana {
    private int progress;

    public Abonat(String email, String name, List<LocalDateTime> date, int id, int progress) {
        super(email, name, date, id);
        this.progress = progress;
    }

    public int getProgres() {
        return progress;
    }

    public void setProgres(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "Abonat{" +
                "progress=" + progress + " " +
                "email=" + getEmail() + " " +
                "nume=" + getNume() + " " +
                "date=" + getDate() + " " +
                "id=" + getId() + " " +
                '}';
    }
}