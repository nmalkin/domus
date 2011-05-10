
public class Dorm implements Comparable<Dorm> {
	/** the name of this dorm */
	private String _name;
	
	public Dorm(String name) {
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
	
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(Dorm o) {
		return _name.compareTo(o.getName());
	}
	
	@Override
	public int hashCode() {
		return _name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(! (o instanceof Dorm)) return false;
		else {
			return _name.equals(((Dorm) o).getName());
		}
	}
}
