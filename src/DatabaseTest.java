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
		
		ArrayList<CampusArea> areas = (ArrayList<CampusArea>) Database.getCampusAreas();
		for(int i = 0; i < areas.size(); i++) System.out.println(areas.get(i).toString());
		
		//LinkedList<Dorm> locations = (LinkedList<Dorm>) areas.get(0).getDorms();
		
		/*Dorm harkness = new Dorm("Harkness House");
		//Dorm newDormB = new Dorm("Gregorian Quad B");
		LinkedList<Dorm> locations = new LinkedList<Dorm>();
		locations.add(harkness);
		//locations.add(newDormB);
		
		int[] years = {2010};
		RoomList r = Database.getResults(locations, 2, years, false, true);
		r.print();*/
		
		/*Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DATABASE_NAME);
		Statement statement = connection.createStatement();
		
		ResultSet result = statement.executeQuery("select * from " + Constants.SEMESTER_TABLE + " where number=415;");
		while (result.next()) {
			System.out.println(result.getInt("number") + " " + result.getInt("y2006") + " " 
					+ result.getInt("y2007") + " " + result.getInt("y2008") + " " + result.getInt("y2009")
					+ " " + result.getInt("y2010") + " " + result.getInt("y2011"));
		}*/
		
		/*int[] years = {2007, 2006, 2010};
		System.out.println(Database.semesterFromLotteryNumber(700, years));
		System.out.println(Database.lotteryNumberFromSemester(8, years));*/
		
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
