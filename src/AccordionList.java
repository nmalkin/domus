import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * A generic class to represent an AccordionList, that is a list
 * with expandable elements. In this class, list items that can be
 * expanded (and thus contain other list items) will be referred to
 * as tabs. List items inside of tabs will simply be referred to as
 * items. List items must extend JComponent, and implement AccordionItem
 * as well as Comparable.
 * 
 * @author jswarren
 */
public class AccordionList<K extends JComponent & AccordionItem, V extends JComponent & AccordionItem> extends JPanel {
	
	/** A multimap of tabs to items */
	private Multimap<K, V> _lists;
	
	/** A header panel with information for the entire list */
	private JPanel _header;
	
	/** Panel for the lists */
	private JPanel _listsPanel;
	
	/** Scrollpane for the entire list */
	private JScrollPane _scroller;
	
	/** Currently selected item, should only be one at a time */
	private V _selectedItem;
	
	/** Last tab, for border display purposes only */
	private K _lastTab;
	
	private int _width, _height, _headerHeight;
	
	private AccordionList(int width, int height, int headerHeight) {
		super();
		_width = width;
		_height = height;
		_headerHeight = headerHeight;
		_lists = LinkedListMultimap.create();
		this.setPreferredSize(new Dimension(width, height + headerHeight));
		FlowLayout layout = (FlowLayout) this.getLayout();
		layout.setVgap(0);
		this.setAlignmentY(TOP_ALIGNMENT);
		_listsPanel = new JPanel();
		_listsPanel.setPreferredSize(new Dimension(width, 0));
		_listsPanel.setSize(new Dimension(width, 0));
		_listsPanel.setMaximumSize(new Dimension(width, 0));
		_listsPanel.addComponentListener(new ScrollBarVisibilityListener());
		layout = (FlowLayout) _listsPanel.getLayout();
		layout.setVgap(0);
		layout.setHgap(0);
		layout.setAlignment(FlowLayout.LEADING);
		_scroller = new JScrollPane(_listsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_scroller.setPreferredSize(new Dimension(width, height));
		_scroller.setSize(new Dimension(width, height));
		_scroller.getVerticalScrollBar().setBorder(Constants.EMPTY_BORDER);
		_scroller.setViewportBorder(Constants.EMPTY_BORDER);
		_scroller.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		this.add(_scroller);
	}
	
	/** Creates a new AccordionList */
	public static <K extends JComponent & AccordionItem, V extends JComponent & AccordionItem> AccordionList<K,V> create(int width, int height, int headerHeight) {
		return new AccordionList<K,V>(width, height, headerHeight);
	}
	
	public void updateHeight(int height) {
		_height = height;
		this.setPreferredSize(new Dimension(_width, _height + _headerHeight));
	}
	
	/** Add a tab (an expandable item) to the list */
	public void addTab(K tab) {
		_listsPanel.add(tab);
		Dimension size = _listsPanel.getPreferredSize();
		_listsPanel.setPreferredSize(new Dimension(size.width, size.height + tab.getPreferredSize().height));
		_listsPanel.setSize(new Dimension(size.width, size.height + tab.getPreferredSize().height));
		_listsPanel.setMaximumSize(new Dimension(size.width, size.height + tab.getPreferredSize().height));
		tab.setVisible(true);
		_lastTab = tab;
	}
	
	/** Remove a tab from the list */
	public void removeTab(K tab) {
		_lists.removeAll(tab);
		_listsPanel.remove(tab);
		Dimension size = _listsPanel.getPreferredSize();
		_listsPanel.setPreferredSize(new Dimension(size.width, size.height - tab.getPreferredSize().height));
		_listsPanel.setSize(new Dimension(size.width, size.height - tab.getPreferredSize().height));
		_listsPanel.setMaximumSize(new Dimension(size.width, size.height - tab.getPreferredSize().height));
		tab.setVisible(false);
	}
	
	/** Changes the size of the panel if a tab is opened or closed */
	public void tabDisplayChanged(int heightChange) {
		Dimension size = _listsPanel.getPreferredSize();
		int height = size.height + heightChange;
		_listsPanel.setPreferredSize(new Dimension(size.width, height));
		_listsPanel.setSize(new Dimension(size.width, height));
		_listsPanel.setMaximumSize(new Dimension(size.width, height));
	}
		
	/** Add an item to a tab in the list */
	public void addListItem(K tab, V item) {
		_lists.put(tab, item);
		tab.addItem(item);
		if (!_lists.keySet().isEmpty()) {
			K previousTab = null;
			for (K t : _lists.keySet())
				if (t != null)
					previousTab = t;
			if (previousTab.isFullWidth())
				previousTab.resizeItem(new Dimension(-_scroller.getVerticalScrollBar().getSize().width, 0));
		}
	}
	
	/** Remove an item from the list */
	public void removeListItem(K tab, V item) {
		_lists.remove(tab, item);
		tab.removeItem(item);
	}
	
	/** Set header to be displayed above the list */
	public void setHeader(JPanel panel) {
		if (_header != null) {
			this.remove(_header);
		}
		_header = panel;
		this.add(_header, 0);
	}
	
	/** 
	 * Sets the currently selected item. Only one should be
	 * selected at once.
	 */
	public void setSelectedItem(V item) {
		if (_selectedItem != null) {
			_selectedItem.setOpen(false);
		}
		_selectedItem = item;
	}
	
	/** Return a set of the tabs in this list */
	public Collection<K> getTabs() {
		return _lists.keySet();
	}
	
	/** Get last tab */
	public K getLastTab() {
		return _lastTab;
	}
	
	/** Return a collection of the items under specified tab */
	public Collection<V> getItemsFromTab(K tab) {
		TreeMultimap<K, V> lists = (TreeMultimap<K, V>) _lists;
		Collection<V> items = lists.get(tab);
		return items;
	}
	
	// Maybe the tabs should do this themselves?
		
	/** Animates the opening of a tab */
	public void animateOpen(K tab) {
		//TODO
		
	}
	
	/** Animates the closing of a tab */
	public void animateClose(K tab) {
		//TODO
		
	}

	private class ScrollBarVisibilityListener extends ComponentAdapter {
		
		boolean _scrollbarVisible;
		
		@Override
		public void componentResized(ComponentEvent e) {
			// determine if the width needs to change
			boolean visible = _scroller.getVerticalScrollBar().isVisible();
			if (visible != _scrollbarVisible) {
				int width = _scroller.getVerticalScrollBar().getSize().width;

				// in which direction does it need to change
				if (visible)
					width = -width;
				
				// change the width of each list
				for (K tab : _lists.keySet()) {
					Dimension size = tab.getSize();
					tab.resizeItem(new Dimension(width, 0));
				}
				_scrollbarVisible = visible;
				int bottom = 0;
				if (_scrollbarVisible)
					bottom = 1;
				_scroller.setBorder(BorderFactory.createMatteBorder(1, 0, bottom, 0, Color.BLACK));
			}
		}
		
	}
}
