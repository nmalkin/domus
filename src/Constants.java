

import java.awt.Color;
import java.awt.Font;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class Constants {

	protected static final int VERSION = 1;
	protected static final int FILE_FORMAT_VERSION = 1;
	
	protected static final String PATH_PREFIX = "";
	
	protected static final int GROUP_SIZE_LIMIT = 12; // people
	protected static final int SUBGROUP_SIZE_LIMIT = 8; // people
	
	protected static final Font DOMUS_FONT = FontLoader.load(PATH_PREFIX + "resources/GOTHIC.TTF");
	
	// General Graphics
	protected static final int INSET = 5; // pixels
	protected static final Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);
	protected static final String DOMUS_FILE = PATH_PREFIX + "img/domus_attempt_3.png";

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
	protected static final Color SIDEBAR_COLOR = new Color(181,181,181);
	
	protected static final Color NEW_PERSON_TEXT_COLOR = Color.BLACK;
	protected static final String NEW_PERSON_DEFAULT_NAME = "A Person";
	
//	protected static final String TRASH_FILE = "img/trash.png";
	protected static final String TRASH_CLOSED_FILE = PATH_PREFIX + "img/trashcan_closed.png";
	protected static final String TRASH_OPEN_FILE = PATH_PREFIX + "img/trashcan_open.png";
	protected static final int TRASH_WIDTH = 77; // pixels
	protected static final int TRASH_HEIGHT = 140; // pixels
	protected static final float TRASH_OVERLAY_ALPHA_FRACTION = 0.25f;
	protected static final int TRASH_OVERLAY_ALPHA = (int) (TRASH_OVERLAY_ALPHA_FRACTION * 255);
	
	// House
	protected static final Color HOUSE_COLOR = new Color(171, 171, 171);
	protected static final int HOUSE_PADDING = 25; // pixels
	protected static final Color HOUSE_COLOR_TRANSPARENT = new Color(
			HOUSE_COLOR.getRed(), HOUSE_COLOR.getGreen(), HOUSE_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
	protected static final int HOUSE_ROOF_HEIGHT = 40; // pixels
	
	protected static final Color SELECTED_HOUSE_BORDER_COLOR = new Color(49, 49, 49);
	protected static final float SELECTED_HOUSE_BORDER_WIDTH = INSET + 0f;
	protected static final Color SELECTED_HOUSE_BORDER_COLOR_TRANSPARENT = new Color(
			SELECTED_HOUSE_BORDER_COLOR.getRed(), SELECTED_HOUSE_BORDER_COLOR.getGreen(), SELECTED_HOUSE_BORDER_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
	
	// SubGroup
	protected static final Color SUBGROUP_COLOR = new Color(251,232,195);
	protected static final int SUBGROUP_PADDING = 25; // pixels
	protected static final Color SUBGROUP_COLOR_TRANSPARENT = new Color(
			SUBGROUP_COLOR.getRed(), SUBGROUP_COLOR.getGreen(), SUBGROUP_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
	// Person images & dimensions
	protected static final String MAN_FILE   = PATH_PREFIX + "img/man.png";
	protected static final int    MAN_WIDTH  = 37; // pixels
	protected static final int    MAN_HEIGHT = 100; // pixels

	protected static final String WOMAN_FILE   = PATH_PREFIX + "img/woman.png";
	protected static final int    WOMAN_WIDTH  = 46; // pixels
	protected static final int    WOMAN_HEIGHT = 100; // pixels
	
	// Lottery panel
	protected static final int LOTTERY_PANEL_WIDTH = 130;
	protected static final int LOTTERY_PANEL_HEIGHT = 500;

	/** the value that the slider starts with */
	protected static final int DEFAULT_LOTTERY_NUMBER = 1;
	
	// Preference panel
	protected final static int PREFERENCE_PANEL_WIDTH = 250;
	/* very bad. fix this (TODO) */
	protected final static int PREFERENCE_PANEL_HEIGHT = 901;
	
	// Results and lists icons
	protected static final String CLOSED_FILE 	= PATH_PREFIX + "img/closed_results_tab_new.png";
	protected static final String OPEN_FILE 	= PATH_PREFIX + "img/open_results_tab_new.png";
	protected static final String ADD_FILE 		= PATH_PREFIX + "img/add_to_list_center_1.png";
	protected static final String REMOVE_FILE 	= PATH_PREFIX + "img/remove_from_list_black_center_1.png";
	protected static final String LEFT_ARROW 	= PATH_PREFIX + "img/left_arrow_smaller.png";
	protected static final String RIGHT_ARROW 	= PATH_PREFIX + "img/right_arrow_smaller.png";
	protected static final int OPEN_ICON_WIDTH = 15;
	protected static final int REMOVE_ICON_HEIGHT = 16;
	
	// Results
	protected static final int RESULTS_LIST_WIDTH = 350;
	protected static final int RESULTS_LIST_HEIGHT = 800;
	protected static final int RESULTS_LIST_TAB_WIDTH = 350;
	protected static final int RESULTS_LIST_TAB_HEIGHT = 25;
	protected static final int RESULTS_LIST_ITEM_WIDTH = 350;
	protected static final int RESULTS_LIST_ITEM_HEIGHT = 15;
	protected static final int RESULTS_HEADER_HEIGHT = 30;
	protected static final int RESULTS_PANEL_WIDTH = 850;
	protected static final int RESULTS_PANEL_HEIGHT = RESULTS_LIST_HEIGHT + RESULTS_HEADER_HEIGHT;
	protected static final int RESULTS_LISTS_DISPLAYED = 2;
	protected static final int RESULTS_PANEL_HORIZONTAL_GAP = 5;
	
	// probability
	protected static final double PROBABILITY_NO_DATA = -1; // this must be < 0 or > 1 to avoid accidental conflicts! 
	
	// Probability display
	protected static final int PROBABILITY_DISPLAY_WIDTH  = 40;
	protected static final int PROBABILITY_DISPLAY_HEIGHT = 10;
	protected static final int PROBABILITY_DISPLAY_RIGHT_SPACING = 15;
	
	/** How many different categories are we dividing probabilities into? */
	protected static final int PROBABILIY_DISPLAY_CATEGORIES = 5; // very unlikely, unlikely, average, likely, very likely
	/** The colors for each probability category, from least probability to highest probability.
	 * (PROBABILITY_DISPLAY_COLORS.length must equal PROBABILIY_DISPLAY_CATEGORIES) */
	protected static final Color[] PROBABILITY_DISPLAY_COLORS = { 
		new Color(250, 100, 80), 	// red
		new Color(250, 200, 150), 	// orange
		new Color(250, 250, 150), 	// yellow
		new Color(150, 250, 150), 	// light green
		new Color(100, 150, 100)	// dark green
	};
	protected static final Color PROBABILITY_DISPLAY_BACKGROUND_COLOR = Color.WHITE;
	
	// Lists
	protected static final int LISTS_PANEL_WIDTH = 800;
	protected static final int LISTS_WIDTH = 350;
	protected static final int LISTS_HEIGHT = 800;
	protected static final int LISTS_ITEM_WIDTH = 200;
	protected static final int LISTS_ITEM_HEIGHT = 15;
	protected static final int LISTS_DISPLAYED = 2;
	protected static final int LISTS_HORIZONTAL_GAP = 5;
	protected static final int LISTS_INSTRUCTIONS_WIDTH = 500;
	protected static final int LISTS_INSTRUCTIONS_HEIGHT = 300;
	
	// Instructions
	protected static final String LISTS_INSTRUCTIONS = "<html><center>To create a list, click on the plus sign " +
			"next to a result on the Results tab.<p><p>" + 
			"You may make as many lists as you like and " +
			"add individual rooms or entire dorms to them.<p><p>" +
			"You can also remove items or an entire list if you want.<p><p>" +
			"Lastly, you can reorder the elements in these lists " +
			"by simply dragging them to a new location.</center></html>";
	
	protected static final String NO_PEOPLE_MESSAGE = "<html><center>You currently have no people in your group!<p><p> " +
			"Click the Preferences tab and drag people onto the canvas to view results.</center></html>";
	
	// Database
	protected static final String DATABASE_NAME = PATH_PREFIX + "data/housingdata.db";
	protected static final String ROOM_TABLE = "rooms_with_regressions";
	protected static final String GENDER_TABLE = "genderNeutral";
	protected static final String SOPHOMORE_TABLE = "sophomoreOnly";
	protected static final String SEMESTER_TABLE = "semester";
	protected static final String CAMPUS_AREA_TABLE = "campusArea";

	// LotteryNumberPanel (optimism)
	protected static final int OPTIMISM_HIGH = 0;
	protected static final int OPTIMISM_MEDIUM = 1;
	protected static final int OPTIMISM_LOW = 2;
	
	protected static final String HAPPY_FILE 		= PATH_PREFIX + "img/Grin.png";
	protected static final String OKAY_FILE 		= PATH_PREFIX + "img/Undecided.png";
	protected static final String SAD_FILE 			= PATH_PREFIX + "img/Crying.png";
	protected static final String SAD_FILE_DIM 		= PATH_PREFIX + "img/Crying2.png";
	protected static final String OKAY_FILE_DIM 	= PATH_PREFIX + "img/Undecided2.png";
	protected static final String HAPPY_FILE_DIM 	= PATH_PREFIX + "img/Grin2.png";

	
	// XML i/o
	protected static final String XML_TRANFORM_FILE = "transform.xsl";
	
	
	// HelpWindow
	protected static final String HELP_SCREEN_PREFERENCE_FILE = PATH_PREFIX + "help/preferences_screen_annotated.png";
	protected static final String HELP_SCREEN_RESULTS_FILE = PATH_PREFIX + "help/results_screen.png";
	protected static final String HELP_SCREEN_CART_FILE = PATH_PREFIX + "help/cart_screen.png";
	
	/** the height to which to scale the help screen images */
	protected static final int HELP_SCREEN_HEIGHT = 700;
	
}
