

public interface AccordionItem extends Comparable<AccordionItem> {

	/**
	 * Returns the index of the AccordionItem in its parent list.
	 */
	public int getIndex();
	
	/**
	 * Returns the state of the AccordionItem.
	 */
	public boolean isOpen();
	
	/**
	 * Sets the index of the AccordionItem in its parent list.
	 */
	public void setIndex(int index);
	
	/**
	 * Sets the state of the AccordionItem.
	 */
	public void setOpen(boolean open);
	
	/**
	 * Adds an AccordionItem to this AccordionItem's list.
	 */
	public void addItem(AccordionItem item);

}