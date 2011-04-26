import java.util.LinkedList;
import java.util.List;

public class Room {
	/** What dorm is this room in? */
	private Dorm _dorm;
	
	/** The name/number of this room. */
	private String _number;
	
	/** This room's history in the lottery. */
	private List<LotteryResult> _results;
	
	/** The average lottery number, calculated depending on defined years of interest. */
	private int _averageResult;
	
	public Room(Dorm dorm, String number, List<LotteryResult> results, int averageResult) {
		_dorm = dorm;
		_number = number;
		_results = results;
		_averageResult = averageResult;
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
	
	/** Returns the average lottery result for this Room */
	public int getAverageResult() {
		return _averageResult;
	}
	
}
