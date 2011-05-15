package domus.data;


public class LotteryResult {
    /** the year of this result */
    private int _year;

    /** the lottery number for this year */
    private int _lotteryNumber;

    public LotteryResult(int year, int lotteryNumber) {
        _year = year;
        _lotteryNumber = lotteryNumber;
    }

    public int getYear() {
        return _year;
    }

    public int getLotteryNumber() {
        return _lotteryNumber;
    }
}
