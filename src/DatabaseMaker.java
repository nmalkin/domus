import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseMaker {
	Connection connection;
	Statement statement;
	HashMap<String, String> campusAreas;

	public DatabaseMaker() throws SQLException {
		try{
			Class.forName("org.sqlite.JDBC");

			connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DATABASE_NAME);
			statement = connection.createStatement();

			String roomParams = " (building, roomNumber, occupancy, y2006, y2007, y2008, y2009, y2010, y2011)";
			String semesterParams = " (number, y2006, y2007, y2008, y2009, y2010, y2011)";

			statement.executeUpdate("drop table if exists " + Constants.ROOM_TABLE + ";");
			statement.executeUpdate("create table " + Constants.ROOM_TABLE + roomParams);
			
			statement.executeUpdate("drop table if exists " + Constants.CAMPUS_AREA_TABLE + ";");
			statement.executeUpdate("create table " + Constants.CAMPUS_AREA_TABLE + " (building, campusArea)");

			statement.executeUpdate("drop table if exists " + Constants.SEMESTER_TABLE + ";");
			statement.executeUpdate("create table " + Constants.SEMESTER_TABLE + semesterParams);

			statement.executeUpdate("drop table if exists " + Constants.GENDER_TABLE + ";");
			statement.executeUpdate("create table " + Constants.GENDER_TABLE + " (building)");

			statement.executeUpdate("drop table if exists " + Constants.SOPHOMORE_TABLE + ";");
			statement.executeUpdate("create table " + Constants.SOPHOMORE_TABLE + " (building)");

			makeCampusMap();
			makeGenderTable();
			makeSophomoreTable();
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: missing .jar file");
		}
	}

	public void add(String[] values, String year) throws SQLException {
		addToSemesterLevel(Integer.parseInt(values[0]), year, Integer.parseInt(values[4]));

		if(!values[1].contains("No Show") &&  !values[1].contains("Pass") && !values[1].contains("Drop")) {

			PreparedStatement prep;
			ResultSet result = statement.executeQuery("select * from " + Constants.ROOM_TABLE + " where building='" + values[1] + "' and roomNumber='" + values[2] + "';");

			if(result.next()) {
				prep = connection.prepareStatement("update " + Constants.ROOM_TABLE + " set y" + year + "=" + values[0] + " where building='" + values[1] + "' and roomNumber='" + values[2] + "';");
				prep.execute();
			}
			else {
				prep = connection.prepareStatement("insert into " + Constants.ROOM_TABLE + " values (?, ?, ?, ?, ?, ?, ?, ?, ?);");

				prep.setInt(Integer.parseInt(year) - 2002, Integer.parseInt(values[0]));
				prep.setString(1, values[1]);
				prep.setString(2, values[2]);
				prep.setInt(3, Integer.parseInt(values[3]));

				addToCampusAreas(values[1]);
				
				prep.addBatch();
				connection.setAutoCommit(false);
				prep.executeBatch();
				connection.setAutoCommit(true);
			}

			result.close();
		}
	}

	public void addToCampusAreas(String building) throws SQLException {
		ResultSet result = statement.executeQuery("select * from " + Constants.CAMPUS_AREA_TABLE + " where building='" + building + "';");
	
		if(!result.next()) {
			PreparedStatement prep = connection.prepareStatement("insert into " + Constants.CAMPUS_AREA_TABLE + " values (?, ?);");
			
			prep.setString(1, building);
			prep.setString(2, campusAreas.get(building));
			
			prep.addBatch();
			connection.setAutoCommit(false);
			prep.executeBatch();
			connection.setAutoCommit(true);
		}
		
		result.close();
	}
	
	public void addToSemesterLevel(int number, String year, int semester) throws SQLException {
		PreparedStatement prep;
		ResultSet result = statement.executeQuery("select * from " + Constants.SEMESTER_TABLE + " where number=" + number + ";");

		if(result.next()) {
			prep = connection.prepareStatement("update " + Constants.SEMESTER_TABLE + " set y" + year + "=" + semester + " where number=" + number + ";");
			prep.execute();
		}
		else {
			prep = connection.prepareStatement("insert into " + Constants.SEMESTER_TABLE + " values (?, ?, ?, ?, ?, ?, ?);");

			prep.setInt(1, number);
			prep.setInt(Integer.parseInt(year) - 2004, semester);

			prep.addBatch();
			connection.setAutoCommit(false);
			prep.executeBatch();
			connection.setAutoCommit(true);
		}

		result.close();
	}

	private void makeSophomoreTable() throws SQLException {
		PreparedStatement prep;

		for(int i = 0; i < DatabaseConstants.SOPHOMORE_DORMS.length; i++) {
			prep = connection.prepareStatement("insert into " + Constants.SOPHOMORE_TABLE + " values ('" + DatabaseConstants.SOPHOMORE_DORMS[i] + "');");
			prep.execute();
		}
	}

	private void makeGenderTable() throws SQLException {
		PreparedStatement prep;

		for(int i = 0; i < DatabaseConstants.NEUTRAL_DORMS.length; i++) {
			prep = connection.prepareStatement("insert into " + Constants.GENDER_TABLE + " values ('" + DatabaseConstants.NEUTRAL_DORMS[i] + "');");
			prep.execute();
		}
	}

	private void makeCampusMap() {
		campusAreas = new HashMap<String, String>();

		campusAreas.put("East Andrews", "Pembroke");
		campusAreas.put("West Andrews", "Pembroke");
		campusAreas.put("Metcalf", "Pembroke");
		campusAreas.put("Miller", "Pembroke");
		campusAreas.put("Machado", "Pembroke");
		campusAreas.put("Emery", "Pembroke");
		campusAreas.put("Woolley", "Pembroke");
		campusAreas.put("Morriss Hall", "Pembroke");
		campusAreas.put("Champlin", "Pembroke");
		campusAreas.put("New Pembroke #1", "Pembroke");
		campusAreas.put("New Pembroke #2", "Pembroke");
		campusAreas.put("New Pembroke #3", "Pembroke");
		campusAreas.put("New Pembroke #4", "Pembroke");
		campusAreas.put("Plantations House", "Pembroke");
		campusAreas.put("111 Brown Street", "Pembroke");

		campusAreas.put("Hope College", "Main Campus");
		campusAreas.put("Slater Hall", "Main Campus");
		campusAreas.put("Hegeman A", "Main Campus");
		campusAreas.put("Hegeman B", "Main Campus");
		campusAreas.put("Hegeman C", "Main Campus");
		campusAreas.put("Hegeman D", "Main Campus");
		campusAreas.put("Hegeman E", "Main Campus");
		campusAreas.put("Caswell Hall", "Main Campus");
		campusAreas.put("Minden", "Main Campus");

		campusAreas.put("Archibald House", "Keeney");
		campusAreas.put("Bronson House", "Keeney");
		campusAreas.put("Everett House", "Keeney");
		campusAreas.put("Jameson House", "Keeney");
		campusAreas.put("Mead House", "Keeney");
		campusAreas.put("Poland House", "Keeney");

		campusAreas.put("Wayland House", "Wriston");
		campusAreas.put("Sears House", "Wriston");
		campusAreas.put("Olney House", "Wriston");
		campusAreas.put("Marcy House", "Wriston");
		campusAreas.put("Diman House", "Wriston");
		campusAreas.put("Goddard House", "Wriston");
		campusAreas.put("Harkness House", "Wriston");
		campusAreas.put("Chapin House", "Wriston");

		campusAreas.put("Grad Center A", "East Campus");
		campusAreas.put("Grad Center B", "East Campus");
		campusAreas.put("Grad Center C", "East Campus");
		campusAreas.put("Grad Center D", "East Campus");
		campusAreas.put("Young Orchard #10", "East Campus");
		campusAreas.put("Young Orchard #2", "East Campus");
		campusAreas.put("Young Orchard #4", "East Campus");
		campusAreas.put("Barbour Hall", "East Campus");
		campusAreas.put("Barbour Hall Apartments", "East Campus");
		campusAreas.put("Gregorian Quad A", "East Campus");
		campusAreas.put("Gregorian Quad B", "East Campus");
		campusAreas.put("King House", "East Campus");
	}

	public void close () throws SQLException {
		statement.close();
		connection.close();
	}

	public static void main(String[] args) throws SQLException {
		DatabaseMaker maker = new DatabaseMaker();

		File path = new File(DatabaseConstants.PATH_NAME);
		String[] files = path.list();

		for(int i = 0; i < files.length; i++) {
			if(!files[i].contains("~")) {
				String year = files[i].replaceAll(".csv", "");
				System.out.println("reading " + year + " data........");

				CSVReader reader = new CSVReader(DatabaseConstants.PATH_NAME + files[i]);
				ArrayList<String[]> rooms = reader.read();

				for(String[] room: rooms) maker.add(room, year);

				System.out.println("done with " + year + " data");
			}
		}

		maker.close();
	}
}
