import java.awt.Point;

public class Person {
	/** What is my name? */
	private String _name;
	
	/** What is my gender? */
	private Gender _gender;
	
	/** Where on the canvas am I located? */
	private Point _position;
	
	public Person(String name, Gender gender) {
		_name = name;
		_gender = gender;
	}
	
	public String getName() {
		return _name;
	}
	
	public Gender getGender() {
		return _gender;
	}
	
	public Point getPosition() { 
		return _position;
	}
	
	public void setPosition(Point p) {
		_position = p;
	}
	
	public String toString() {
		return getName() + "(" + this.getGender().toString() + ")";
	}
}
