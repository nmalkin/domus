import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import org.dom4j.DocumentException;


public class MainWindow extends JFrame {
	protected MainWindow() {
		super("Domus");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// look and feel
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) { e.printStackTrace(); }
		
		// menu bar
		JMenuItem loadMenuItem = new JMenuItem("Open");
		loadMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					DomusXML.readXML("test.xml");
					Canvas.getInstance().redrawFromState();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DocumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					DomusXML.writeXML("test.xml");
				} catch(java.io.IOException err) {
					System.err.println("an error occurred writing to output");
					//TODO: display a message
				}
			}
		});
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(loadMenuItem);
		fileMenu.add(saveMenuItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		
		this.setJMenuBar(menuBar);
		
		// tabs
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Preferences", new PreferencesTab());
		tabbedPane.addTab("Results", new ResultsTab());
		tabbedPane.addTab("Cart", ListsTab.getInstance());
//		tabbedPane.setEnabledAt(1, false);
//		tabbedPane.setEnabledAt(2, false);
		
		this.add(tabbedPane);
		
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * Displays a pop-up window with the given message and an appropriate icon.
	 * 
	 * @param message the message to be displayed
	 * @param messageType one of the message types accepted by JOptionPane; see http://download.oracle.com/javase/6/docs/api/javax/swing/JOptionPane.html
	 */
	private void showMessage(String message, int messageType) {
		JOptionPane.showMessageDialog(this, message, "Domus", messageType);
	}
	
	/**
	 * Displays a pop-up window with the given message
	 * @param message
	 */
	private void showMessage(String message) {
		showMessage(message, JOptionPane.PLAIN_MESSAGE);
	}
	
	
}
