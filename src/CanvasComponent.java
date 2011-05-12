import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * An abstract class representing objects that will be drawn on the Canvas.
 * They must be able to be dragged and change their position 
 * (hence, this is a subclass of DraggablePositionableComponent).
 * They also share functionality related to movement constraints
 * (they can't move off the Canvas)
 * and certain behavior (hovering over trash).
 * 
 * @author nmalkin
 *
 */
public abstract class CanvasComponent extends DraggablePositionableComponent {
	
	protected CanvasComponent() {
		super();
		
		this.addMouseMotionListener(new TrashHoverListener());
	}
	
	/**
	 * Given how much you want this component to move,
	 * returns how much you can *actually* move this component
	 * without running into the walls of the Canvas.
	 * 
	 * @param xDelta how much you want the component to move in the x dimension
	 * @param yDelta how much you want the component to move in the y dimension
	 * @return Point with values for how much you are allowed to move
	 */
	protected Point movementConstraint(int xDelta, int yDelta) {
		int myX = this.getPosition().x;
		int myY = this.getPosition().y;
		
		Point constraint = new Point(xDelta,yDelta);
		
		int xNew = myX + xDelta;
		int yNew = myY + yDelta;
		
		try {
			Canvas canvas = (Canvas) this.getParent();
			if(canvas != null) {
				int xMin = 0; //Constants.SEPARATOR_X_POSITION;
				int xMax = canvas.getWidth();
				int yMin = 0;
				int yMax = canvas.getHeight();
				
				if(xNew < xMin) constraint.x = xMin - myX;
				if(yNew < yMin) constraint.y = yMin - myY;
				if(xNew + getWidth()  > xMax) constraint.x = xMax - (myX + getWidth());
				if(yNew + getHeight() > yMax) constraint.y = yMax - (myY + getHeight());
			}
		} catch(ClassCastException e) {}
		
		return constraint;
	}
	
	/**
	 * Checks if the current component is out of the bounds for the Canvas.
	 * 
	 * NOTE that for this method to work, the parent of this element 
	 * must be the Canvas.
	 * Otherwise an UnsupportedOperationException is thrown.
	 * 	TODO: after testing is over, it might be a good idea to change this behavior to just return true on error.
	 * 
	 * @return
	 */
	@Deprecated
	protected boolean outOfBounds() {
		Canvas canvas = null;
		
		try {
			canvas = (Canvas) this.getParent();
		} catch(ClassCastException e) {
			throw new UnsupportedOperationException("expected parent of element to be Canvas");
		}
		
		if(canvas == null) {
			throw new UnsupportedOperationException("cannot be out of bounds when there is no parent");
		}
		
		Rectangle canvasRectangle = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
		double intersectionArea = GraphicsSupport.intersectionAreaFraction(getRectangle(), canvasRectangle);
		
		if(intersectionArea < 1) { // some part of the rectangle is out of bounds
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns true if this CanvasComponent is over the trash icon on the Canvas.
	 * 
	 * TODO: consider caching this value, since now it's being recalculated several times between moves
	 * 
	 * @return
	 */
	protected boolean overTrashIcon() {
		return Canvas.getInstance().overTrashIcon(this);
	}
	
	
	protected class TrashHoverListener extends MouseAdapter {
		private boolean _trashOpened;
		
		private TrashHoverListener() {
			_trashOpened = false;
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			CanvasComponent source = (CanvasComponent) e.getSource();
			if(source.overTrashIcon()) {
				if(! _trashOpened) { // if we haven't already opened the trash
					Canvas.getInstance().openTrash();
					_trashOpened = true;
				}
			} else {
				if(_trashOpened) { // if we haven't already closed the trash
					Canvas.getInstance().closeTrash();
					_trashOpened = false;
				}
			}
		}
	}
}
