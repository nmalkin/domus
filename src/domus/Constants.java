package domus;


import java.awt.Color;
import java.awt.Font;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import domus.gui.util.FontLoader;


public class Constants {
	public static final String LAST_UPDATED = "May 2011";
	public static final int VERSION = 1;
	public static final int FILE_FORMAT_VERSION = 1;
	
	public static final String PATH_PREFIX = "";
	
	public static final int GROUP_SIZE_LIMIT = 12; // people
	public static final int SUBGROUP_SIZE_LIMIT = 8; // people
	
	public static final Font DOMUS_FONT = FontLoader.load(PATH_PREFIX + "resources/GOTHIC.TTF");
	
	// General Graphics
	public static final int INSET = 5; // pixels
	public static final Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);
	public static final String DOMUS_FILE = PATH_PREFIX + "img/domus_attempt_3.png";

	// Canvas
	public static final int CANVAS_WIDTH  = 600;
	public static final int CANVAS_HEIGHT = 650;
	public static final Color CANVAS_COLOR = Color.WHITE;
	
	public static final Integer HOUSE_LAYER    = 1;
	public static final Integer SUBGROUP_LAYER = 2;
	public static final Integer PERSON_LAYER   = 3;
	
	/** what portion of an object is overlapping for the two to be considered intersecting */
	public static final double INTERSECTION_FRACTION = 0.5;
	
	public static final int SIDEBAR_WIDTH = 120;
	public static final Color SIDEBAR_COLOR = new Color(181,181,181);
	
	public static final Color NEW_PERSON_TEXT_COLOR = Color.BLACK;
	public static final String NEW_PERSON_DEFAULT_NAME = "A Person";
	
//	public static final String TRASH_FILE = "img/trash.png";
	public static final String TRASH_CLOSED_FILE = PATH_PREFIX + "img/trashcan_closed.png";
	public static final String TRASH_OPEN_FILE = PATH_PREFIX + "img/trashcan_open.png";
	public static final int TRASH_WIDTH = 77; // pixels
	public static final int TRASH_HEIGHT = 140; // pixels
	public static final float TRASH_OVERLAY_ALPHA_FRACTION = 0.25f;
	public static final int TRASH_OVERLAY_ALPHA = (int) (TRASH_OVERLAY_ALPHA_FRACTION * 255);
	
	// House
	public static final Color HOUSE_COLOR = new Color(171, 171, 171);
	public static final int HOUSE_PADDING = 25; // pixels
	public static final Color HOUSE_COLOR_TRANSPARENT = new Color(
			HOUSE_COLOR.getRed(), HOUSE_COLOR.getGreen(), HOUSE_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
	public static final int HOUSE_ROOF_HEIGHT = 40; // pixels
	
	public static final Color SELECTED_HOUSE_BORDER_COLOR = new Color(49, 49, 49);
	public static final float SELECTED_HOUSE_BORDER_WIDTH = INSET + 0f;
	public static final Color SELECTED_HOUSE_BORDER_COLOR_TRANSPARENT = new Color(
			SELECTED_HOUSE_BORDER_COLOR.getRed(), SELECTED_HOUSE_BORDER_COLOR.getGreen(), SELECTED_HOUSE_BORDER_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
	
	// SubGroup
	public static final Color SUBGROUP_COLOR = new Color(251,232,195);
	public static final int SUBGROUP_PADDING = 25; // pixels
	public static final Color SUBGROUP_COLOR_TRANSPARENT = new Color(
			SUBGROUP_COLOR.getRed(), SUBGROUP_COLOR.getGreen(), SUBGROUP_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
	// Person images & dimensions
	public static final String MAN_FILE   = PATH_PREFIX + "img/man.png";
	public static final int    MAN_WIDTH  = 37; // pixels
	public static final int    MAN_HEIGHT = 100; // pixels

	public static final String WOMAN_FILE   = PATH_PREFIX + "img/woman.png";
	public static final int    WOMAN_WIDTH  = 46; // pixels
	public static final int    WOMAN_HEIGHT = 100; // pixels
	
	// Lottery panel
	public static final int LOTTERY_PANEL_WIDTH = 130;
	public static final int LOTTERY_PANEL_HEIGHT = 500;

	/** the value that the slider starts with */
	public static final int DEFAULT_LOTTERY_NUMBER = 1;
	
	// Preference panel
	public final static int PREFERENCE_PANEL_WIDTH = 250;
	/* very bad. fix this (TODO) */
	public final static int PREFERENCE_PANEL_HEIGHT = 901;
	
	// Results and lists icons
	public static final String CLOSED_FILE 	= PATH_PREFIX + "img/closed_results_tab_new.png";
	public static final String OPEN_FILE 	= PATH_PREFIX + "img/open_results_tab_new.png";
	public static final String ADD_FILE 		= PATH_PREFIX + "img/add_to_list_center_1.png";
	public static final String REMOVE_FILE 	= PATH_PREFIX + "img/remove_from_list_black_center_1.png";
	public static final String LEFT_ARROW 	= PATH_PREFIX + "img/left_arrow_smaller.png";
	public static final String RIGHT_ARROW 	= PATH_PREFIX + "img/right_arrow_smaller.png";
	public static final String GENDER_EMBLEM = PATH_PREFIX + "img/genderNeutralEmblem.png";
	public static final String APARTMENT_EMBLEM = PATH_PREFIX + "img/apartmentRateEmblem.png";
	public static final String SOPHOMORE_EMBLEM = PATH_PREFIX + "img/sophomoreOnlyEmblem.png";
	public static final int OPEN_ICON_WIDTH = 15;
	public static final int REMOVE_ICON_HEIGHT = 16;
	
	// Results
	public static final int RESULTS_LIST_WIDTH = 350;
	public static final int RESULTS_LIST_HEIGHT = 800;
	public static final int RESULTS_LIST_TAB_WIDTH = 350;
	public static final int RESULTS_LIST_TAB_HEIGHT = 25;
	public static final int RESULTS_LIST_ITEM_WIDTH = 350;
	public static final int RESULTS_LIST_ITEM_HEIGHT = 15;
	public static final int RESULTS_HEADER_HEIGHT = 30;
	public static final int RESULTS_PANEL_WIDTH = 850;
	public static final int RESULTS_PANEL_HEIGHT = RESULTS_LIST_HEIGHT + RESULTS_HEADER_HEIGHT;
	public static final int RESULTS_LISTS_DISPLAYED = 2;
	public static final int RESULTS_PANEL_HORIZONTAL_GAP = 5;
	
	// probability
	public static final double PROBABILITY_NO_DATA = -1; // this must be < 0 or > 1 to avoid accidental conflicts! 
	
	// Probability display
	public static final int PROBABILITY_DISPLAY_WIDTH  = 40;
	public static final int PROBABILITY_DISPLAY_HEIGHT = 10;
	public static final int PROBABILITY_DISPLAY_RIGHT_SPACING = 15;
	
	/** How many different categories are we dividing probabilities into? */
	public static final int PROBABILIY_DISPLAY_CATEGORIES = 5; // very unlikely, unlikely, average, likely, very likely
	/** The colors for each probability category, from least probability to highest probability.
	 * (PROBABILITY_DISPLAY_COLORS.length must equal PROBABILIY_DISPLAY_CATEGORIES) */
	public static final Color[] PROBABILITY_DISPLAY_COLORS = { 
		new Color(250, 100, 80), 	// red
		new Color(250, 200, 150), 	// orange
		new Color(250, 250, 150), 	// yellow
		new Color(150, 250, 150), 	// light green
		new Color(100, 150, 100)	// dark green
	};
	public static final Color PROBABILITY_DISPLAY_BACKGROUND_COLOR = Color.WHITE;
	
	// Lists
	public static final int LISTS_PANEL_WIDTH = 800;
	public static final int LISTS_WIDTH = 350;
	public static final int LISTS_HEIGHT = 800;
	public static final int LISTS_ITEM_WIDTH = 200;
	public static final int LISTS_ITEM_HEIGHT = 15;
	public static final int LISTS_DISPLAYED = 2;
	public static final int LISTS_HORIZONTAL_GAP = 5;
	public static final int LISTS_INSTRUCTIONS_WIDTH = 500;
	public static final int LISTS_INSTRUCTIONS_HEIGHT = 300;
	
	// Instructions
	public static final String LISTS_INSTRUCTIONS = "<html><center>To create a list, click on the plus sign " +
			"next to a result on the Results tab.<p><p>" + 
			"You may make as many lists as you like and " +
			"add individual rooms or entire dorms to them.<p><p>" +
			"You can also remove items or an entire list if you want.<p><p>" +
			"Lastly, you can reorder the elements in these lists " +
			"by simply dragging them to a new location.<p><p>" +
			"Hold control to select items to drag.</center></html>";
	
	public static final String NO_PEOPLE_MESSAGE = "<html><center>You currently have no people in your group!<p><p> " +
			"Click the Preferences tab and drag people onto the canvas to view results.</center></html>";
	
	// Database
	public static final String DATABASE_NAME = PATH_PREFIX + "data/housingdata.db";
	public static final String ROOM_TABLE = "rooms_with_regressions";
	public static final String GENDER_TABLE = "genderNeutral";
	public static final String SOPHOMORE_TABLE = "sophomoreOnly";
	public static final String SEMESTER_TABLE = "semester";
	public static final String CAMPUS_AREA_TABLE = "campusArea";

	// LotteryNumberPanel (optimism)
	public static final int OPTIMISM_HIGH = 0;
	public static final int OPTIMISM_MEDIUM = 1;
	public static final int OPTIMISM_LOW = 2;
	
	public static final String HAPPY_FILE 		= PATH_PREFIX + "img/Grin.png";
	public static final String OKAY_FILE 		= PATH_PREFIX + "img/Undecided.png";
	public static final String SAD_FILE 			= PATH_PREFIX + "img/Crying.png";
	public static final String SAD_FILE_DIM 		= PATH_PREFIX + "img/Crying2.png";
	public static final String OKAY_FILE_DIM 	= PATH_PREFIX + "img/Undecided2.png";
	public static final String HAPPY_FILE_DIM 	= PATH_PREFIX + "img/Grin2.png";

	
	// XML i/o
	public static final String XML_TRANFORM_FILE = "transform.xsl";
	
	
	// HelpWindow
	public static final String HELP_SCREEN_PREFERENCE_FILE = PATH_PREFIX + "help/preferences_screen_annotated.png";
	public static final String HELP_SCREEN_RESULTS_FILE = PATH_PREFIX + "help/results_screen.png";
	public static final String HELP_SCREEN_CART_FILE = PATH_PREFIX + "help/cart_screen.png";
	
	/** the height to which to scale the help screen images */
	public static final int HELP_SCREEN_HEIGHT = 700;
	
}
