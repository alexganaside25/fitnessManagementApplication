package sala.newsletter;

import entities.Persoana;

import java.util.ArrayList;
import java.util.List;

public class Newsletter implements Subject {
    private String news;
    private List<Persoana> persoaneInscriseLaNewsletter = new ArrayList<>();

    public void setNews(String news) {
        this.news = news;
    }

    @Override
    public boolean addPersoana(Persoana persoana) {
        if (persoaneInscriseLaNewsletter.contains(persoana)) {
            return true;
        }
        persoaneInscriseLaNewsletter.add(persoana);
        return false;
    }

    @Override
    public String notifyObservers() {
        StringBuilder result = new StringBuilder();
        for (Persoana persoana : persoaneInscriseLaNewsletter) {
            result.append(persoana.update(news)).append("\n");
        }
        return result.toString();
    }
}
