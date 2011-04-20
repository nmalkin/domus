import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


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
				//TODO
				showMessage("load state (TODO)");
			}
		});
		
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO
				showMessage("save state (TODO)");
			}
		});
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(loadMenuItem);
		fileMenu.add(saveMenuItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		
		this.setJMenuBar(menuBar);
		
		
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
