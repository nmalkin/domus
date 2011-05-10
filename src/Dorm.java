
public class Dorm implements Comparable<Dorm> {
	/** the name of this Dorm */
	private String _name;
	
	/** the genderNeutral status of this Dorm */
	private boolean _isGenderNeutral;
	
	public Dorm(String name) {
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
	
	public String toString() {
		return getName();
	}
	
	public boolean isGenderNeutral() {
		return _isGenderNeutral;
	}
	
	public void setGenderNeutral(boolean isGenderNeutral) {
		_isGenderNeutral = true;
	}
	
	@Override
	public int compareTo(Dorm o) {
		// TODO Auto-generated method stub
		return _name.compareTo(o.getName());
	}


}
