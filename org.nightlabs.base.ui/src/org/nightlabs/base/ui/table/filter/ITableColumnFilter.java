package org.nightlabs.base.ui.table.filter;

import org.eclipse.swt.widgets.Composite;

/**
 * Interface for filtering elements of a column.
 *  
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface ITableColumnFilter 
{
	/**
	 * Determines whether the given input element should be filtered or not.
	 * 
	 * @param element the element to check if filtering needs to be done
	 * @return true if the element should be filtered and false if not.
	 */
	public boolean filterElement(Object element);

	/**
	 * Clears the filter, so that the method {@link #filterElement(Object)} will always return false;
	 */
	public void clear();
	
	/**
	 * Return the text which describes the filter criteria set by the filter.
	 * 
	 * @return the filter criteria text.
	 */
	public String getFilterText();
	
	/**
	 * Returns an {@link ITableColumnFilterActionHandler} implementation which is responsible for editing the
	 * filter criteria.
	 * 
	 * @param parent the parent composite.
	 * @return the {@link ITableColumnFilterActionHandler} implementation which is responsible for editing the
	 * filter criteria.
	 */
	public ITableColumnFilterActionHandler createActionHandler(Composite parent);
	
	/**
	 * Determines if filter criteria are set or not.
	 * After the call of {@link #clear()} this method should always return true.
	 * 
	 * @return true if the filter is cleared or false if filter criteria are set/active
	 */
	public boolean isEmpty();
}
