package domus.data;


import java.awt.Color;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import domus.gui.lists.ResultsListItem;

public class RoomList extends AbstractCollection<ResultsListItem> {
    /** the name by which this RoomList will be referred to */
    private String _name;

    /** the rooms that make up this RoomList */
    private Collection<ResultsListItem> _resultsListItems;

    /** the color for this list, used on results tab */
    private Color _listColor;

    /**
     * Initializes a RoomList with the given name.
     */
    public RoomList(String name) {
        _name = name;
        _resultsListItems = new LinkedList<ResultsListItem>();
        _listColor = null;
    }

    /**
     * Initializes a RoomList with the given name, with the given Rooms.
     * 
     * @param dorms
     */
    public RoomList(Collection<ResultsListItem> items, String name) {
        _name = name;
        _resultsListItems = items;
        _listColor = null;
    }

    /**
     * Initializes a RoomList without a name.
     */
    public RoomList() {
        this("");
    }

    /**
     * Initializes a RoomList without a name, with the given Rooms.
     * 
     * @param dorms
     */
    public RoomList(Collection<ResultsListItem> items) {
        this(items, "");
    }

    @Override
    public boolean add(ResultsListItem e) {
        if (!_resultsListItems.contains(e)) {
            return _resultsListItems.add(e);
        }
        return false;
    }

    @Override
    public boolean remove(Object e) {
        if (_resultsListItems.contains(e)) {
            return _resultsListItems.remove(e);
        }
        return false;
    }

    @Override
    public Iterator<ResultsListItem> iterator() {
        return _resultsListItems.iterator();
    }

    @Override
    public int size() {
        return _resultsListItems.size();
    }

    public String getName() {
        return _name;
    }

    public Color getColor() {
        return _listColor;
    }

    public void setColor(Color color) {
        if (color != null)
            _listColor = color;
        else {
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            _listColor = new Color(r, g, b);
        }
    }
}
