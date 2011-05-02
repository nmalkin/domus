import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The LocationPreferencePanel allows the user to select which dorms and areas of campus 
 * they would be okay living in.
 * 
 * @author nmalkin
 *
 */
public class LocationPreferencePanel extends JPanel implements ChangeListener {
	// LocationPreferencePanel is a singleton.
	private static final LocationPreferencePanel INSTANCE = new LocationPreferencePanel();
	public static LocationPreferencePanel getInstance() { return INSTANCE; }
	
	/** all the dorm checkboxes in this panel */
	private Map<Dorm, DormCheckBox> _dormBoxes;
	
	/** flag for preventing concurrent modification */
	private boolean _changingState = false;
	
	private LocationPreferencePanel() {
		_dormBoxes = new HashMap<Dorm, DormCheckBox>();
		
		State.getInstance().setSelectedHouseChangeListener(this);
		
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
				
				_dormBoxes.put(dorm, dormBox);
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
			if(! _changingState) {
				LocationPreference preference = new LocationPreference();
				
				for(DormCheckBox checkbox : _dormBoxes.values()) {
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

	@Override
	public void stateChanged(ChangeEvent e) {
		// the currently selected house has changed
		_changingState = true;
		
		// clear all the current values
		for(DormCheckBox checkbox : _dormBoxes.values()) {
			checkbox.setSelected(false);
		}
		
		// update the checkboxes accordingly
		if(State.getInstance().getSelectedHouse() != null) {
			for(Dorm dorm : State.getInstance().getSelectedHouse().getLocationPreference()) {
				DormCheckBox checkbox = _dormBoxes.get(dorm);
				if(checkbox != null) { // null shouldn't happen
					checkbox.setSelected(true);
				}
			}
		}
		
		_changingState = false;
	}
}
