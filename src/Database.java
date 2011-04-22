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
		//TODO
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
		//TODO
		// use INSTANCE.privateMethods()...
		return null;
	}
	
	/**
	 * Given a lottery number,
	 * returns the average semester level for groups with this lottery number.
	 * Looks at settings in State to choose which years to use. (TODO)
	 * 
	 * @param lotteryNumber the lottery number
	 * @return average semester level for groups with this lottery number
	 */
	public static int semesterFromLotteryNumber(int lotteryNumber) {
		//TODO
		return 7 - lotteryNumber / 200; // not a real formula
	}
	
	/**
	 * Given a semester level,
	 * returns an approximate lottery number for groups with this semester level.
	 * Looks at settings in State to choose which years to use. (TODO)
	 * TODO TOO: interface will probably change to accommodate happiness level as well.
	 * 
	 * @param lotteryNumber the lottery number
	 * @return average semester level for groups with this lottery number
	 */
	public static int lotteryNumberFromSemester(int semester) {
		//TODO
		return 800 - (semester - 3) * 200; // not a real formula
	}
	
}
