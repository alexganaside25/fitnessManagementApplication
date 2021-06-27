package sala.newsletter;

import entities.Persoana;

public interface Subject {
    boolean addPersoana(Persoana persoana);

    String notifyObservers();
}
