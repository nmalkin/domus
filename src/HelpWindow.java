import java.awt.Dimension;

import javax.swing.*;

public class HelpWindow extends JFrame {
	public HelpWindow() {
		super("Help - Domus");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		// look and feel
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) { e.printStackTrace(); }
				
		// tabs
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Preferences", newImageTab("help/preferences_screen.png"));
		tabbedPane.addTab("Results", newImageTab("help/results_screen.png"));
		tabbedPane.addTab("Cart", newImageTab("help/cart_screen.png"));
		
		this.add(tabbedPane);
		
		this.pack();
		this.setVisible(true);
	}
	
	private static JPanel newImageTab(String imageFileName) {
		JPanel tab = new JPanel();
		ImageIcon screen = new ImageIcon(imageFileName);
		tab.add(new JLabel(screen));
		tab.setPreferredSize(new Dimension(screen.getIconWidth(), screen.getIconHeight()));
		return tab;
	}
}
