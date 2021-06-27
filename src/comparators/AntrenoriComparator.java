package comparators;

import entities.Antrenor;

import java.util.Comparator;

public class AntrenoriComparator implements Comparator<Antrenor> {
    @Override
    public int compare(Antrenor a1, Antrenor a2) {
        if (a1.getNrAbonati() != a2.getNrAbonati()) {
            return a1.getNrAbonati() - a2.getNrAbonati();
        }
        return a1.getNume().compareTo(a2.getNume());
    }
}