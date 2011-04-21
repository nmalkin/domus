import java.util.Collection;
import java.util.Map;


public class Database {
	private static final Database INSTANCE = new Database();
	
	/** 
	 * all dorms known to the database, with the key being their name
	 * (as referenced in the database)
	 */
	private Map<String, Dorm> _dorms;
	
	//TODO: implement
	private Database() {
		// here, the connection to the sql backend is established, etc.
	}
	
	/**
	 * Returns a Collection with all the CampusAreas (and Dorms)
	 * known in the database.
	 * 
	 * @return
	 */
	public static Collection<CampusArea> getCampusAreas() {
		return null;
	}
	
	/**
	 * Returns all the rooms that satisfy the given conditions.
	 * 
	 * @param locations
	 * @param occupancy
	 * @param years
	 * @param genderNeutral
	 * @param sophomoreOnly
	 * @return
	 */
	public static RoomList getResults(
			Collection<Dorm> locations, 
			int occupancy, 
			int[] years,
			boolean genderNeutral,
			boolean sophomoreOnly)
	{
		// use INSTANCE.privateMethods()...
		return null;
	}
	
	/**
	 * Given a lottery number and an array of years to use,
	 * returns the average semester level for groups with this lottery number.
	 * 
	 * @param lotteryNumber the lottery number
	 * @param years all years to include in the results
	 * @return average semester level for groups with this lottery number
	 */
	public static int semesterLevel(int lotteryNumber, int[] years) {
		return 1;
	}
	
	/**
	 * Given a lottery number,
	 * returns the average semester level for groups with this lottery number,
	 * over all the data in the database.
	 * 
	 * @param lotteryNumber the lottery number
	 * @return average semester level for groups with this lottery number
	 */
	public static int semesterLevel(int lotteryNumber) {
		return 1;
	}
}
