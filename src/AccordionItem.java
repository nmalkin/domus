import java.awt.Dimension;

public interface AccordionItem extends Comparable<AccordionItem> {

	/**
	 * Returns the index of the AccordionItem in its parent list.
	 */
	public double getComparisonValue();
	
	/**
	 * Returns the state of the AccordionItem.
	 */
	public boolean isOpen();
	
	/**
	 * Sets the index of the AccordionItem in its parent list.
	 */
	public void setComparisonValue(double d);
	
	/**
	 * Sets the state of the AccordionItem.
	 */
	public void setOpen(boolean open);
	
	/**
	 * Adds an AccordionItem to this AccordionItem's list.
	 */
	public void addItem(AccordionItem item);
	
	/**
	 * Removes an AccordionItem from this AccordionItem's list.
	 */
	public void removeItem(AccordionItem item);
	
	/**
	 * Resizes the item.
	 */
	public void resizeItem(Dimension d);

}