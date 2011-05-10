import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Room implements Comparable<Room> {
	/* factory methods for room */
	private static Map<String, Room> _roomMap = new HashMap<String, Room>();
	
	public static Room getRoom(Dorm dorm, String roomNumber) {
		String roomName = dorm.getName() + roomNumber;
		Room room = null;
		if ((room = _roomMap.get(roomName)) == null) {
			room = new Room(dorm, roomNumber);
			_roomMap.put(roomName, room);
		}
		return room;
	}
	
	/** What dorm is this room in? */
	private Dorm _dorm;
	
	/** The name/number of this room. */
	private String _number;
	
	/** The occupancy of this room. */
	private int _occupancy;
	
	/** This room's history in the lottery. */
	private List<LotteryResult> _results;
	
	/** 
	 * The RoomLists that this room is a part of.
	 * Used for displaying list labels on results page.
	 */
	private List<RoomList> _roomLists;
	
	/**
	 * The ResultsListItems that this room is a part of.
	 * Used for updating list labels on results tab.
	 */
	private List<ResultsListItem> _listItems;
	
	/** regressions coefficients
	 * 
	 * we compute the probability of obtaining this room 
	 * by using (externally calculated) regressions coefficients
	 * and plugging values into the logistic function
	 */
	private double _b0_coefficient, _b1_coefficient;
	
	
	private Room (Dorm dorm, String number) {
		_dorm = dorm;
		_number = number;
		_results = new LinkedList<LotteryResult>();
		_roomLists = new LinkedList<RoomList>();
		_listItems = new LinkedList<ResultsListItem>();
		
		_b0_coefficient = 0;
		_b1_coefficient = 0;
	}
	
	/** Returns the Dorm to which this Room belongs */
	public Dorm getDorm() {
		return _dorm;
	}
	
	/** Returns the number/name of this room */
	public String getNumber() {
		return _number;
	}
	
	/** Returns the past lottery results for this Room */
	public List<LotteryResult> getResults() {
		return new LinkedList<LotteryResult>(_results);
	}
	
	/** 
	 * Returns whether or not this room is genderNeutral.
	 * Based on Dorm's genderNeutrality and Room's occupancy.
	 */
	public boolean isGenderNeutral() {
		if (_occupancy > 1) 
			return _dorm.isGenderNeutral();
		return false;
	}
	
	/** Returns the average lottery result for this Room. */
	@Deprecated
	public int getAverageResult() {
		int averageResult = 0;
		
		for(int i = 0; i < _results.size(); i++) averageResult += _results.get(i).getLotteryNumber();
		
		averageResult /= _results.size();
		
		return averageResult;
	}
	
	/** 
	 * Returns the probability of receiving this room based on group's lottery number.
	 * 
	 * To compute the probability using the "regression technique," we use the coefficients from the logistic regression
	 * (set using setCoefficients) and plug the lottery number into the logistic function:
	 * 1/(1+e^-(b0 + b1*x))
	 * where x is the lottery number.
	 * 
	 * To compute the probability using the "simple technique," we compute the theoretical success rate in previous years,
	 * using this number.
	 * i.e., if you had this number during each of the previous years, would you have gotten this room?
	 * If this technique is used, and there is no data for the currently selected years, @return Constants.PROBABILITY_NO_DATA
	 */
	public double getProbability() {
		if(State.getInstance().useRegressionProbability()) {
			/*
			 * The regression probability technique uses pre-calculated beta coefficients,
			 * and the logistic function.
			 */
			return 1.0 / (1.0 + Math.exp(-1 * (_b0_coefficient + _b1_coefficient * State.getInstance().getGroup().getLotteryNumber())));
		} else {
			/* 
			 * To compute the probability using the "simple technique," we compute the theoretical success rate in previous years,
			 * using this number.
			 * i.e., if you had this number during each of the previous years, would you have gotten this room?
			 */
			Integer[] years = State.getInstance().getYears();
			int lotteryNumber = State.getInstance().getGroup().getLotteryNumber();
			int successCount = 0;
			
			if(_results.size() == 0) { // there are no results for the selected years. probability concept isn't meaningful
				return Constants.PROBABILITY_NO_DATA;
			} else {
				for(LotteryResult result : _results) {
					for(int year : years) {
						if(result.getYear() == year) {
							if(lotteryNumber <= result.getLotteryNumber()){
								successCount++;
							}
							
							break;
						}
					}
				}
			
				return successCount / (_results.size() + 0.0);
			}
			
		}
	}
	
	public void setCoefficients(double b0, double b1) {
		_b0_coefficient = b0;
		_b1_coefficient = b1;
	}

	public String toString() {
		String resultString = "[ ";
		
		for(LotteryResult r: _results) resultString += r.getLotteryNumber() + " ";
		resultString += "]";
		
		return _dorm.getName() + " " + _number + "; " + resultString + ";" + getProbability() + ";";
	}
	
	public void addResult(LotteryResult result) {
		_results.add(result);
	}
	
	public void clearResults() {
		_results.clear();
	}
	
	public Collection<RoomList> getRoomLists() {
		return _roomLists;
	}
	
	public void addToRoomList(RoomList list) {
		if (!_roomLists.contains(list))
			_roomLists.add(list);
	}
	
	public void removeFromRoomList(RoomList list) {
		if (_roomLists.contains(list))
			_roomLists.remove(list);
	}
	
	public Collection<ResultsListItem> getListItems() {
		return _listItems;
	}
	
	public void addToListItem(ResultsListItem item) {
		if (!_listItems.contains(item))
			_listItems.add(item);
	}
	
	public void removeFromListItem(ResultsListItem item) {
		if (_listItems.contains(item))
			_listItems.remove(item);
	}

	/**
	 * Compares two rooms based on their probability.
	 * If two rooms have the same probability, they are compared lexicographically.
	 * 
	 * @return a number less than 0 if this room has a lower probability,
	 * or if the two probabilities are equal but this building+room comes first lexicographically
	 */
	@Override
	public int compareTo(Room o) {
		String myName = getDorm().getName() + " " + getNumber();
		String theirName = o.getDorm().getName() + " " + o.getNumber();
		return myName.compareTo(theirName);
	}
}
