
public enum Gender {
	MALE, FEMALE;
	
	public String toString() {
		String r = "";
		
		switch(this) {
			case MALE:
				r = "M";
				break;
			case FEMALE:
				r = "F";
				break;
		}
		
		return r;
	}
}
