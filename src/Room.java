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
	
	public Room (Dorm dorm, String number) {
		_dorm = dorm;
		_number = number;
		_results = new LinkedList<LotteryResult>();
	}
	
	public String toString() {
		String resultString = "[ ";
		
		for(LotteryResult r: _results) resultString += r.getLotteryNumber() + " ";
		resultString += "]";
		
		return _dorm.getName() + " " + _number + "; " + resultString + "; avg " + _averageResult;
	}
	
	public void addResult(LotteryResult result) {
		_results.add(result);
		updateAverage();
	}
	
	public void updateAverage() {
		_averageResult = 0;
		
		for(int i = 0; i < _results.size(); i++) _averageResult += _results.get(i).getLotteryNumber();
		
		_averageResult /= _results.size();
	}
}
