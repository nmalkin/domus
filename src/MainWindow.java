import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.TransformerException;

import org.dom4j.DocumentException;

public class MainWindow extends JFrame {
	
	private JCheckBoxMenuItem sophomoreOnlyMenuItem;
	protected MainWindow() {
		super("Domus");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// look and feel
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) { e.printStackTrace(); }
		
		// state file chooser
		final JFileChooser stateFileChooser = new JFileChooser();
		stateFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		// html export file chooser
		final JFileChooser exportFileChooser = new JFileChooser();
		exportFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter filter = new FileNameExtensionFilter("HTML file", "htm", "html");
		exportFileChooser.addChoosableFileFilter(filter);
		
		// menu bar
		
		// load state button
		JMenuItem loadMenuItem = new JMenuItem("Open");
		loadMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int returnVal = stateFileChooser.showOpenDialog(MainWindow.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = stateFileChooser.getSelectedFile();
						
						if(! file.exists()) {
							JOptionPane.showMessageDialog(MainWindow.this, 
									"Domus can't open this file: it doesn't exist!", 
									"Domus", 
									JOptionPane.WARNING_MESSAGE);
						} else {
							DomusXML.readXML(file);
							Canvas.getInstance().redrawFromState();
							ListsTab.getInstance().updateLists();
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DocumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		// save state button
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int returnVal = stateFileChooser.showSaveDialog(MainWindow.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = stateFileChooser.getSelectedFile();
						
						if(file.exists()) {
							int option = JOptionPane.showConfirmDialog(MainWindow.this, 
									"This file already exists. Are you sure you want to overwrite it?", 
									"Domus", 
									JOptionPane.OK_CANCEL_OPTION);
							
							if(option == JOptionPane.OK_OPTION) {
								DomusXML.writeXML(file);
							}
						} else {
							DomusXML.writeXML(file);
						}
					}
					
					
				} catch(java.io.IOException err) {
					System.err.println("an error occurred writing to output");
					//TODO: display a message
				}
			}
		});
		
		// export button
		JMenuItem exportMenuItem = new JMenuItem("Export");
		exportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int returnVal = exportFileChooser.showSaveDialog(MainWindow.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = exportFileChooser.getSelectedFile();
						
						if(file.exists()) {
							int option = JOptionPane.showConfirmDialog(MainWindow.this, 
									"This file already exists. Are you sure you want to overwrite it?", 
									"Domus", 
									JOptionPane.OK_CANCEL_OPTION);
							
							if(option == JOptionPane.OK_OPTION) {
								DomusXML.writeHTML(file);
							}
						} else {
							DomusXML.writeHTML(file);
						}
					}
				
				} catch(java.io.IOException err) {
					System.err.println("an error occurred writing to output");
					err.printStackTrace();
					//TODO: display a message
				} catch (TransformerException err) {
					System.err.println("an error occurred writing to output");
					err.printStackTrace();
					//TODO: display a message
				}
			}
		});
		
		// exit
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(loadMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(exportMenuItem);
		fileMenu.add(exitMenuItem);
		
		JMenu optionsMenu = new JMenu("Options");
		
		JMenuItem yearsMenuItem = new JMenuItem("Choose years");
		yearsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new YearWindow();
			}
		});
		
		sophomoreOnlyMenuItem = new JCheckBoxMenuItem("Sophomore-Only Eligible");
		sophomoreOnlyMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(sophomoreOnlyMenuItem.isSelected()) State.getInstance().setSophomoreStatus(true);
				else State.getInstance().setSophomoreStatus(false);
			}
		});
		
		optionsMenu.add(yearsMenuItem);
		optionsMenu.add(sophomoreOnlyMenuItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(optionsMenu);
		
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
