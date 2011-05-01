import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Database {
	private static final Database INSTANCE = new Database();
	private static Connection connection;
	private static Statement statement;

	/** 
	 * all dorms known to the database, with the key being their name
	 * (as referenced in the database)
	 */
	private HashMap<String, Dorm> _dorms;

	private Database() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DATABASE_NAME);
			statement = connection.createStatement();

			_dorms = new HashMap<String, Dorm>();
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: missing .jar file");
		} catch (SQLException e) {
			System.out.println("ERROR: unable to connect to database " + Constants.DATABASE_NAME);
		}
	}

	/**
	 * Returns a Collection with all the CampusAreas (and Dorms)
	 * known in the database.
	 * 
	 * @return
	 */
	public static Collection<CampusArea> getCampusAreas() {
		ArrayList<CampusArea> campusAreas = new ArrayList<CampusArea>();

		try {
			ResultSet areas = statement.executeQuery("select distinct campusArea from " + Constants.CAMPUS_AREA_TABLE + ";");

			while (areas.next()) {
				CampusArea area = new CampusArea(areas.getString("campusArea"));
				campusAreas.add(area);
			}

			areas.close();

			ResultSet dorms = statement.executeQuery("select distinct building, campusArea from " + Constants.CAMPUS_AREA_TABLE + ";");

			while(dorms.next()) {
				Dorm dorm = new Dorm(dorms.getString("building"));
				for(CampusArea ca : campusAreas) {
					if(dorms.getString("campusArea").equals(ca.getName())) {
						INSTANCE._dorms.put(dorm.getName(), dorm);
						ca.add(dorm);
						break;
					}
				}
			}

			dorms.close();
		} catch (SQLException e) {
			System.out.println("ERROR: unable to retrieve campus areas");
		}

		return campusAreas;
	}

	private boolean isGenderNeutral(Dorm d) throws SQLException {
		ResultSet neutral = statement.executeQuery("select building='" + d.getName() + "' from " + Constants.GENDER_TABLE + ";");
		return neutral.next();
	}

	private boolean isSophomoreOnly(Dorm d) throws SQLException {
		ResultSet sophomore = statement.executeQuery("select building='" + d.getName() + "' from " + Constants.SOPHOMORE_TABLE + ";");
		return sophomore.next();
	}

	/**
	 * Returns all the rooms that satisfy the given conditions.
	 * 
	 * @param locations
	 * @param occupancy
	 * @param years
	 * @param genderNeutral
	 * @param sophomoreEligible
	 * @return
	 * @throws SQLException 
	 */
	public static RoomList getResults(Collection<Dorm> locations, int occupancy, int[] years, boolean genderNeutral, boolean sophomoreEligible) {
		RoomList rooms = new RoomList();

		try {
			for(Dorm d: locations) {
				if((sophomoreEligible || !INSTANCE.isSophomoreOnly(d)) && (!genderNeutral || INSTANCE.isGenderNeutral(d))) { 
					ResultSet subRooms = statement.executeQuery("select * from " + Constants.ROOM_TABLE + " where occupancy=" + occupancy
							+ " and building='" + d.getName() + "';");

					while(subRooms.next()) {
						Room room = new Room(INSTANCE._dorms.get(subRooms.getString("building")), subRooms.getString("roomNumber"));

						for(int i = 0; i < years.length; i++) {
							if(subRooms.getInt("y" + years[i]) != 0) {
								LotteryResult result = new LotteryResult(years[i], subRooms.getInt("y" + years[i]));
								room.addResult(result);
							}
						}

						rooms.add(room);
					}

					subRooms.close();
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR: unable to retrieve results");
		}

		return rooms;
	}

	/**
	 * Given a lottery number,
	 * returns the average semester level for groups with this lottery number.
	 * Looks at settings in State to choose which years to use.
	 * 
	 * @param lotteryNumber the lottery number
	 * @return average semester level for groups with this lottery number
	 * @throws SQLException 
	 */
	public static int semesterFromLotteryNumber(int lotteryNumber) {
		try {
			int[] years = {2006,2007,2008,2009,2010,2011}; //TODO: get years from State
			
			ResultSet semesters = statement.executeQuery("select * from " + Constants.SEMESTER_TABLE + " where number=" + lotteryNumber + ";");
			semesters.next();

			int sum = 0;
			int count = 0;
			for (int i = 0; i < years.length; i++) {
				sum += semesters.getInt("y" + years[i]);

				if(semesters.getInt(years[i] - 2004) != 0) count++;
			}

			return sum / count;
		} catch(SQLException e) {
			//TODO: better handling
			return -1;
		}
	}

	/**
	 * Given a semester level,
	 * returns an approximate lottery number for groups with this semester level.
	 * Looks at settings in State to choose which years to use. 
	 * 
	 * @param lotteryNumber the lottery number
	 * @return average semester level for groups with this lottery number
	 * @throws SQLException 
	 */
	public static int lotteryNumberFromSemester(int semester) {
		int[] years = {2006,2007,2008,2009,2010,2011}; //TODO: get years from State
		
		// still need to incorporate happiness level
		int count = 0;
		int sum = 0;

		try {
		for(int i = 0; i < years.length; i++) {
			ResultSet numbers = statement.executeQuery("select * from " + Constants.SEMESTER_TABLE + " where y" + years[i] + "=" + semester + ";");

			while(numbers.next()) {
				sum += numbers.getInt("number");
				count++;
			}
		}

		return sum / count;
		} catch(SQLException e) { //TODO
			return -1; //TODO: fix this too
		}
	}

	public static void closeDatabase() {
		INSTANCE.close();
	}

	private void close() {
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			System.out.println("ERROR: unable to close database");
		}
	}

}
