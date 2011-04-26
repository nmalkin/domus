import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

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
	
	/** Free vertical space */
	private int _availSpace;
	
	/** Variables for controlling animations */
	private int _shownSpace;
	private int _hiddenSpace;
	private int _timeStep = 10;
	
	/** A multimap of tabs to items */
	private Multimap<K, V> _lists;
	
	private AccordionList() {
		super();
		_lists = TreeMultimap.create();
		FlowLayout layout = (FlowLayout) this.getLayout();
		layout.setVgap(0);
		this.setAlignmentY(TOP_ALIGNMENT);
	}
	
	/** Creates a new AccordionList */
	public static <K extends JComponent & AccordionItem, V extends JComponent & AccordionItem> AccordionList<K,V> create() {
		return new AccordionList<K,V>();
	}
	
	/** Add a tab (an expandable item) to the list */
	public void addTab(K tab) {
		this.add(tab);
		tab.setVisible(true);
	}
	
	/** Add an item to a tab in the list */
	public void addListItem(K tab, V item) {
		_lists.put(tab, item);
		tab.addItem(item);
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
	
}
