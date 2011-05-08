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
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;


public class PreferencePanelTab  extends JPanel implements AccordionItem {
	private JPanel _tab;
	private boolean _isOpen;
	private JLabel _expandIcon;
	private JScrollPane _scroller;
	private JPanel _itemsPanel;
	
	private static ImageIcon _openIcon = new ImageIcon(Constants.OPEN_FILE, "open results list");
	private static ImageIcon _closedIcon = new ImageIcon(Constants.CLOSED_FILE, "closed results list");
	
	//constants
	private final int _tabHeight = 40;
	private final int _listWidth = Constants.PREFERENCE_PANEL_WIDTH;
	private final int _scrollPaneHeight = 100;
	private final int _scrollBarWidth = 20;

	PreferencePanelTab(SubGroup subgroup) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentX(LEFT_ALIGNMENT);
		this.setPreferredSize(new Dimension(_listWidth, _tabHeight));
		this.setSize(new Dimension(_listWidth, _tabHeight));
		_isOpen = false;
		
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
		
		for(Person p: subgroup) {
			JLabel person = new JLabel(new ImageIcon(p.getGender().getImage().getScaledInstance(-1, 30, Image.SCALE_SMOOTH)));
			_tab.add(person);
			_tab.add(Box.createRigidArea(new Dimension(5,0)));
		}
		
		_tab.add(Box.createHorizontalGlue());
		this.add(_tab);
		
		
		_itemsPanel = new JPanel();
		_itemsPanel.setPreferredSize(new Dimension(_listWidth, 0));
		_itemsPanel.setSize(new Dimension(_listWidth, 0));
		_itemsPanel.setVisible(true);
		_itemsPanel.setLayout(new BoxLayout(_itemsPanel, BoxLayout.PAGE_AXIS));
		
		_scroller = new JScrollPane(_itemsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_scroller.setBounds(0, _tabHeight, _listWidth, _scrollPaneHeight);
		_scroller.setVisible(false);
		this.add(_scroller);
	}
	
	@Override
	public void addItem(AccordionItem item) {
		_itemsPanel.add((JComponent) item);
		Dimension size = _itemsPanel.getPreferredSize();
		size = new Dimension(size.width, size.height + ((JComponent) item).getHeight());
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
			_isOpen = !_isOpen;
			if (_isOpen) {
				_expandIcon.setIcon(_openIcon);
				_scroller.setVisible(true);
				setPreferredSize(new Dimension(getWidth(), _tabHeight + _scrollPaneHeight));
				revalidate();
			}
			else {
				_expandIcon.setIcon(_closedIcon);
				_scroller.setVisible(false);
				setPreferredSize(new Dimension(getWidth(), _tabHeight));
				revalidate();
			}
		}
		
	}

	@Override
	public void setOpen(boolean open) {
		_isOpen = open;
	}

	@Override
	public int compareTo(AccordionItem o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
