import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PreferencePanelTab  extends JPanel implements AccordionItem {
	private JPanel _tab;
	private boolean _isOpen;
	private JLabel _expandIcon;
	private JPanel _itemsPanel;
	private AccordionList<PreferencePanelTab, PreferencePanelItem> _parentList;

	private static ImageIcon _openIcon = new ImageIcon(Constants.OPEN_FILE, "open results list");
	private static ImageIcon _closedIcon = new ImageIcon(Constants.CLOSED_FILE, "closed results list");

	//constants
	private final int _tabHeight = 40;
	private final int _listWidth = Constants.PREFERENCE_PANEL_WIDTH;

	PreferencePanelTab(House house, AccordionList<PreferencePanelTab, PreferencePanelItem> list) {
		super();
		this.setLayout(null);
		this.setPreferredSize(new Dimension(_listWidth, _tabHeight));
		this.setSize(new Dimension(_listWidth, _tabHeight));
		_isOpen = false;
		_parentList = list;

		_expandIcon = new JLabel();
		_expandIcon.setIcon(_closedIcon);

		//Set up expandable tab
		_tab = new JPanel();
		_tab.setLayout(new BoxLayout(_tab, BoxLayout.X_AXIS));
		_tab.setMinimumSize(new Dimension(_listWidth, 0));
		_tab.setBounds(0, 0, _listWidth, _tabHeight);
		_tab.addMouseListener(new ExpandListener());

		_tab.add(_expandIcon);
		_tab.add(Box.createRigidArea(new Dimension(10,40)));

		for(SubGroup sg : house) {
			for(Person p: sg) {
				JLabel person = new JLabel(new ImageIcon(p.getGender().getImage().getScaledInstance(-1, 30, Image.SCALE_SMOOTH)));
				if(!p.getName().equals("A Person")) person.setToolTipText(p.getName());
				_tab.add(person);
				_tab.add(Box.createRigidArea(new Dimension(5,0)));
			}
			_tab.add(Box.createRigidArea(new Dimension(10,0)));
		}

		_tab.add(Box.createHorizontalGlue());
		this.add(_tab);

		_itemsPanel = new JPanel();
		_itemsPanel.setPreferredSize(new Dimension(_listWidth, 0));
		_itemsPanel.setSize(new Dimension(_listWidth, 0));
		_itemsPanel.setVisible(false);
		_itemsPanel.setLayout(new BoxLayout(_itemsPanel, BoxLayout.PAGE_AXIS));
		_itemsPanel.setLocation(0,_tabHeight);

		this.add(_itemsPanel);
	}

	@Override
	public void addItem(AccordionItem item) {
		_itemsPanel.add((JComponent) item);
		Dimension size = _itemsPanel.getPreferredSize();
		size = new Dimension(size.width, size.height + Constants.REMOVE_ICON_HEIGHT);
		_itemsPanel.setPreferredSize(size);
		_itemsPanel.setSize(size);
	}

	@Override
	public int getComparisonValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isOpen() {
		return _isOpen;
	}

	@Override
	public void removeItem(AccordionItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setComparisonValue(int index) {
		// TODO Auto-generated method stub

	}

	private class ExpandListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			boolean open = !isOpen();
			if (open) {
				_expandIcon.setIcon(_openIcon);
				_itemsPanel.setVisible(true);
				setPreferredSize(new Dimension(getWidth(), _tabHeight + _itemsPanel.getPreferredSize().height));
				revalidate();
				setOpen(open);
			}
			else {
				setOpen(open);
				_expandIcon.setIcon(_closedIcon);
				_itemsPanel.setVisible(false);
				setPreferredSize(new Dimension(getWidth(), _tabHeight));
				revalidate();
			}	
		}

	}

	@Override
	public void setOpen(boolean open) {
		_parentList.tabDisplayChanged(open, _itemsPanel.getPreferredSize().height);
		_isOpen = open;
	}

	@Override
	public int compareTo(AccordionItem o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/** 
	 * Resizes the list. Called when the visibility
	 * of the vertical scrollbar on the parent components
	 * changes.
	 */
	public void resizeItem(Dimension d) {
		//resize this component
		Dimension size = this.getSize();
		this.setPreferredSize(new Dimension(d.width, size.height));
		this.setSize(new Dimension(d.width, size.height));
		
		//resize tab
		size = _tab.getSize();
		_tab.setPreferredSize(new Dimension(d.width, size.height));
		_tab.setSize(new Dimension(d.width, size.height));

		//resize itemsPanel
		size = _itemsPanel.getSize();
		_itemsPanel.setPreferredSize(new Dimension(d.width, size.height));
		_itemsPanel.setSize(new Dimension(d.width, size.height));
		
		//resize items
		for (Component c : _itemsPanel.getComponents()) {
			size = c.getSize();
			c.setPreferredSize(new Dimension(d.width, size.height));
			c.setSize(new Dimension(d.width, size.height));
		}
	}

}
