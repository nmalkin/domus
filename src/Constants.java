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
	protected static final int CANVAS_HEIGHT = 650;
	protected static final Color CANVAS_COLOR = Color.WHITE;
	
	protected static final Integer HOUSE_LAYER    = 1;
	protected static final Integer SUBGROUP_LAYER = 2;
	protected static final Integer PERSON_LAYER   = 3;
	
	/** what portion of an object is overlapping for the two to be considered intersecting */
	protected static final double INTERSECTION_FRACTION = 0.5;
	
	protected static final int SIDEBAR_WIDTH = 120;
	protected static final Color SIDEBAR_COLOR = Color.GRAY;
	
	protected static final int NEW_MALE_X_POSITION = (0 + Constants.SIDEBAR_WIDTH) / 2 - Gender.MALE.getImageDimension().width / 2;
	protected static final int NEW_MALE_Y_POSITION = 120;
	
	protected static final int NEW_FEMALE_X_POSITION = (0 + Constants.SIDEBAR_WIDTH) / 2 - Gender.FEMALE.getImageDimension().width / 2;
	protected static final int NEW_FEMALE_Y_POSITION = 320;
	
	protected static final String TRASH_FILE = "img/trash.png";
	protected static final int TRASH_WIDTH = 77; // pixels
	protected static final int TRASH_HEIGHT = 100; // pixels
	protected static final int TRASH_X_POSITION = (0 + Constants.SIDEBAR_WIDTH) / 2 - TRASH_WIDTH / 2;
	protected static final int TRASH_Y_POSITION = 500;
	protected static final float TRASH_OVERLAY_ALPHA_FRACTION = 0.25f;
	protected static final int TRASH_OVERLAY_ALPHA = (int) (TRASH_OVERLAY_ALPHA_FRACTION * 255);
	
	// House
	protected static final int HOUSE_PADDING = 45; // pixels
	protected static final Color HOUSE_COLOR = Color.YELLOW;
	protected static final Color HOUSE_COLOR_TRANSPARENT = new Color(
			HOUSE_COLOR.getRed(), HOUSE_COLOR.getGreen(), HOUSE_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
	protected static final float SELECTED_HOUSE_BORDER_WIDTH = INSET + 0f;
	protected static final Color SELECTED_HOUSE_BORDER_COLOR = Color.CYAN;
	protected static final Color SELECTED_HOUSE_BORDER_COLOR_TRANSPARENT = new Color(
			SELECTED_HOUSE_BORDER_COLOR.getRed(), SELECTED_HOUSE_BORDER_COLOR.getGreen(), SELECTED_HOUSE_BORDER_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
	
	// SubGroup
	protected static final int SUBGROUP_PADDING = 25; // pixels
	protected static final Color SUBGROUP_COLOR = Color.PINK;
	protected static final Color SUBGROUP_COLOR_TRANSPARENT = new Color(
			SUBGROUP_COLOR.getRed(), SUBGROUP_COLOR.getGreen(), SUBGROUP_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
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
	protected static final String LEFT_ARROW = "img/left_arrow_smaller.png";
	protected static final String RIGHT_ARROW = "img/right_arrow_smaller.png";
	
	// Database
	/** database name **/
	public final static String DATABASE_NAME = "data/housingdata.db";
	
	/** table names in database */
	public final static String ROOM_TABLE = "rooms";
	public final static String GENDER_TABLE = "genderNeutral";
	public final static String SOPHOMORE_TABLE = "sophomoreOnly";
	public final static String SEMESTER_TABLE = "semester";
	public final static String CAMPUS_AREA_TABLE = "campusArea";
	
	public final static int FIRST_YEAR = 2006;
	public final static int LAST_YEAR = 2011;
	
	public final static int OPTIMISTIC = 0;
	public final static int AVERAGE = 1;
	public final static int PESSIMISTIC = 2;
}
