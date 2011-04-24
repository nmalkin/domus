import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * This class implements the MouseMotionListener interface to listen for objects being dragged
 * and update them as necessary.
 * 
 * IMPORTANT: components that use this listener MUST implement the Draggable interface.
 * If they do not, mouseDragged will throw an UnsupportedOperationException.
 * 
 * @author nmalkin
 * @see #mouseDragged(MouseEvent)
 * @see UnsupportedOperationException
 *
 */
public class DragListener implements MouseMotionListener {
	// DragListener is a singleton
	private static final DragListener INSTANCE = new DragListener();
	public static DragListener getInstance() { return INSTANCE; }
	
	private Point _lastMousePosition;
	
	private DragListener() {
		_lastMousePosition = new Point(0,0);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) throws UnsupportedOperationException {
		int xOffset = e.getX() - _lastMousePosition.x;
		int yOffset = e.getY() - _lastMousePosition.y;
		
		try {
			Draggable source = (Draggable) e.getSource();
			source.moveBy(xOffset, yOffset);
		} catch(ClassCastException err) {
			throw new UnsupportedOperationException("DragListener must be assigned to a Droppable instance");
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		_lastMousePosition.x = e.getX();
		_lastMousePosition.y = e.getY();
	}

}
