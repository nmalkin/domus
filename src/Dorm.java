
public class Dorm implements Comparable<Dorm> {
	/** the name of this Dorm */
	private String _name;
	
	/** the genderNeutral status of this Dorm */
	private boolean _isGenderNeutral;
	
	/** sophomore only status of this Dorm */
	private boolean _isSophomoreOnly;
	
	public Dorm(String name) {
		_name = name;
		_isGenderNeutral = false;
		_isGenderNeutral = false;
	}
	
	public String getName() {
		return _name;
	}
	
	public boolean isGenderNeutral() {
		return _isGenderNeutral;
	}
	
	public boolean isSophomoreOnly() {
		return _isSophomoreOnly;
	}
	
	public void setGenderNeutral(boolean isGenderNeutral) {
		_isGenderNeutral = true;
	}
	
	public void setSophomoreOnly(boolean isSophomoreOnly) {
		_isSophomoreOnly = isSophomoreOnly;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int compareTo(Dorm o) {
		// TODO Auto-generated method stub
		return _name.compareTo(o.getName());
	}


}
