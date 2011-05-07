import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

	private JLabel _happyButton;
	private JLabel _okayButton;
	private JLabel _sadButton;

	/** the slider displaying the lottery number */
	private JSlider _numberSlider;

	/** the combo box displaying the semester level */
	private JComboBox _semesterLevelBox;


	public LotteryNumberPanel() {
		// slider
		_numberSlider = new JSlider(JSlider.VERTICAL, 1, Database.getMaxLotteryNumber(), Constants.DEFAULT_LOTTERY_NUMBER);
		_numberSlider.addChangeListener(this);
		_numberSlider.setMajorTickSpacing(50);
		_numberSlider.setPaintTicks(true);
		_numberSlider.setPaintLabels(true);

		// semester level
		Integer[] semesterLevels = {3, 4, 5, 6, 7, 8, 9, 10}; 
		//TODO: rather than ints, have these be objects, so we can include a string description (e.g., "sophomore") as well

		_semesterLevelBox = new JComboBox(semesterLevels);
		_semesterLevelBox.addActionListener(this);
		//semesterLevel.setPreferredSize(new Dimension(100,25));
		_semesterLevelBox.setMaximumSize(new Dimension(100,25));
		_semesterLevelBox.setEditable(false);

		// set semester level for current number
		int semesterLevel = Database.semesterFromLotteryNumber(Constants.DEFAULT_LOTTERY_NUMBER);
		int semesterIndex = semesterLevel - 3;
		_semesterLevelBox.setSelectedIndex(semesterIndex);

		// set panel size and add elements to it
		Dimension size = new Dimension(Constants.LOTTERY_PANEL_WIDTH,Constants.LOTTERY_PANEL_HEIGHT);
		this.setPreferredSize(size);
		this.setSize(size);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new MatteBorder(0,1,0,0,Color.GRAY));

		this.add(new JLabel("Lottery number"));
		this.add(_numberSlider);

		this.add(Box.createRigidArea(new Dimension(0,10)));

		//TODO: make sure all the elements actually align
		//numberSlider.setAlignmentX(CENTER_ALIGNMENT);

		this.add(new JLabel("Semester level:"));
		this.add(_semesterLevelBox);

		this.add(Box.createRigidArea(new Dimension(0,10)));

		this.add(new JLabel("Level of optimism"));

		_happyButton = new JLabel(_happyImage);
		_happyButton.addMouseListener(new HappyListener());

		_okayButton = new JLabel(_okayImage);
		_okayButton.addMouseListener(new OkayListener());

		_sadButton = new JLabel(_sadImage);
		_sadButton.addMouseListener(new SadListener());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		buttonPanel.add(Box.createRigidArea(new Dimension((this.getWidth() - 3 * Constants.OPTIMISM_BUTTON_WIDTH - 30) / 2, 0)));
		buttonPanel.add(_happyButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(_okayButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(_sadButton);

		this.add(buttonPanel);

		this.add(Box.createRigidArea(new Dimension(0,10)));
		this.addAncestorListener(this);

		_numberSlider.setValue(Database.lotteryNumberFromSemester(semesterIndex) / 2);
	}
	
	public class HappyListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			State.getInstance().setOptimism(Constants.OPTIMISTIC);
			updateSliderFromSemester();
		}
	}

	public class OkayListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			State.getInstance().setOptimism(Constants.AVERAGE);
			updateSliderFromSemester();
		}
	}

	public class SadListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			State.getInstance().setOptimism(Constants.PESSIMISTIC);
			updateSliderFromSemester();
		}
	}

	public void updateSliderFromSemester() {
		int lotteryNumber = Database.lotteryNumberFromSemester(_semesterLevelBox.getSelectedIndex() + 3);

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

		int happiness = Database.optimismFromLotteryNumber(lotteryNumber);

		if(happiness == Constants.OPTIMISTIC) {
			_happyButton.setBorder(new LineBorder(Color.GRAY));
			_okayButton.setBorder(null);
			_sadButton.setBorder(null);
		}
		else if(happiness == Constants.AVERAGE) {
			_happyButton.setBorder(null);
			_okayButton.setBorder(new LineBorder(Color.GRAY));
			_sadButton.setBorder(null);
		}
		else {
			_happyButton.setBorder(null);
			_okayButton.setBorder(null);
			_sadButton.setBorder(new LineBorder(Color.GRAY));
		}

		State.getInstance().setOptimism(happiness);
		_semesterLevelBox.setSelectedIndex(semesterIndex);
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
			int lotteryNumber = Database.lotteryNumberFromSemester(semester);

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
