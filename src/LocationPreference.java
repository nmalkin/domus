import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Groups can express their preferences for locations by listing which
 * dorms will be accessible for their (sub-)group.
 * Consequently, a LocationPreference is a collection of Dorms
 * (that are acceptable to a group).
 *
 */
public class LocationPreference extends AbstractCollection<Dorm> {
	private Collection<Dorm> _dorms;
	
	public LocationPreference() {
		_dorms = new LinkedList<Dorm>();
	}
	
	public LocationPreference(Collection<Dorm> dorms) {
		_dorms = dorms;
	}
	
	@Override
	public boolean add(Dorm e) {
		return _dorms.add(e);
	}

	@Override
	public Iterator<Dorm> iterator() {
		return _dorms.iterator();
	}

	@Override
	public int size() {
		return _dorms.size();
	}
}
