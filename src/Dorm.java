
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
		// TODO Auto-generated method stub
		return _name.compareTo(o.getName());
	}
}
