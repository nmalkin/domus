package domus.gui.canvas;


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

/**
 * An abstract class representing objects that will be drawn using absolute
 * positioning on the screen and therefore require methods for setting and
 * updating the position.
 * 
 * @author nmalkin
 * 
 */
public abstract class DraggablePositionableComponent extends JComponent
        implements Draggable {
    /** Where (within my container) am I located? */
    private Point _position;

    protected DraggablePositionableComponent() {
        _position = new Point(0, 0);

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
        return new Rectangle(this.getPosition().x, this.getPosition().y,
                getWidth(), getHeight());
    }

    /**
     * Updates the object's position on screen by (re-)setting its bounds.
     * 
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    protected void updatePosition() {
        this.setBounds(_position.x, _position.y, this.getPreferredSize().width,
                this.getPreferredSize().height);
    }

    @Override
    public void moveBy(int x, int y) {
        setPosition(_position.x + x, _position.y + y);
    }

    /**
     * This class implements the MouseMotionListener interface to listen for
     * objects being dragged and update them as necessary.
     * 
     * IMPORTANT: components that use this listener MUST implement the Draggable
     * interface. If they do not, mouseDragged will throw an
     * UnsupportedOperationException.
     * 
     * @author nmalkin
     * @see #mouseDragged(MouseEvent)
     * @see UnsupportedOperationException
     * 
     */
    protected static class DragListener implements MouseMotionListener {
        // DragListener is a singleton
        private static final DragListener INSTANCE = new DragListener();

        public static DragListener getInstance() {
            return INSTANCE;
        }

        private Point _lastMousePosition;

        private DragListener() {
            _lastMousePosition = new Point(0, 0);
        }

        @Override
        public void mouseDragged(MouseEvent e)
                throws UnsupportedOperationException {
            int xOffset = e.getX() - _lastMousePosition.x;
            int yOffset = e.getY() - _lastMousePosition.y;

            try {
                Draggable source = (Draggable) e.getSource();
                source.moveBy(xOffset, yOffset);
            } catch (ClassCastException err) {
                throw new UnsupportedOperationException(
                        "DragListener must be assigned to a Droppable instance");
            }

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            _lastMousePosition.x = e.getX();
            _lastMousePosition.y = e.getY();
        }
    }
}
