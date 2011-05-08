import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;


public class ResultsPreferencePanel extends JPanel {
	
	protected ResultsPreferencePanel() {
		Dimension size = new Dimension(Constants.LOTTERY_PANEL_WIDTH,Constants.LOTTERY_PANEL_HEIGHT);
		this.setPreferredSize(size);
		this.setSize(size);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new MatteBorder(0,0,0,1,Color.GRAY));
		
	}

}
