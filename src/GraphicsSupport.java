import java.awt.Rectangle;

/**
 * Provides support code for drawing operations that's used by multiple components
 * and doesn't really fit anywhere else.
 * (Essentially, this is a mixin.)
 * 
 * @author nmalkin
 *
 */
public class GraphicsSupport {
	/**
	 * What fraction of Rectangle A or B's area does their intersection take up?
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double intersectionAreaFraction(Rectangle a, Rectangle b) {
		if(! a.intersects(b)) {
			return 0;
		}
		
		Rectangle intersection = a.intersection(b);
		
		double aArea = a.getWidth() * a.getHeight();
		double bArea = b.getWidth() * b.getHeight();
		double intersectionArea = intersection.getWidth() * intersection.getHeight();
		
		if(aArea == 0 || bArea == 0) {
			return 0;
		} else {
			return intersectionArea / Math.min(aArea, bArea);
		}
	}
}
