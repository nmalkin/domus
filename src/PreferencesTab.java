import java.awt.BorderLayout;

import javax.swing.JPanel;


public class PreferencesTab extends JPanel {
	public PreferencesTab() {
		this.setLayout(new BorderLayout());
		this.add(new JPanel(), BorderLayout.CENTER); // canvas
		this.add(new LotteryNumberPanel(), BorderLayout.EAST);
	}
}
