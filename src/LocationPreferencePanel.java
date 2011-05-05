import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
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
		
		this.setLayout(new BorderLayout());
		
		JTextPane title = new JTextPane();
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		title.setText("LOCATION PREFERENCES");
		
		ParentCheckBox masterBox = new ParentCheckBox("I'm willing to live anywhere!");
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(title, BorderLayout.WEST);
		topPanel.add(masterBox, BorderLayout.EAST);
		
		JPanel locations = new JPanel();
		locations.setLayout(new BoxLayout(locations, BoxLayout.X_AXIS));
		
		for(CampusArea area : Database.getCampusAreas()) {
			JPanel areaPanel = new JPanel();
			areaPanel.setLayout(new BoxLayout(areaPanel, BoxLayout.Y_AXIS));
			areaPanel.setPreferredSize(new Dimension(200,260));
			areaPanel.setMinimumSize(new Dimension(200,260));
			areaPanel.setBorder(new LineBorder(Color.GRAY));
			
			JPanel areaNamePanel = new JPanel();
			areaNamePanel.setLayout(new BoxLayout(areaNamePanel, BoxLayout.X_AXIS));
			MatteBorder line = new MatteBorder(0, 0, 1, 0, Color.GRAY);
			
			areaNamePanel.setBorder(line);
			areaNamePanel.setPreferredSize(new Dimension(200,25));
			areaNamePanel.setMinimumSize(new Dimension(200,25));
			
			ParentCheckBox areaBox = new ParentCheckBox(area.getName());
			areaBox.setSelected(false);
			
			areaNamePanel.add(areaBox);
			areaNamePanel.add(Box.createHorizontalGlue());
			areaPanel.add(areaNamePanel);
			
			for(Dorm dorm : area) {
				JPanel dormPanel = new JPanel();
				dormPanel.setLayout(new BoxLayout(dormPanel, BoxLayout.X_AXIS));
				dormPanel.setPreferredSize(new Dimension(200, 25));
				dormPanel.setMinimumSize(new Dimension(200,25));
				
				DormCheckBox dormBox = new DormCheckBox(dorm);
				
				dormBox.setSelected(false);
				dormBox.addItemListener(myListener);
				
				dormBox.addParent(areaBox);
				dormBox.addParent(masterBox);
				areaBox.addChild(dormBox);
				masterBox.addChild(dormBox);
				
				_dormBoxes.put(dorm, dormBox);
				
				dormPanel.add(Box.createRigidArea(new Dimension(15,0)));
				dormPanel.add(dormBox);
				dormPanel.add(Box.createHorizontalGlue());
				
				areaPanel.add(dormPanel);
			}
			
			areaPanel.add(Box.createVerticalGlue());
			locations.add(areaPanel);
		}
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(locations, BorderLayout.SOUTH);
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
