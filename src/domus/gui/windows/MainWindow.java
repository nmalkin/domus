package domus.gui.windows;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.TransformerException;

import org.dom4j.DocumentException;

import domus.Constants;
import domus.State;
import domus.gui.canvas.Canvas;
import domus.gui.tabs.ListsTab;
import domus.gui.tabs.PreferencesTab;
import domus.gui.tabs.ResultsTab;
import domus.xml.DomusXML;

public class MainWindow extends JFrame {
    JTabbedPane tabbedPane;

    public static void openMainWindow() {
    	MainWindow main = null;
    	
    	Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				showMessage(null, "Oh no! An error occurred: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
    	});
    	
    	try {
    		main = new MainWindow();
    	} catch(Exception e) {
    		main.showMessage(main, "Oh no! An error occurred: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    private MainWindow() throws Exception {
        super("Domus");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // look and feel
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // state file chooser
        final JFileChooser stateFileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Domus XML file", "xml");
        stateFileChooser.setFileFilter(filter);
        stateFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // html export file chooser
        final JFileChooser exportFileChooser = new JFileChooser();
        exportFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        filter = new FileNameExtensionFilter("HTML file", "html");
        exportFileChooser.setFileFilter(filter);

        // menu bar items
        // sophomore-only checkbox
        final JCheckBoxMenuItem sophomoreOnlyMenuItem = new JCheckBoxMenuItem(
                "Sophomore-Only Eligible");
        sophomoreOnlyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sophomoreOnlyMenuItem.isSelected())
                    State.getInstance().getGroup().setSophomoreStatus(true);
                else
                    State.getInstance().getGroup().setSophomoreStatus(false);
            }
        });

        // load state button
        JMenuItem loadMenuItem = new JMenuItem("Open");
        loadMenuItem.setMnemonic(KeyEvent.VK_O);
        loadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int returnVal = stateFileChooser
                            .showOpenDialog(MainWindow.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = stateFileChooser.getSelectedFile();

                        if (!file.exists()) {
                            JOptionPane
                                    .showMessageDialog(
                                            MainWindow.this,
                                            "Domus can't open this file: it doesn't exist!",
                                            "Domus",
                                            JOptionPane.WARNING_MESSAGE);
                        } else {
                            DomusXML.readXML(file);
                            Canvas.getInstance().redrawFromState(); // update
                                                                    // canvas
                            ListsTab.getInstance().updateLists(); // update cart
                            sophomoreOnlyMenuItem.setSelected(State
                                    .getInstance().getGroup().isSophomore()); // update
                                                                              // menu
                                                                              // sophomore-only
                                                                              // value

                            /*
                             * Now we need to update the slider with the new
                             * lottery number. But how do we get to it? Luckily
                             * for us, the LotteryNumberPanel has an
                             * AncestorListener: when one of its parent
                             * components becomes visible, it will update from
                             * the State. But the problem is that its parent
                             * component is already visible (whatever tab is
                             * currently open), so setting it as visible (or as
                             * selected) won't do anything. So we will
                             * momentarily switch to a different tab, and then
                             * switch back, triggering an ancestor event. This
                             * is a hacky way of doing things, but probably the
                             * best one under the circumstances. TODO: using a
                             * model for LotteryNumberPanel may help avoid this
                             * problem. see
                             * http://download.oracle.com/javase/tutorial
                             * /uiswing/components/model.html
                             */
                            final int NUMBER_OF_TABS = 3;
                            tabbedPane.setSelectedIndex((tabbedPane
                                    .getSelectedIndex() + 1)
                                    % NUMBER_OF_TABS);
                            tabbedPane.setSelectedIndex((tabbedPane
                                    .getSelectedIndex() + 2)
                                    % NUMBER_OF_TABS);
                        }
                    }
                } catch (IOException err) {
                    showMessage("Problem reading from file: " + err.getMessage(), JOptionPane.ERROR_MESSAGE);
                } catch (DocumentException err) {
                    showMessage("Invalid input file: \n" + err.getMessage(), JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // save state button
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File file = getValidSaveFile(stateFileChooser, "xml");

                    if (file != null) {
                        DomusXML.writeXML(file);
                    }
                } catch (IOException err) {
                    showMessage("Problem writing to file: " + err.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // export button
        JMenuItem exportMenuItem = new JMenuItem("Export");
        exportMenuItem.setMnemonic(KeyEvent.VK_E);
        exportMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File file = getValidSaveFile(exportFileChooser, "html");

                    if (file != null) {
                        DomusXML.writeHTML(file);
                    }

                } catch (IOException err) {
                    showMessage("Problem writing to file: " + err.getMessage(), JOptionPane.ERROR_MESSAGE);
                } catch (TransformerException err) {
                	showMessage("Export failed. Unable to process output document: " + err.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // exit
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(exportMenuItem);
        fileMenu.add(exitMenuItem);

        JMenu optionsMenu = new JMenu("Settings");
        optionsMenu.setMnemonic(KeyEvent.VK_S);

        JMenuItem yearsMenuItem = new JMenuItem("Choose probability model");
        yearsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProbabilityMethodWindow();
            }
        });

        optionsMenu.add(yearsMenuItem);
        optionsMenu.add(sophomoreOnlyMenuItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem guideMenuItem = new JMenuItem("Guide");
        guideMenuItem.setMnemonic(KeyEvent.VK_G);
        guideMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HelpWindow();
                /*
                 * TODO: after the help window opens, the JMenu seems to
                 * permanently lose focus.
                 */
            }
        });
        helpMenu.add(guideMenuItem);

        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: better about message
                showMessage("Domus\nVersion " + Constants.VERSION + " (" + Constants.LAST_UPDATED + ")\nNathan Malkin, Sumner Warren, Miya Schneider");
            }
        });
        helpMenu.add(aboutMenuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);

        // tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Preferences", new PreferencesTab());
        tabbedPane.addTab("Results", new ResultsTab());
        tabbedPane.addTab("Cart", ListsTab.getInstance());
        this.add(tabbedPane);

        this.pack();
        this.setVisible(true);
    }
    
    /**
     * Displays a pop-up window with the given message and an appropriate icon.
     * 
     * @param parentComponent the parent component for this message
     * @param message
     *            the message to be displayed
     * @param messageType
     *            one of the message types accepted by JOptionPane; see
     *            http://download
     *            .oracle.com/javase/6/docs/api/javax/swing/JOptionPane.html
     */
    private static void showMessage(java.awt.Component parentComponent, String message, int messageType) {
        JOptionPane.showMessageDialog(parentComponent, message, "Domus", messageType);
    }

    /**
     * Displays a pop-up window with the given message and an appropriate icon.
     * 
     * @param message
     *            the message to be displayed
     * @param messageType
     *            one of the message types accepted by JOptionPane; see
     *            http://download
     *            .oracle.com/javase/6/docs/api/javax/swing/JOptionPane.html
     */
    private void showMessage(String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, "Domus", messageType);
    }

    /**
     * Displays a pop-up window with the given message
     * 
     * @param message
     */
    private void showMessage(String message) {
        showMessage(message, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Prompts the user for a file to save to. If the selected file doesn't have
     * the right extension, appends it automatically. If the target file exists,
     * has the user confirm their intent or try again if necessary.
     * 
     * TODO: accept multiple allowed extensions
     * 
     * @param fc
     *            the file chooser to open the dialog with
     * @param extension
     *            the allowed file extension
     * @return the File that the user wants to save to, or null if the user
     *         doesn't want to save (clicked cancel)
     */
    private File getValidSaveFile(JFileChooser fc, String extension) {
        File saveFile;

        boolean repeat;
        do {
            saveFile = null;
            repeat = false;

            int returnVal = fc.showSaveDialog(MainWindow.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                saveFile = fc.getSelectedFile();

                if (!extension.equals(getExtension(saveFile))) {
                    // append (a) valid file extension to get a new file
                    String newFile = saveFile.getAbsolutePath() + "."
                            + extension;
                    saveFile = new File(newFile);
                }

                if (saveFile.exists()) {
                    int option = JOptionPane
                            .showConfirmDialog(
                                    MainWindow.this,
                                    "This file already exists. Are you sure you want to overwrite it?",
                                    "Domus", JOptionPane.OK_CANCEL_OPTION);

                    if (option == JOptionPane.CANCEL_OPTION) {
                        repeat = true;
                    }
                }
            }
        } while (repeat);

        return saveFile;
    }

    /**
     * Given a file, returns its extension.
     * 
     * Code taken directly from:
     * http://download.oracle.com/javase/tutorial/uiswing
     * /examples/components/FileChooserDemo2Project/src/components/Utils.java
     * 
     * @param f
     *            file whose extension we're looking for
     * @return the file extension, or null if there isn't any
     */
    protected static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
