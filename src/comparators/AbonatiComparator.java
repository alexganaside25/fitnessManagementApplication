package comparators;

import entities.Abonat;

import java.util.Comparator;

public class AbonatiComparator implements Comparator<Abonat> {
    @Override
    public int compare(Abonat a1, Abonat a2) {
        if (a1.getProgres() != a2.getProgres()) {
            return a2.getProgres() - a1.getProgres();
        }
        return a1.getNume().compareTo(a2.getNume());
    }
}