package domus;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import domus.data.Dorm;
import domus.data.Group;
import domus.data.Room;
import domus.data.RoomList;
import domus.db.Database;
import domus.gui.canvas.House;
import domus.gui.canvas.SubGroup;

/**
 * Stores all variable, non-GUI data in the program.
 * 
 * @author nmalkin, jswarren
 */
public class State {
    // State is a singleton
    private static final State INSTANCE = new State();

    public static State getInstance() {
        return INSTANCE;
    }

    /** the (current) group */
    private Group _group;

    /** the most recent results (based on the current group) */
    private Multimap<SubGroup, Room> _results;

    /** room lists */
    private List<RoomList> _roomLists;

    /** the house that's currently selected on the screen */
    private House _selectedHouse;

    /** listeners for whether or not the selected house is changing */
    private List<ChangeListener> _selectedHouseChangeListeners;

    /** listener for whether the selected probability model is changing */
    private ChangeListener _selectedProbabilityModelChangeListener;

    /** listener for whether the selected years are changing */
    private ChangeListener _selectedYearsChangeListener;

    /**
     * Should we use the regression probability method? (if false, "simple"
     * technique will be used)
     */
    private boolean _useRegressionProbability;

    /** Years to use for calculating probability. */
    private List<Integer> _years;

    /** Years NOT to use for calculating probability. */
    private List<Integer> _ignoredYears;

    private State() {
        _group = new Group();
        _results = TreeMultimap.create();
        _roomLists = new LinkedList<RoomList>();
        _selectedHouse = null;
        _selectedHouseChangeListeners = new LinkedList<ChangeListener>();
        _selectedProbabilityModelChangeListener = null;
        _selectedYearsChangeListener = null;
        _useRegressionProbability = true;
        _years = new LinkedList<Integer>();
        _ignoredYears = new LinkedList<Integer>();

        for (int year : Database.getYears())
            _years.add((Integer) year);
    }

    public Group getGroup() {
        return _group;
    }

    public void setGroup(Group group) { // TODO: do we want to expose it like
                                           // this? -RE: No...? -Sumner
        _group = group;
    }

    /**
     * Returns whether or not the user has chosen to use regression probability.
     * 
     * @return true if you should use the regression probability technique
     */
    public boolean useRegressionProbability() {
        return _useRegressionProbability;
    }

    /**
     * Allows you to set whether or not to use the regression probability
     * technique.
     * 
     * @param use
     *            true if we should use the regression probability technique,
     *            false if we shouldn't
     */
    public void useRegressionProbability(boolean use) {
        _useRegressionProbability = use;

        // notify the listener that the selected probability model have changed
        if (_selectedProbabilityModelChangeListener != null) {
            ChangeEvent e = new ChangeEvent(this);
            _selectedProbabilityModelChangeListener.stateChanged(e);
        }
    }

    /**
     * Attaches the given ChangeListener such that a ChangeEvent is fired
     * whenever the selected house is changed.
     * 
     * @param l
     */
    public void setSelectedProbabilityModelChangeListener(ChangeListener l) {
        if (l != null)
            _selectedProbabilityModelChangeListener = l;
    }

    public Integer[] getYears() {
        Integer[] years = new Integer[_years.size()];
        for (int i = 0; i < years.length; i++)
            years[i] = _years.get(i);
        return years;
    }

    public Integer[] getIgnoredYears() {
        Integer[] years = new Integer[_ignoredYears.size()];
        for (int i = 0; i < years.length; i++)
            years[i] = _ignoredYears.get(i);
        return years;
    }

    public void addYear(Integer year) {
        _years.add(year);
        _ignoredYears.remove(year);

        // notify the listener that the selected years have changed
        if (_selectedYearsChangeListener != null) {
            ChangeEvent e = new ChangeEvent(this);
            _selectedYearsChangeListener.stateChanged(e);
        }
    }

    public void removeYear(Integer year) {
        _years.remove(year);
        _ignoredYears.add(year);

        // notify the listener that the selected years have changed
        if (_selectedYearsChangeListener != null) {
            ChangeEvent e = new ChangeEvent(this);
            _selectedYearsChangeListener.stateChanged(e);
        }
    }

    /**
     * Attaches the given ChangeListener such that a ChangeEvent is fired
     * whenever the selected house is changed.
     * 
     * @param l
     */
    public void setSelectedYearsChangeListener(ChangeListener l) {
        if (l != null)
            _selectedYearsChangeListener = l;
    }

    public void setSelectedHouse(House h) {
        _selectedHouse = h;

        for (ChangeListener l : _selectedHouseChangeListeners) {
            if (l == null)
                continue;
            ChangeEvent e = new ChangeEvent(this);
            l.stateChanged(e);
        }
    }

    public House getSelectedHouse() {
        return _selectedHouse;
    }

    /**
     * Attaches the given ChangeListener such that a ChangeEvent is fired
     * whenever the selected house is changed.
     * 
     * @param l
     */
    public void addSelectedHouseChangeListener(ChangeListener l) {
        if (l != null)
            _selectedHouseChangeListeners.add(l);
    }

    /**
     * Fetches results from the Database based on new preferences.
     */
    public void updateResults() {
        // get years
        Integer[] years;
        if (_useRegressionProbability) { // if we're using the regression
                                         // probability model, we want all the
                                         // years
            years = getYears();
        } else { // otherwise, we only want the years that have been selected
            years = new Integer[_years.size()];
            for (int i = 0; i < _years.size(); i++) {
                years[i] = _years.get(i);
            }
        }

        _results.clear();

        boolean sophomoreOnlyEligible = _group.isSophomore();
        for (House h : _group) { // for each house:
            List<HashSet<Dorm>> dormsUsedBySubgroups = new LinkedList<HashSet<Dorm>>();

            Collection<Dorm> locations = h.getLocationPreference();
            for (SubGroup sg : h) { // for each subgroup:
                HashSet<Dorm> dormsUsedByThisSubgroup = new HashSet<Dorm>();

                // get results
                Collection<Room> results = Database.getResults(locations, sg
                        .getOccupancy(), years, (!sg.sameGender()),
                        sophomoreOnlyEligible);

                for (Room r : results) { // for each room in the results:
                    _results.put(sg, r); // save the room

                    dormsUsedByThisSubgroup.add(r.getDorm()); // save what dorm
                                                              // it's in
                }

                dormsUsedBySubgroups.add(dormsUsedByThisSubgroup);
            }

            if (dormsUsedBySubgroups.isEmpty())
                continue; // there are no results for this house. nothing to see
                          // here.

            // find all the dorms that are shared by the resulting rooms
            Set<Dorm> commonDorms = dormsUsedBySubgroups.get(0);
            for (HashSet<Dorm> dormUsedByASubgroup : dormsUsedBySubgroups) {
                commonDorms.retainAll(dormUsedByASubgroup);
            }

            // go back through the result rooms for this subgroup and remove any
            // ones that are not in a shared dorm
            for (SubGroup sg : h) {
                Iterator<Room> it = _results.get(sg).iterator();
                while (it.hasNext()) {
                    if (!commonDorms.contains(it.next().getDorm())) { // if the
                                                                      // room
                                                                      // has an
                                                                      // un-common
                                                                      // (unique)
                                                                      // dorm
                        it.remove();
                    }
                }
            }
        }
    }

    public Multimap<SubGroup, Room> getResults() {
        return TreeMultimap.create(_results);
    }

    public List<RoomList> getRoomLists() {
        return new LinkedList<RoomList>(_roomLists);
    }

    public void addRoomList(RoomList list) {
        _roomLists.add(list);
    }

    public void removeRoomList(RoomList list) {
        _roomLists.remove(list);
    }

    public void export(String filename) {
        // TODO: consider returning boolean depending on success/failure
    }

    public void load(String filename) {
        // TODO: consider returning boolean depending on success/failure
    }

}
