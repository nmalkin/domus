import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The LotteryNumberPanel allows the user to input their lottery number,
 * either directly (through the slider) or, indirectly, 
 * by having the program estimate it based on their semester level and optimism.
 * 
 * @author nmalkin, jswarren
 *
 */
public class LotteryNumberPanel extends JPanel implements ChangeListener, ActionListener, AncestorListener {
	
	
	
	/** the slider displaying the lottery number */
	private JSlider _numberSlider;
	
	/** the combo box displaying the semester level */
	private JComboBox _semesterLevelBox;
		
	
	public LotteryNumberPanel() {
		// slider
		_numberSlider = new JSlider(JSlider.VERTICAL, 1, Constants.MAX_LOTTERY_NUMBER, Constants.DEFAULT_LOTTERY_NUMBER);
		_numberSlider.addChangeListener(this);
		_numberSlider.setMajorTickSpacing(50);
		_numberSlider.setPaintTicks(true);
		_numberSlider.setPaintLabels(true);
		
		// semester level
		Integer[] semesterLevels = {3, 4, 5, 6, 7, 8, 9}; 
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
		Dimension size = new Dimension(200,500);
		this.setPreferredSize(size);
		this.setSize(size);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.add(new JLabel("Lottery number:"));
		this.add(_numberSlider);
		
		//TODO: make sure all the elements actually align
		//numberSlider.setAlignmentX(CENTER_ALIGNMENT);
		
		this.add(new JLabel("Estimated semester level:"));
		this.add(_semesterLevelBox);
		
		this.add(new JLabel("Estimated happiness level:"));
		JComponent happiness = new JLabel("Happy");
		happiness.setToolTipText("Brown students are always happy.");
		happiness.setBorder(BorderFactory.createLineBorder(Color.black));
		this.add(happiness);
		this.addAncestorListener(this);
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
			System.out.println(semester);
			
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
