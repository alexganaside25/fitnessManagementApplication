package entities;

import sala.newsletter.Observer;
import sala.newsletter.Subject;

import java.time.LocalDateTime;
import java.util.List;

public class Persoana implements Observer {
    private String email;
    private String nume;
    private List<LocalDateTime> date;
    private int id;

    public Persoana(String email, String nume, List<LocalDateTime> date, int id) {
        this.email = email;
        this.nume = nume;
        this.date = date;
        this.id = id;
    }

    @Override
    public boolean subscribe(Subject subject) {
        return subject.addPersoana(this);
    }

    @Override
    public String update(String news) {
        return String.format("A fost notificata persoana cu adresa de email %s de stirea cu mesajul: %s", email, news);
    }

    public String getEmail() {
        return email;
    }

    public String getNume() {
        return nume;
    }

    public List<LocalDateTime> getDate() {
        return date;
    }

    public void setDate(List<LocalDateTime> date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }
}
