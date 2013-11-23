package org.nightlabs.base.ui.table.filter;

/**
 * This interface is responsible for the visual editing of the filter criteria of an {@link ITableColumnFilter}.
 *   
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface ITableColumnFilterActionHandler 
{
	/**
	 * Determines whether the filter has been edited or not.
	 * 
	 * @return true if filter criteria has been edited or false if not.
	 */
	public boolean editFilter();
	
	/**
	 * Returns the {@link ITableColumnFilter} this action handler is editing..
	 * If {@link #editFilter()} has been called before and returned true, this method
	 * should return the edited instance.
	 * 
	 * @return the {@link ITableColumnFilter} this action handler is editing.
	 */
	public ITableColumnFilter getTableColumnFilter();
}
