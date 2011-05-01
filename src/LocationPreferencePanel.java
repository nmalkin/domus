import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.*;

/**
 * The LocationPreferencePanel allows the user to select which dorms and areas of campus 
 * they would be okay living in.
 * 
 * @author nmalkin
 *
 */
public class LocationPreferencePanel extends JPanel {
	// LocationPreferencePanel is a singleton.
	private static final LocationPreferencePanel INSTANCE = new LocationPreferencePanel();
	public static LocationPreferencePanel getInstance() { return INSTANCE; }
	
	/** all the dorm checkboxes in this panel */
	private List<DormCheckBox> _dormBoxes;
	
	private LocationPreferencePanel() {
		_dormBoxes = new LinkedList<DormCheckBox>();
		
		CheckBoxListener myListener = new CheckBoxListener();
		
		for(CampusArea area : Database.getCampusAreas()) {
			ParentCheckBox areaBox = new ParentCheckBox(area.getName());
			areaBox.setSelected(true);
			this.add(areaBox);
			
			for(Dorm dorm : area) {
				DormCheckBox dormBox = new DormCheckBox(dorm);
				
				dormBox.setSelected(true);
				dormBox.addItemListener(myListener);
				
				dormBox.setParent(areaBox);
				areaBox.addChild(dormBox);
				
				_dormBoxes.add(dormBox);
				this.add(dormBox);
			}
		}
	}
	
	/**
	 * Waits for people to click on one of the checkboxes,
	 * then updates the currently selected house with a 
	 * LocationPreference that has all the currently selected dorms.
	 *
	 */
	private class CheckBoxListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			LocationPreference preference = new LocationPreference();
			
			for(DormCheckBox checkbox : _dormBoxes) {
				if(checkbox.isSelected()) {
					preference.add(checkbox.getDorm());
				}
			}
			
			if(State.getInstance().getSelectedHouse() != null) {
				State.getInstance().getSelectedHouse().setLocationPreference(preference);
			}
		}	
	}
}
