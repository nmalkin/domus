import java.awt.Color;


public class Constants {
	// lottery numbers
	/** the maximum lottery number one is allowed to enter */
	protected static final int MAX_LOTTERY_NUMBER = 800;
	
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
	
	// House
	protected static final java.awt.Color HOUSE_COLOR = Color.BLUE;
	protected static final int HOUSE_PADDING = 45; // pixels
	
	// SubGroup
	protected static final java.awt.Color SUBGROUP_COLOR = Color.GREEN;
	protected static final int SUBGROUP_PADDING = 25; // pixels
	
	// Person images & dimensions
	protected static final String MAN_FILE   = "/home/nmalkin/course/cs032/domus/img/man.png";
	protected static final int    MAN_WIDTH  = 37; // pixels
	protected static final int    MAN_HEIGHT = 100; // pixels

	protected static final String WOMAN_FILE   = "/home/nmalkin/course/cs032/domus/img/woman.png";
	protected static final int    WOMAN_WIDTH  = 46; // pixels
	protected static final int    WOMAN_HEIGHT = 100; // pixels
}
