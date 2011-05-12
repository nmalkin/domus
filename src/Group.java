

import java.awt.Point;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Group extends AbstractCollection<House> {

    /** the houses that make up this group */
    private Collection<House> _houses;

    /** What is our [possibly estimated] lottery number? */
    private int _lotteryNumber;

    /** Where on screen am I located? */
    private Point _position;

    /**
     * Is this a sophomore group? (Are they eligible for sophomore-only
     * housing?)
     */
    private boolean _sophomore;

    /** listener for changes in the sophomore status or lottery number */
    private List<ChangeListener> _groupStateChangeListeners;

    public Group() {
        _houses = new LinkedList<House>();
        _position = new Point(0, 0);
        _sophomore = false;
        _groupStateChangeListeners = new LinkedList<ChangeListener>();
    }

    @Override
    public boolean add(House e) {
        return _houses.add(e);

    }

    public boolean remove(House e) {
        return _houses.remove(e);

    }

    @Override
    public Iterator<House> iterator() {
        return _houses.iterator();
    }

    @Override
    public int size() {
        return _houses.size();
    }

    public Point getPosition() {
        return _position;
    }

    public void setPosition(Point p) {
        _position = p;
    }

    public int getLotteryNumber() {
        return _lotteryNumber;
    }

    public void setLotteryNumber(int number) {
        _lotteryNumber = number;

        // notify the listeners that the lottery number has changed
        for (ChangeListener l : _groupStateChangeListeners) {
            if (l != null) {
                ChangeEvent e = new GroupChangeEvent(this,
                        GroupChangeEvent.UPDATE_PROBABILITIES);
                l.stateChanged(e);
            }
        }
    }

    /**
     * Sets the sophomore status.
     * 
     * @param soph
     *            true if the group is eligible for sophomore-only housing
     */
    public void setSophomoreStatus(boolean soph) {
        _sophomore = soph;

        // notify the listeners that the sophomore only status have changed
        for (ChangeListener l : _groupStateChangeListeners) {
            if (l != null) {
                ChangeEvent e = new GroupChangeEvent(this,
                        GroupChangeEvent.UPDATE_RESULTS);
                l.stateChanged(e);
            }
        }
    }

    public boolean isSophomore() {
        return _sophomore;
    }

    /**
     * Attaches the given ChangeListener such that a ChangeEvent is fired
     * whenever the selected house is changed.
     * 
     * @param l
     */
    public void addGroupStateChangeListener(ChangeListener l) {
        if (l != null)
            _groupStateChangeListeners.add(l);
    }

    /**
     * Returns the total number of people in this group.
     * 
     * @return
     */
    public int numberOfPeople() {
        int count = 0;

        for (House h : _houses) {
            count += h.numberOfPeople();
        }

        return count;
    }

    public class GroupChangeEvent extends ChangeEvent {

        // constants for what to update
        private static final boolean UPDATE_RESULTS = true;
        private static final boolean UPDATE_PROBABILITIES = false;

        // field for update status of event
        private boolean _updateType;

        public GroupChangeEvent(Object source, boolean updateType) {
            super(source);
            _updateType = updateType;
        }

        public boolean getUpdateType() {
            return _updateType;
        }

    }
}
