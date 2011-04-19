import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A CampusArea is simply a collection of Dorms that can be found in  
 * that particular area of campus.
 *
 */
public class CampusArea extends AbstractCollection<Dorm> {
	/** the dorms that constitute this area */
	private Collection<Dorm> _dorms;
	
	/** a name to refer to this area */
	private String _name;
	
	public CampusArea(String name) {
		_dorms = new LinkedList<Dorm>();
		_name = name;
	}
	
	public CampusArea(Collection<Dorm> dorms, String name) {
		_dorms = dorms;
		_name = name;
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
	
	public String getName() {
		return _name;
	}
	
	public String toString() {
		return getName() + " " + _dorms.toString();
	}
}
