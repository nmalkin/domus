import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The LocationPreferencePanel allows the user to select which dorms and areas of campus 
 * they would be okay living in.
 * 
 * @author nmalkin, mmschnei
 *
 */
public class LocationPreferencePanel extends JPanel implements ChangeListener {
	// LocationPreferencePanel is a singleton.
	private static final LocationPreferencePanel INSTANCE = new LocationPreferencePanel();
	public static LocationPreferencePanel getInstance() { return INSTANCE; }
	
	/** all the checkboxes on this panel. ever. */
	private Collection<JCheckBox> _checkBoxes;
	
	/** all the dorm checkboxes in this panel */
	private Map<Dorm, DormCheckBox> _dormBoxes;
	
	/** flag for preventing concurrent modification */
	private boolean _changingState = false;
	
	private LocationPreferencePanel() {
		_checkBoxes = new LinkedList<JCheckBox>();
		_dormBoxes = new HashMap<Dorm, DormCheckBox>();
		
		State.getInstance().addSelectedHouseChangeListener(this);
		
		CheckBoxListener myListener = new CheckBoxListener();
		
		this.setLayout(new BorderLayout());
		this.setBorder(new MatteBorder(1,0,0,0,Color.GRAY));
		
		JLabel title = new JLabel("LOCATION PREFERENCES".toLowerCase());
		title.setFont(Constants.DOMUS_FONT.deriveFont(Font.PLAIN, 24));
		
		ParentCheckBox masterBox = new ParentCheckBox("I'm willing to live anywhere!");
		_checkBoxes.add(masterBox);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(title);
		topPanel.add(Box.createRigidArea(new Dimension(10,0)));
		topPanel.add(masterBox);
		
		JPanel locations = new JPanel();
		locations.setLayout(new BoxLayout(locations, BoxLayout.X_AXIS));
		
		for(CampusArea area : Database.getCampusAreas()) {
			JPanel areaPanel = new JPanel();
			areaPanel.setLayout(new BoxLayout(areaPanel, BoxLayout.Y_AXIS));
//			areaPanel.setPreferredSize(new Dimension(200,260));
//			areaPanel.setMinimumSize(new Dimension(200,260));
			areaPanel.setBorder(new LineBorder(Color.GRAY));
			
			JPanel areaNamePanel = new JPanel();
			areaNamePanel.setLayout(new BoxLayout(areaNamePanel, BoxLayout.X_AXIS));
			MatteBorder line = new MatteBorder(0, 0, 1, 0, Color.GRAY);
			
			areaNamePanel.setBorder(line);
//			areaNamePanel.setPreferredSize(new Dimension(200,25));
//			areaNamePanel.setMinimumSize(new Dimension(200,25));
			
			ParentCheckBox areaBox = new ParentCheckBox(area.getName());
			areaBox.setSelected(false);
			_checkBoxes.add(areaBox);
			
			areaNamePanel.add(areaBox);
			areaNamePanel.add(Box.createHorizontalGlue());
			areaPanel.add(areaNamePanel);
			
			for(Dorm dorm : area) {
				JPanel dormPanel = new JPanel();
				dormPanel.setLayout(new BoxLayout(dormPanel, BoxLayout.X_AXIS));
//				dormPanel.setPreferredSize(new Dimension(200, 25));
//				dormPanel.setMinimumSize(new Dimension(200,25));
				
				DormCheckBox dormBox = new DormCheckBox(dorm);
				
				dormBox.setSelected(false);
				dormBox.addItemListener(myListener);
				
				dormBox.addParent(areaBox);
				dormBox.addParent(masterBox);
				areaBox.addChild(dormBox);
				masterBox.addChild(dormBox);
				
				_dormBoxes.put(dorm, dormBox);
				_checkBoxes.add(dormBox);
				
//				dormPanel.add(Box.createRigidArea(new Dimension(15,0)));
				dormPanel.add(dormBox);
				dormPanel.add(Box.createHorizontalGlue());
				
				areaPanel.add(dormPanel);
			}
			
			areaPanel.add(Box.createVerticalGlue());
			locations.add(areaPanel);
		}
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(locations, BorderLayout.SOUTH);
		
		stateChanged(null);
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
		for(JCheckBox checkbox : _checkBoxes) {
			checkbox.setEnabled(true);
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
		} else { // no house selected
			for(JCheckBox checkbox : _checkBoxes) { // disable all checkboxes
				checkbox.setEnabled(false);
			}
		}
		
		_changingState = false;
	}
}
