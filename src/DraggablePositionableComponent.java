import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

/**
 * An abstract class representing objects that will be drawn using absolute positioning on the screen
 * and therefore require methods for setting and updating the position.
 * 
 * @author nmalkin
 *
 */
public abstract class DraggablePositionableComponent extends JComponent implements Draggable {
	/** Where (within my container) am I located? */
	private Point _position;
	
	protected DraggablePositionableComponent() {
		 _position = new Point(0,0);
		 
		// add drag listener
		this.addMouseMotionListener(DragListener.getInstance());
	}
	
	public Point getPosition() { 
		return _position;
	}
	
	public void setPosition(Point p) {
		_position = p;
		updatePosition();
	}
	
	public void setPosition(int x, int y) {
		_position.x = x;
		_position.y = y;
		
		updatePosition();
	}
	
	public Rectangle getRectangle() {
		return new Rectangle(this.getPosition().x, this.getPosition().y, getWidth(), getHeight());
	}
	
	/**
	 * Updates the object's position on screen by (re-)setting its bounds.
	 * 
	 * @see java.awt.Component#setBounds(int, int, int, int)
	 */
	protected void updatePosition() {
		this.setBounds(_position.x, _position.y, this.getPreferredSize().width, this.getPreferredSize().height);
	}
	
	@Override
	public void moveBy(int x, int y) {
		setPosition(_position.x + x, _position.y + y);
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
		Point constraint = new Point(xDelta,yDelta);
		
		int xNew = _position.x + xDelta;
		int yNew = _position.y + yDelta;
		
		try {
			Canvas canvas = (Canvas) this.getParent();
			if(canvas != null) {
				int xMin = 0; //Constants.SEPARATOR_X_POSITION;
				int xMax = canvas.getWidth();
				int yMin = 0;
				int yMax = canvas.getHeight();
				
				if(xNew < xMin) constraint.x = xMin - _position.x;
				if(yNew < yMin) constraint.y = yMin - _position.y;
				if(xNew + getWidth()  > xMax) constraint.x = xMax - (_position.x + getWidth());
				if(yNew + getHeight() > yMax) constraint.y = yMax - (_position.y + getHeight());
			}
		} catch(ClassCastException e) {}
		
		return constraint;
	}
	
	@Override
	protected void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);
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
}
