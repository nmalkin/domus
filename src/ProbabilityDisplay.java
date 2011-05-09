import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;


public class ProbabilityDisplay extends JPanel {
	/** the probability to be displayed, a value between 0 and 1 */
	private double _probability;
	
	public ProbabilityDisplay(double probability) {
		_probability = probability;
		this.setToolTipText(getProbabilityString());
		
		Dimension dimension = new Dimension(Constants.PROBABILITY_DISPLAY_WIDTH, Constants.PROBABILITY_DISPLAY_HEIGHT);
		this.setMaximumSize(dimension);
		this.setPreferredSize(dimension);
		this.setSize(dimension);
	}
	
	/**
	 * Returns a string representation of the probability,
	 * as a value between 0 and 100, rounded to the nearest percentage,
	 * and with a % sign at the end.
	 * 
	 * @return String representation of probability
	 */
	private String getProbabilityString() {
		return (int) (_probability * 100) + "%";
	}
	
	/**
	 * Sets the probability. A value between 0.0 and 1.0
	 * 
	 * @param probability, probability for this display
	 */
	public void setProbability(double probability) {
		_probability = probability;
		this.setToolTipText(getProbabilityString());
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		// draw background rectangle
		g2.setPaint(Constants.PROBABILITY_DISPLAY_BACKGROUND_COLOR);
		g2.fill(new Rectangle(0, 0, Constants.PROBABILITY_DISPLAY_WIDTH, Constants.PROBABILITY_DISPLAY_HEIGHT));
		
		// how far should we fill?
		int fillWidth = (int) (Constants.PROBABILITY_DISPLAY_WIDTH * _probability);
		
		// which category does this fit in?
		int category = (int) (_probability / (1.0 / Constants.PROBABILIY_DISPLAY_CATEGORIES));
		
		// special case: if probability is 1, 
		// the above formula would've assigned it to a non-existent (n+1-nth) category.
		// correct for this.
		if(category >= Constants.PROBABILITY_DISPLAY_COLORS.length) {
			category = Constants.PROBABILITY_DISPLAY_COLORS.length - 1; // assign it to the last category
		}
		
		// draw fill rectangle
		g2.setPaint(Constants.PROBABILITY_DISPLAY_COLORS[category]); // color determined by category
		g2.fill(new Rectangle(0, 0, fillWidth, Constants.PROBABILITY_DISPLAY_HEIGHT));
	}
	
	@Override
	public String toString() {
		return getProbabilityString();
	}
}
