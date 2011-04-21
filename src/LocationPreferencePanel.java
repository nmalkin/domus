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
	
	private LocationPreferencePanel() {
		for(CampusArea area : Database.getCampusAreas()) {
			ParentCheckBox areaBox = new ParentCheckBox(area.getName());
			areaBox.setSelected(true);
			this.add(areaBox);
			
			for(Dorm dorm : area) {
				ChildCheckBox dormBox = new ChildCheckBox(dorm.getName());
				dormBox.setSelected(true);
				dormBox.setParent(areaBox);
				areaBox.addChild(dormBox);
				this.add(dormBox);
			}
		}
	}
}
