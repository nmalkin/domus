import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;


public class PreferencePanel extends JPanel {
	
	private AccordionList<PreferencePanelTab, PreferencePanelItem> preferenceList;
	
	protected PreferencePanel() {
		Dimension size = new Dimension(Constants.PREFERENCE_PANEL_WIDTH,0);
		this.setPreferredSize(size);
		this.setSize(size);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new MatteBorder(0,0,0,1,Color.GRAY));
		
		preferenceList = AccordionList.create();

		for(House h: State.getInstance().getGroup()) {
			for(SubGroup sg : h) {
				PreferencePanelTab tab = new PreferencePanelTab(sg);
				preferenceList.addTab(tab);
				
				for(Dorm d: h.getLocationPreference()){
					PreferencePanelItem dorm = new PreferencePanelItem(d);
					//dorm.setInsets(tab.getBorderInsets());
					
					preferenceList.addListItem(tab, dorm);
				}
			}
		}
		
		this.add(preferenceList);
	}
	
	protected void updatePreferences() {
		this.remove(preferenceList);
		preferenceList = AccordionList.create();

		for(House h: State.getInstance().getGroup()) {
			for(SubGroup sg : h) {
				PreferencePanelTab tab = new PreferencePanelTab(sg);
				preferenceList.addTab(tab);
				
				for(Dorm d: h.getLocationPreference()){
					PreferencePanelItem dorm = new PreferencePanelItem(d);
					//dorm.setInsets(tab.getBorderInsets());
					
					preferenceList.addListItem(tab, dorm);
				}
			}
		}
		
		this.add(preferenceList);
	}

}
