import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;


public class PreferencePanel extends JPanel {

	private AccordionList<PreferencePanelTab, PreferencePanelItem> _preferenceList;

	private int _listWidth = Constants.PREFERENCE_PANEL_WIDTH - 1;
	private int _listHeight = Constants.PREFERENCE_PANEL_HEIGHT;

	protected PreferencePanel() {
		Dimension size = new Dimension(Constants.PREFERENCE_PANEL_WIDTH,0);
		this.setPreferredSize(size);
		this.setSize(size);
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.setAlignmentX(CENTER_ALIGNMENT);
		this.setBorder(new MatteBorder(0, 0, 0, 1, Color.GRAY));
		
		JLabel title = new JLabel("preference panel");
		title.setFont(Constants.DOMUS_FONT.deriveFont(Font.PLAIN, 18));
		
		this.add(title);
		size = Toolkit.getDefaultToolkit().getScreenSize();
		_preferenceList = AccordionList.create(_listWidth, Math.min(size.height - 100, _listHeight), 0);

		for(House h: State.getInstance().getGroup()) {
			PreferencePanelTab tab = new PreferencePanelTab(h, _preferenceList);
			_preferenceList.addTab(tab);
			
			List<Dorm> dorms = new LinkedList<Dorm>();
			for (Dorm d : h.getLocationPreference())
				dorms.add(d);
			Collections.sort(dorms, new DormComparator());
			for (Dorm d : dorms){
				PreferencePanelItem dorm = new PreferencePanelItem(h, d, _preferenceList);

				_preferenceList.addListItem(tab, dorm);
			}
		}

		this.add(_preferenceList);
	}
	
	public void updateHeight(int height) {
		_listHeight = height;
		_preferenceList.updateHeight(height);
	}
	
	/**
	 * Compares Dorms by CampusArea (alphabetically) and then
	 * lexicographically inside of that.
	 * 
	 * @author jswarren
	 */
	private class DormComparator implements Comparator<Dorm> {

		Map<Dorm, CampusArea> _campusAreas;
		
		public DormComparator() {
			_campusAreas = new HashMap<Dorm, CampusArea>();
			for (CampusArea ca : Database.getCampusAreas()) {
				for (Dorm d : ca)
					_campusAreas.put(d, ca);
			}
		}
		
		@Override
		public int compare(Dorm o1, Dorm o2) {
			// TODO Auto-generated method stub
			CampusArea ca1 = _campusAreas.get(o1);
			CampusArea ca2 = _campusAreas.get(o2);
			
			int result = ca1.compareTo(ca2);
			if (result < 0)
				return -1;
			if (result > 1)
				return 1;
			return o1.compareTo(o2);
		}
		
	}
}
