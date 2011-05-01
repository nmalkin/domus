/**
 * An interface for objects that can be dragged.
 * 
 * @author nmalkin
 *
 */
public interface Draggable {
	/**
	 * Moves this Draggable object by the provided number of pixels.
	 * 
	 * @param x the number of pixels by which the object should be shifted in the x dimension
	 * @param y the number of pixels by which the object should be shifted in the y dimension
	 */
	public void moveBy(int x, int y);
}
