package domus.data;


import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Groups can express their preferences for locations by listing which dorms
 * will be accessible for their (sub-)group. Consequently, a LocationPreference
 * is a collection of Dorms (that are acceptable to a group).
 * 
 */
public class LocationPreference extends AbstractSet<Dorm> {
    private Set<Dorm> _dorms;

    public LocationPreference() {
        _dorms = new HashSet<Dorm>();
    }

    /** a copy constructor */
    public LocationPreference(LocationPreference lp) {
        _dorms = new HashSet<Dorm>(lp);
    }

    @Override
    public boolean add(Dorm e) {
        return _dorms.add(e);
    }

    public boolean remove(Dorm e) {
        return _dorms.remove(e);
    }

    @Override
    public Iterator<Dorm> iterator() {
        return _dorms.iterator();
    }

    @Override
    public int size() {
        return _dorms.size();
    }

    @Override
    public String toString() {
        String r = "| ";
        for (Dorm d : _dorms) {
            r += d + " |";
        }

        return r;
    }
}
