import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

public class HelpWindow extends JFrame {
	private List<ImageIcon> _screens;
	
	public HelpWindow() {
		super("Help - Domus");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		// look and feel
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) { e.printStackTrace(); }
				
		// tabs
		_screens = new LinkedList<ImageIcon>();
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Preferences", newImageTab(Constants.HELP_SCREEN_PREFERENCE_FILE));
		tabbedPane.addTab("Results", newImageTab(Constants.HELP_SCREEN_RESULTS_FILE));
		tabbedPane.addTab("Cart", newImageTab(Constants.HELP_SCREEN_CART_FILE));
		
		this.add(tabbedPane);
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				// resizing the components, as below, would be nice, but it is too slow
//				for(ImageIcon screen : _screens) {
//					Image scaledImage = screen.getImage().getScaledInstance(-1, HelpWindow.this.getHeight(), Image.SCALE_SMOOTH);
//					screen.setImage(scaledImage);
//				}
			}
		});
		
		this.pack();
		this.setVisible(true);
	}
	
	private JPanel newImageTab(String imageFileName) {
		// get image icon
		ImageIcon screen = new ImageIcon(imageFileName);
		_screens.add(screen);
		
		// scale image
		Image scaledImage = screen.getImage().getScaledInstance(-1, Constants.HELP_SCREEN_HEIGHT, Image.SCALE_SMOOTH);
		screen.setImage(scaledImage);
		
		// add image icon to tab
		JPanel tab = new JPanel();
		tab.add(new JLabel(screen));
		
		// set the tab's size
		tab.setPreferredSize(new Dimension(screen.getIconWidth(), screen.getIconHeight()));
		
		return tab;
	}
}
