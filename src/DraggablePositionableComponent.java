import java.awt.Point;

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
	
	@Override
	protected void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);
//		System.out.println(this.toString() + " " + getPosition());
	}
}
