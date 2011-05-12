

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Database {
    private static final Database INSTANCE = new Database();
    private static Connection connection;
    private static Statement statement;

    /**
     * A list of all CampusAreas known to the database.
     */
    private static ArrayList<CampusArea> _campusAreas;

    /**
     * all dorms known to the database, with the key being their name (as
     * referenced in the database)
     */
    private HashMap<String, Dorm> _dorms;

    private Database() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"
                    + Constants.DATABASE_NAME);
            statement = connection.createStatement();

            _dorms = new HashMap<String, Dorm>();
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: missing .jar file");
        } catch (SQLException e) {
            System.err.println("ERROR: unable to connect to database "
                    + Constants.DATABASE_NAME);
        }
    }

    /**
     * Given a string with a dorm name, returns the Dorm object associated with
     * it.
     * 
     * @param dormName
     * @return
     */
    protected static Dorm getDorm(String dormName) {
        return INSTANCE._dorms.get(dormName);
    }

    protected static int[] getYears() {
        try {
            ResultSet rs = statement.executeQuery("select * from "
                    + Constants.SEMESTER_TABLE + " limit 1;");
            ResultSetMetaData metaData = rs.getMetaData();
            int numberOfYears = metaData.getColumnCount() - 1;

            int[] years = new int[numberOfYears];

            for (int i = 2; i < numberOfYears + 2; i++)
                years[i - 2] = Integer.parseInt(metaData.getColumnName(i)
                        .replace("y", ""));

            rs.close();

            return years;
        } catch (SQLException e) {
            System.out.println("error retrieving years");
        }

        return null;
    }

    /**
     * Returns a Collection with all the CampusAreas (and Dorms) known in the
     * database.
     * 
     * @return
     */
    protected static Collection<CampusArea> getCampusAreas() {
        if (_campusAreas != null)
            return _campusAreas;

        _campusAreas = new ArrayList<CampusArea>();

        try {
            ResultSet areas = statement
                    .executeQuery("select distinct campusArea from "
                            + Constants.CAMPUS_AREA_TABLE + ";");

            while (areas.next()) {
                CampusArea area = new CampusArea(areas.getString("campusArea"));
                _campusAreas.add(area);
            }

            areas.close();

            ResultSet dorms = statement
                    .executeQuery("select distinct building, campusArea from "
                            + Constants.CAMPUS_AREA_TABLE + ";");

            while (dorms.next()) {
                Dorm dorm = new Dorm(dorms.getString("building"));

                for (CampusArea ca : _campusAreas) {
                    if (dorms.getString("campusArea").equals(ca.getName())) {
                        INSTANCE._dorms.put(dorm.getName(), dorm);
                        ca.add(dorm);
                        break;
                    }
                }
            }

            dorms.close();

            for (Dorm d : INSTANCE._dorms.values())
                d.setGenderNeutral(isGenderNeutral(d));
            for (Dorm d : INSTANCE._dorms.values())
                d.setSophomoreOnly(isSophomoreOnly(d));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ERROR: unable to retrieve campus areas");
        }

        return _campusAreas;
    }

    private static boolean isGenderNeutral(Dorm d) throws SQLException {
        ResultSet neutral = statement.executeQuery("select * from "
                + Constants.GENDER_TABLE + " where building='" + d.getName()
                + "';");
        boolean genderNeutral = neutral.next();
        neutral.close();
        return genderNeutral;
    }

    private static boolean isSophomoreOnly(Dorm d) throws SQLException {
        ResultSet sophomore = statement.executeQuery("select * from "
                + Constants.SOPHOMORE_TABLE + " where building='" + d.getName()
                + "';");
        boolean sophomoreOnly = sophomore.next();
        sophomore.close();
        return sophomoreOnly;
    }

    protected static int getMaxLotteryNumber() {
        try {
            ResultSet maxNum = statement
                    .executeQuery("select max(number) as maxNum from "
                            + Constants.SEMESTER_TABLE + ";");
            maxNum.next();

            return maxNum.getInt("maxNum");
        } catch (SQLException e) {
            return -1;
        }
    }

    /**
     * Returns all the rooms that satisfy the given conditions.
     * 
     * @param locations
     * @param occupancy
     * @param years
     * @param genderNeutral
     *            gender-neutral housing is REQUIRED for this subgroup
     * @param sophomoreEligible
     * @return
     * @throws SQLException
     */
    protected static Collection<Room> getResults(Collection<Dorm> locations,
            int occupancy, Integer[] years, boolean genderNeutral,
            boolean sophomoreEligible) {
        List<Room> rooms = new LinkedList<Room>();

        try {
            for (Dorm d : locations) {
                if ((sophomoreEligible || !d.isSophomoreOnly())
                        && (!genderNeutral || d.isGenderNeutral())) {
                    ResultSet subRooms = statement
                            .executeQuery("select * from "
                                    + Constants.ROOM_TABLE
                                    + " where occupancy=" + occupancy
                                    + " and building='" + d.getName() + "';");

                    while (subRooms.next()) {
                        Room room = Room.getRoom(INSTANCE._dorms.get(subRooms
                                .getString("building")), subRooms
                                .getString("roomNumber"));

                        // save coefficients
                        room.setCoefficients(subRooms.getDouble("b0"), subRooms
                                .getDouble("b1"));

                        // save occupancy
                        room.setOccupancy(occupancy);

                        // save apartement fee status
                        room
                                .setApartmentFee(subRooms
                                        .getInt("apartmentRate") == 1);

                        // save past lottery results (replacing the current
                        // ones, if necessary)
                        room.clearResults();

                        for (int i = 0; i < years.length; i++) {
                            if (subRooms.getInt("y" + years[i]) != 0) {
                                LotteryResult result = new LotteryResult(
                                        years[i], subRooms.getInt("y"
                                                + years[i]));
                                room.addResult(result);
                            }
                        }

                        rooms.add(room);
                    }

                    subRooms.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ERROR: unable to retrieve results");
        }

        return rooms;
    }

    protected static int optimismFromLotteryNumber(int lotteryNumber) {
        try {
            Integer[] years = State.getInstance().getYears();
            int semester = semesterFromLotteryNumber(lotteryNumber);

            int sum = 0;

            for (int i = 0; i < years.length; i++) {
                ResultSet totalNums = statement
                        .executeQuery("select count(number) as count from "
                                + Constants.SEMESTER_TABLE + " where y"
                                + years[i] + "=" + semester + ";");
                int total = totalNums.getInt("count");
                totalNums.close();

                ResultSet firstNumber = statement
                        .executeQuery("select number as firstNum from "
                                + Constants.SEMESTER_TABLE + " where y"
                                + years[i] + "=" + semester
                                + " order by number limit 1;");
                int firstNum = firstNumber.getInt("firstNum");
                firstNumber.close();

                if (lotteryNumber < firstNum + total / 3)
                    sum += Constants.OPTIMISM_HIGH;
                else if (lotteryNumber > firstNum + 2 * total / 3 + 1)
                    sum += Constants.OPTIMISM_LOW;
                else
                    sum += Constants.OPTIMISM_MEDIUM;
            }

            return Math.round(sum / years.length);
        } catch (SQLException e) {
            return -1;
        }
    }

    /**
     * Given a lottery number, returns the average semester level for groups
     * with this lottery number. Looks at settings in State to choose which
     * years to use.
     * 
     * @param lotteryNumber
     *            the lottery number
     * @return average semester level for groups with this lottery number
     * @throws SQLException
     */
    protected static int semesterFromLotteryNumber(int lotteryNumber) {
        try {
            Integer[] years = State.getInstance().getYears();

            ResultSet semesters = statement.executeQuery("select * from "
                    + Constants.SEMESTER_TABLE + " where number="
                    + lotteryNumber + ";");
            semesters.next();

            int sum = 0;
            int count = 0;
            for (int i = 0; i < years.length; i++) {
                sum += semesters.getInt("y" + years[i]);

                if (semesters.getInt(years[i] - 2004) != 0)
                    count++;
            }

            return Math.round(sum / count);
        } catch (SQLException e) {
            // TODO: better handling
            return -1;
        }
    }

    /**
     * Given a semester level, returns an approximate lottery number for groups
     * with this semester level. Looks at settings in State to choose which
     * years to use.
     * 
     * @param lotteryNumber
     *            the lottery number
     * @param optimism
     *            expected level of optimism
     * @return average semester level for groups with this lottery number
     * @throws SQLException
     */
    protected static int predictLotteryNumber(int semester, int optimism) {
        Integer[] years = State.getInstance().getYears();

        int count = 0;
        int sum = 0;

        int first, last;

        try {
            for (int i = 0; i < years.length; i++) {
                ResultSet totalNums = statement
                        .executeQuery("select count(number) as count from "
                                + Constants.SEMESTER_TABLE + " where y"
                                + years[i] + "=" + semester + ";");
                int total = totalNums.getInt("count");
                totalNums.close();

                ResultSet firstNumber = statement
                        .executeQuery("select number as firstNum from "
                                + Constants.SEMESTER_TABLE + " where y"
                                + years[i] + "=" + semester
                                + " order by number limit 1;");
                int firstNum = firstNumber.getInt("firstNum");
                firstNumber.close();

                if (optimism == Constants.OPTIMISM_HIGH) {
                    first = firstNum;
                    last = firstNum + total / 3;
                } else if (optimism == Constants.OPTIMISM_MEDIUM) {
                    first = firstNum + total / 3 + 1;
                    last = firstNum + 2 * total / 3;
                } else {
                    first = firstNum + 2 * total / 3 + 1;
                    last = firstNum + total - 1;
                }

                ResultSet numbers = statement.executeQuery("select * from "
                        + Constants.SEMESTER_TABLE + " where y" + years[i]
                        + "=" + semester + " and number between " + first
                        + " and " + last + ";");

                while (numbers.next()) {
                    sum += numbers.getInt("number");
                    count++;
                }

                numbers.close();
            }

            return sum / count;
        } catch (SQLException e) {
            return -1; // TODO: fix this too
        }
    }

    /**
     * Given a room, makes sure its coefficients and lottery results have been
     * set. If they have not, sets them using database values.
     * 
     * @param room
     */
    public static void updateRoomData(Room room) {
        try {
            if (room.getResults().isEmpty()) {
                int[] years = getYears();

                // if the results list is empty, we will assume that
                // the room has NEITHER results NOR coefficients
                // and populate it with both of them
                ResultSet result = statement.executeQuery("SELECT * FROM "
                        + Constants.ROOM_TABLE + " WHERE building='"
                        + room.getDorm().getName() + "' AND roomNumber='"
                        + room.getNumber() + "';");

                if (result.next()) {
                    // save coefficients
                    room.setCoefficients(result.getDouble("b0"), result
                            .getDouble("b1"));

                    for (int i = 0; i < years.length; i++) {
                        if (result.getInt("y" + years[i]) != 0) {
                            LotteryResult lotteryResult = new LotteryResult(
                                    years[i], result.getInt("y" + years[i]));
                            room.addResult(lotteryResult);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static void closeDatabase() {
        INSTANCE.close();
    }

    private void close() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("ERROR: unable to close database");
        }
    }

}
