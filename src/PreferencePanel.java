import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;


public class PreferencePanel extends JPanel {

	private AccordionList<PreferencePanelTab, PreferencePanelItem> preferenceList;

	private final int _listWidth = Constants.PREFERENCE_PANEL_WIDTH;
	private final int _listHeight = Constants.PREFERENCE_PANEL_HEIGHT;

	protected PreferencePanel() {
		Dimension size = new Dimension(Constants.PREFERENCE_PANEL_WIDTH,0);
		this.setPreferredSize(size);
		this.setSize(size);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new MatteBorder(0, 0, 0, 1, Color.GRAY));

		/*JLabel instructions = new JLabel(Constants.PREFERENCE_PANEL_INSTRUCTIONS);
		instructions.setMinimumSize(new Dimension(Constants.PREFERENCE_PANEL_WIDTH,0));
		this.add(instructions);*/
		
		preferenceList = AccordionList.create(_listWidth, _listHeight, 0);

		for(House h: State.getInstance().getGroup()) {
			PreferencePanelTab tab = new PreferencePanelTab(h, preferenceList);
			preferenceList.addTab(tab);
			
			List<Dorm> dorms = new LinkedList<Dorm>();
			for (Dorm d : h.getLocationPreference())
				dorms.add(d);
			Collections.sort(dorms, new DormComparator());
			for (Dorm d : dorms){
				PreferencePanelItem dorm = new PreferencePanelItem(h, d, preferenceList);

				preferenceList.addListItem(tab, dorm);
			}
		}

		this.add(preferenceList);
	}
	
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
