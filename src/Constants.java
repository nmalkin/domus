import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;


public class Constants {
	protected static final int VERSION = 1;
	protected static final int FILE_FORMAT_VERSION = 1;
	
	protected static final int GROUP_SIZE_LIMIT = 12; // people
	protected static final int SUBGROUP_SIZE_LIMIT = 8; // people
	
	// General Graphics
	protected static final int INSET = 5; // pixels
	protected static final Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);
	protected static final String DOMUS_FILE = "img/domus.png";

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
	
	protected static final Color NEW_PERSON_TEXT_COLOR = Color.LIGHT_GRAY;
	
	protected static final String TRASH_FILE = "img/trash.png";
	protected static final int TRASH_WIDTH = 77; // pixels
	protected static final int TRASH_HEIGHT = 100; // pixels
	protected static final float TRASH_OVERLAY_ALPHA_FRACTION = 0.25f;
	protected static final int TRASH_OVERLAY_ALPHA = (int) (TRASH_OVERLAY_ALPHA_FRACTION * 255);
	
	// House
	protected static final Color HOUSE_COLOR = new Color(171, 171, 171);
	protected static final int HOUSE_PADDING = 45; // pixels
	protected static final Color HOUSE_COLOR_TRANSPARENT = new Color(
			HOUSE_COLOR.getRed(), HOUSE_COLOR.getGreen(), HOUSE_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
	protected static final Color SELECTED_HOUSE_BORDER_COLOR = new Color(49, 49, 49);
	protected static final float SELECTED_HOUSE_BORDER_WIDTH = INSET + 0f;
	protected static final Color SELECTED_HOUSE_BORDER_COLOR_TRANSPARENT = new Color(
			SELECTED_HOUSE_BORDER_COLOR.getRed(), SELECTED_HOUSE_BORDER_COLOR.getGreen(), SELECTED_HOUSE_BORDER_COLOR.getBlue(), 
			TRASH_OVERLAY_ALPHA);
	
	
	// SubGroup
	protected static final Color SUBGROUP_COLOR = new Color(243,158,33);
	protected static final int SUBGROUP_PADDING = 25; // pixels
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
	
	// Lottery panel
	protected static final int LOTTERY_PANEL_WIDTH = 200;
	protected static final int LOTTERY_PANEL_HEIGHT = 500;

	/** the value that the slider starts with */
	protected static final int DEFAULT_LOTTERY_NUMBER = 1;
	
	// Results and lists icons
	protected static final String CLOSED_FILE = "img/closed_results_tab_new.png";
	protected static final String OPEN_FILE = "img/open_results_tab_new.png";
	protected static final String ADD_FILE = "img/add_to_list_new.png";
	protected static final String REMOVE_FILE = "img/remove_from_list_black.png";
	protected static final String LEFT_ARROW = "img/left_arrow_smaller.png";
	protected static final String RIGHT_ARROW = "img/right_arrow_smaller.png";
	protected static final int OPEN_ICON_WIDTH = 15;
	
	// Results
	protected static final int RESULTS_LIST_WIDTH = 350;
	protected static final int RESULTS_LIST_HEIGHT = 800;
	protected static final int RESULTS_LIST_TAB_WIDTH = 150;
	protected static final int RESULTS_LIST_TAB_HEIGHT = 25;
	protected static final int RESULTS_LIST_ITEM_WIDTH = 150;
	protected static final int RESULTS_LIST_ITEM_HEIGHT = 15;
	protected static final int RESULTS_HEADER_HEIGHT = 30;
	protected static final int RESULTS_PANEL_WIDTH = 1200;
	protected static final int RESULTS_PANEL_HEIGHT = RESULTS_LIST_HEIGHT + RESULTS_HEADER_HEIGHT;
	protected static final int RESULTS_LISTS_DISPLAYED = 3;
	protected static final int RESULTS_PANEL_HORIZONTAL_GAP = 5;
	protected static final String SMALL_MAN_FILE = "img/small_man.png";
	protected static final String SMALL_WOMAN_FILE = "img/small_woman.png";
	
	// Lists
	protected static final int LISTS_PANEL_WIDTH = 1000;
	protected static final int LISTS_WIDTH = 350;
	protected static final int LISTS_HEIGHT = 800;
	protected static final int LISTS_DISPLAYED = 3;
	protected static final int LISTS_HORIZONTAL_GAP = 5;
	protected static final int LISTS_INSTRUCTIONS_WIDTH = 500;
	protected static final int LISTS_INSTRUCTIONS_HEIGHT = 300;
	
	// Instructions
	protected static final String LISTS_INSTRUCTIONS = "<html><center>To create a list, click on the plus sign " +
			"next to a result on the Results tab.<p><p>" + 
			"You may make as many lists as you like and " +
			"add individual rooms or entire dorms to them.<p><p>" +
			"You can also remove items or an entire list if you want.<p><p>" +
			"Lastly, you can reorder the elements in these list " +
			"by simply dragging them to a new location.</center></html>";
	
	// Database
	protected static final String DATABASE_NAME = "data/housingdata.db";
	protected static final String ROOM_TABLE = "rooms_with_regressions";
	protected static final String GENDER_TABLE = "genderNeutral";
	protected static final String SOPHOMORE_TABLE = "sophomoreOnly";
	protected static final String SEMESTER_TABLE = "semester";
	protected static final String CAMPUS_AREA_TABLE = "campusArea";

	// LotteryNumberPanel (optimism)
	protected static final int OPTIMISM_HIGH = 0;
	protected static final int OPTIMISM_MEDIUM = 1;
	protected static final int OPTIMISM_LOW = 2;
	
	protected static final String HAPPY_FILE = "img/Grin.png";
	protected static final String OKAY_FILE = "img/Undecided.png";
	protected static final String SAD_FILE = "img/Crying.png";

	protected static final int OPTIMISM_BUTTON_WIDTH = 18;
	
	// XML i/o
	protected static final String XML_TRANFORM_FILE = "transform.xsl";
}
