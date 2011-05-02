import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class DatabaseTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		/*Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DATABASE_NAME);
		Statement statement = connection.createStatement();
		
		//ResultSet result = statement.executeQuery("select * from " + Constants.TABLE_NAME + " where building='East Andrews' and roomNumber='256';");
		
		ResultSet result = statement.executeQuery("select * from " + Constants.ROOM_TABLE + " where campusArea='Keeney';");

		while (result.next()) {
			System.out.println(result.getString("building") + " " + result.getString("roomNumber") + " " 
					+ result.getInt("y2006") + " " + result.getInt("y2007") + " " + result.getInt("y2008") 
					+ " " + result.getInt("y2009") + " " + result.getInt("y2010") + " " + result.getInt("y2011"));
		}

		result.close();
		connection.close();*/
		
		System.out.println(Database.getMaxLotteryNumber());
		
		//ArrayList<CampusArea> areas = (ArrayList<CampusArea>) Database.getCampusAreas();
		//for(int i = 0; i < areas.size(); i++) System.out.println(areas.get(i).toString());
		
		//LinkedList<Dorm> locations = (LinkedList<Dorm>) areas.get(0).getDorms();
		
		/*Dorm d1 = new Dorm("Grad Center A");
		Dorm d2 = new Dorm("Grad Center B");
		Dorm d3 = new Dorm("Grad Center C");
		Dorm d4 = new Dorm("Grad Center D");
		//Dorm newDormB = new Dorm("Gregorian Quad B");
		LinkedList<Dorm> locations = new LinkedList<Dorm>();
		locations.add(d1);
		locations.add(d2);
		locations.add(d3);
		locations.add(d4);
		//locations.add(newDormB);
		
		int[] years = {2006, 2007, 2008, 2009, 2010, 2011};
		RoomList rooms = Database.getResults(locations, 5, years, true, true);
		for(Room r: rooms) {
			System.out.println(r.toString());
		}*/
		
		/*Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DATABASE_NAME);
		Statement statement = connection.createStatement();
		
		ResultSet result = statement.executeQuery("select * from " + Constants.SEMESTER_TABLE + " where number=415;");
		while (result.next()) {
			System.out.println(result.getInt("number") + " " + result.getInt("y2006") + " " 
					+ result.getInt("y2007") + " " + result.getInt("y2008") + " " + result.getInt("y2009")
					+ " " + result.getInt("y2010") + " " + result.getInt("y2011"));
		}*/

//		System.out.println(Database.semesterFromLotteryNumber(792));
//		System.out.println(Database.lotteryNumberFromSemester(8));
		
		/*Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DATABASE_NAME);
		Statement statement = connection.createStatement();
		
		ResultSet result = statement.executeQuery("select distinct building from " + Constants.ROOM_TABLE + ";");
		while (result.next()) {
			System.out.println(result.getString("building"));
		}*/
		
		/*Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DATABASE_NAME);
		Statement statement = connection.createStatement();
		
		ResultSet result = statement.executeQuery("select distinct building from " + Constants.GENDER_TABLE + ";");
		while (result.next()) {
			System.out.println(result.getString("building"));
		}*/
	}
}
