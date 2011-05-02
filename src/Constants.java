import java.awt.Color;


public class Constants {
	// lottery numbers
	/** the maximum lottery number one is allowed to enter */
	protected static final int MAX_LOTTERY_NUMBER = 789;
	
	/** the value that the slider starts with */
	protected static final int DEFAULT_LOTTERY_NUMBER = 1;
	
	// general graphics
	protected static final int INSET = 5; // pixels
	
	// Canvas
	protected static final int CANVAS_WIDTH  = 600;
	protected static final int CANVAS_HEIGHT = 400;
	protected static final java.awt.Color CANVAS_COLOR = Color.WHITE;
	
	protected static final Integer HOUSE_LAYER    = 1;
	protected static final Integer SUBGROUP_LAYER = 2;
	protected static final Integer PERSON_LAYER   = 3;
	
	/** what portion of an object is overlapping for the two to be considered intersecting */
	protected static final double INTERSECTION_FRACTION = 0.5;
	
	protected static final int SEPARATOR_X_POSITION = 100;
	
	protected static final int NEW_MALE_X_POSITION = (0 + Constants.SEPARATOR_X_POSITION) / 2 - Gender.MALE.getImageDimension().width / 2;
	protected static final int NEW_MALE_Y_POSITION = 120;
	
	protected static final int NEW_FEMALE_X_POSITION = (0 + Constants.SEPARATOR_X_POSITION) / 2 - Gender.FEMALE.getImageDimension().width / 2;
	protected static final int NEW_FEMALE_Y_POSITION = 320;
	
	// House
	protected static final java.awt.Color HOUSE_COLOR = Color.BLUE;
	protected static final int HOUSE_PADDING = 45; // pixels
	
	protected static final java.awt.Color SELECTED_HOUSE_BORDER_COLOR = Color.PINK;
	protected static final float SELECTED_HOUSE_BORDER_WIDTH = INSET + 0f;
	
	// SubGroup
	protected static final java.awt.Color SUBGROUP_COLOR = Color.GREEN;
	protected static final int SUBGROUP_PADDING = 25; // pixels
	
	// Person images & dimensions
	protected static final String MAN_FILE   = "img/man.png";
	protected static final int    MAN_WIDTH  = 37; // pixels
	protected static final int    MAN_HEIGHT = 100; // pixels

	protected static final String WOMAN_FILE   = "img/woman.png";
	protected static final int    WOMAN_WIDTH  = 46; // pixels
	protected static final int    WOMAN_HEIGHT = 100; // pixels
	
	// Results and lists icons
	protected static final String CLOSED_FILE = "img/closed_results_tab_new.png";
	protected static final String OPEN_FILE = "img/open_results_tab_new.png";
	protected static final String ADD_FILE = "img/add_to_list_new.png";
	protected static final String REMOVE_FILE = "img/remove_from_list_black.png";
	
	// Database
	/** database name **/
	public final static String DATABASE_NAME = "data/housingdata.db";
	
	/** table names in database */
	public final static String ROOM_TABLE = "rooms";
	public final static String GENDER_TABLE = "genderNeutral";
	public final static String SOPHOMORE_TABLE = "sophomoreOnly";
	public final static String SEMESTER_TABLE = "semester";
	public final static String CAMPUS_AREA_TABLE = "campusArea";
}
