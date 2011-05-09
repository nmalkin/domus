import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;


public class PreferencePanel extends JPanel {

	private AccordionList<PreferencePanelTab, PreferencePanelItem> preferenceList;

	private final int _listWidth = Constants.PREFERENCE_PANEL_WIDTH;
	private final int _listHeight = Constants.PREFERENCE_PANEL_HEIGHT;

	protected PreferencePanel() {
		Dimension size = new Dimension(Constants.PREFERENCE_PANEL_WIDTH,0);
		this.setPreferredSize(size);
		this.setSize(size);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new MatteBorder(0,0,0,1,Color.GRAY));

		/*JLabel instructions = new JLabel(Constants.PREFERENCE_PANEL_INSTRUCTIONS);
		instructions.setMinimumSize(new Dimension(Constants.PREFERENCE_PANEL_WIDTH,0));
		this.add(instructions);*/
		
		preferenceList = AccordionList.create(_listWidth, _listHeight, 0);

		for(House h: State.getInstance().getGroup()) {
			PreferencePanelTab tab = new PreferencePanelTab(h, preferenceList);
			preferenceList.addTab(tab);

			for(Dorm d: h.getLocationPreference()){
				PreferencePanelItem dorm = new PreferencePanelItem(h, d, preferenceList);

				preferenceList.addListItem(tab, dorm);
			}
		}

		this.add(preferenceList);
	}
}
