import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The LotteryNumberPanel allows the user to input their lottery number,
 * either directly (through the slider) or, indirectly, 
 * by having the program estimate it based on their semester level and optimism.
 * 
 * @author nmalkin, jswarren, mmschnei
 *
 */
public class LotteryNumberPanel extends JPanel implements ChangeListener, ActionListener, AncestorListener {

	private static ImageIcon _happyImage = new ImageIcon(Constants.HAPPY_FILE, "yay!");
	private static ImageIcon _okayImage = new ImageIcon(Constants.OKAY_FILE, "eh");
	private static ImageIcon _sadImage = new ImageIcon(Constants.SAD_FILE, "booo");
	private static ImageIcon _sadImageDim = new ImageIcon(Constants.SAD_FILE_DIM, "booo");
	private static ImageIcon _happyImageDim = new ImageIcon(Constants.HAPPY_FILE_DIM, "yay!");
	private static ImageIcon _okayImageDim = new ImageIcon(Constants.OKAY_FILE_DIM, "eh");

	private JLabel _happyButton;
	private JLabel _okayButton;
	private JLabel _sadButton;

	/** the slider displaying the lottery number */
	private JSlider _numberSlider;

	/** the combo box displaying the semester level */
	private JComboBox _semesterLevelBox;
	
	/** the currently selected optimism level */
	private int _selectedOptimism;


	public LotteryNumberPanel() {
		// set panel size
		Dimension size = new Dimension(Constants.LOTTERY_PANEL_WIDTH,Constants.LOTTERY_PANEL_HEIGHT);
		this.setPreferredSize(size);
		this.setSize(size);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new MatteBorder(0,1,0,0,Color.GRAY));
		
		this.setBackground(Constants.SIDEBAR_COLOR);
		
		// slider
		_numberSlider = new JSlider(JSlider.VERTICAL, 1, Database.getMaxLotteryNumber(), Constants.DEFAULT_LOTTERY_NUMBER);
		_numberSlider.addChangeListener(this);
		_numberSlider.setMajorTickSpacing(50);
		_numberSlider.setPaintTicks(true);
		_numberSlider.setPaintLabels(true);
		_numberSlider.setAlignmentX(CENTER_ALIGNMENT);
		_numberSlider.setBackground(Constants.SIDEBAR_COLOR);

		// semester level
		Integer[] semesterLevels = {3, 4, 5, 6, 7, 8, 9, 10}; 
		//TODO: rather than ints, have these be objects, so we can include a string description (e.g., "sophomore") as well

		_semesterLevelBox = new JComboBox(semesterLevels);
		_semesterLevelBox.addActionListener(this);
		//semesterLevel.setPreferredSize(new Dimension(100,25));
		_semesterLevelBox.setMaximumSize(new Dimension(100,25));
		_semesterLevelBox.setEditable(false);
		_semesterLevelBox.setAlignmentX(CENTER_ALIGNMENT);
		_semesterLevelBox.setBackground(Constants.SIDEBAR_COLOR);

		// set semester level for current number
		int semesterLevel = Database.semesterFromLotteryNumber(Constants.DEFAULT_LOTTERY_NUMBER);
		int semesterIndex = semesterLevel - 3;
		_semesterLevelBox.setSelectedIndex(semesterIndex);

		this.add(Box.createRigidArea(new Dimension(0,10)));
		JLabel lotteryNumTitle = new JLabel("LOTTERY NUMBER");
		lotteryNumTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD,12));
		lotteryNumTitle.setAlignmentX(CENTER_ALIGNMENT);
		this.add(lotteryNumTitle);
		
		this.add(_numberSlider);

		this.add(Box.createRigidArea(new Dimension(0,10)));
		
		JLabel semesterLevelTitle = new JLabel("SEMESTER LEVEL");
		semesterLevelTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD,12));
		semesterLevelTitle.setAlignmentX(CENTER_ALIGNMENT);
		this.add(semesterLevelTitle);
		
		this.add(_semesterLevelBox);

		this.add(Box.createRigidArea(new Dimension(0,10)));

		JLabel optimismLevelTitle = new JLabel("LEVEL OF OPTIMISM");
		optimismLevelTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD,12));
		optimismLevelTitle.setAlignmentX(CENTER_ALIGNMENT);
		this.add(optimismLevelTitle);

		MouseAdapter optimismButtonListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JComponent source = (JComponent) e.getSource();
				source.requestFocus();
				int optimism = -1;
				
				if(source == _happyButton) {
					optimism = Constants.OPTIMISM_HIGH;
				} else if(source == _okayButton) {
					optimism = Constants.OPTIMISM_MEDIUM;
				} else if(source == _sadButton) {
					optimism = Constants.OPTIMISM_LOW;
				}
				
				selectOptimism(optimism);
				_selectedOptimism = optimism;
				updateSliderFromSemester();
			}
		};
		
		_happyButton = new JLabel();
		_happyButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		_happyButton.setIcon(_happyImageDim);
		_happyButton.addMouseListener(optimismButtonListener);

		_okayButton = new JLabel();
		_okayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		_okayButton.setIcon(_okayImageDim);
		_okayButton.addMouseListener(optimismButtonListener);

		_sadButton = new JLabel();
		_sadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		_sadButton.setIcon(_sadImageDim);
		_sadButton.addMouseListener(optimismButtonListener);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
		buttonPanel.setBackground(Constants.SIDEBAR_COLOR);
		
		buttonPanel.add(_happyButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(_okayButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(_sadButton);

		this.add(buttonPanel);

		this.add(Box.createRigidArea(new Dimension(0,10)));
		this.addAncestorListener(this);	
	}

	public void updateSliderFromSemester() {
		int lotteryNumber = Database.predictLotteryNumber(_semesterLevelBox.getSelectedIndex() + 3, _selectedOptimism);

		// update the State with the current lottery number
		State.getInstance().getGroup().setLotteryNumber(lotteryNumber);
		
		// update the slider with the number
		_numberSlider.setValue(lotteryNumber);
	}
	
	/**
	 * Listens for changes in the slider and updates the state accordingly.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() != _numberSlider) throw new IllegalArgumentException("expecting event from slider");

		if(_numberSlider.hasFocus()) {
			// get the lottery number from the slider
			int lotteryNumber = _numberSlider.getValue();
	
			// update the State with the current lottery number
			State.getInstance().getGroup().setLotteryNumber(lotteryNumber);
	
			// get the semester level for the currently selected number
			int semesterLevel = Database.semesterFromLotteryNumber(lotteryNumber);
			int semesterIndex = semesterLevel - 3; // the first semester displayed in the combobox is 3
			//TODO: have a better system; probably affected by whether the semesterLevel stays an int 
			//      (see todo above)
	
			// update the combobox with it
			_semesterLevelBox.setSelectedIndex(semesterIndex);
			
			// get happiness level and update state and the display with it
			int happiness = Database.optimismFromLotteryNumber(lotteryNumber);
			_selectedOptimism = happiness;
			selectOptimism(happiness);
		}
	}
	
	/**
	 * Given an optimism level, selects the appropriate button
	 * (and deselects the others).
	 * 
	 * @param optimism the optimism level
	 * @see Constants for allowed optimism levels
	 */
	protected void selectOptimism(int optimism) {
		_happyButton.setIcon(_happyImageDim);
		_okayButton.setIcon(_okayImageDim);
		_sadButton.setIcon(_sadImageDim);
		
		switch(optimism) {
			case Constants.OPTIMISM_HIGH:
				_happyButton.setIcon(_happyImage);
				break;
			case Constants.OPTIMISM_MEDIUM:
				_okayButton.setIcon(_okayImage);
				break;
			case Constants.OPTIMISM_LOW:
				_sadButton.setIcon(_sadImage);
				break;
		}
	}

	/**
	 * Responds to changes in the semester level JComboBox.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() != _semesterLevelBox) throw new IllegalArgumentException("expecting event from combobox");

		if(_semesterLevelBox.hasFocus()) {
			// get the semester level from the box
			int semester = (Integer) _semesterLevelBox.getSelectedItem();

			// get an estimated lottery number from the database
			int lotteryNumber = Database.predictLotteryNumber(semester, _selectedOptimism);

			// update the state with the number
			State.getInstance().getGroup().setLotteryNumber(lotteryNumber);

			// update the slider with the number
			_numberSlider.setValue(lotteryNumber);
		}
	}

	/**
	 * Called when the ancestor (Preferences or Results tab) becomes visible.
	 * Ensures that the values on both of the sliders will always be the same.
	 */
	@Override
	public void ancestorAdded(AncestorEvent e) {
		// get the group's lottery number
		int lotteryNumber = State.getInstance().getGroup().getLotteryNumber();

		_numberSlider.setValue(lotteryNumber);
	}

	/**
	 * Nothing needs to happen when an ancestor is moved or removed.
	 */
	@Override
	public void ancestorMoved(AncestorEvent e) { }

	@Override
	public void ancestorRemoved(AncestorEvent e) { }
}
