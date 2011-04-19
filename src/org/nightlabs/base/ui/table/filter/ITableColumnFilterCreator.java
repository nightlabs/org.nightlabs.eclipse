package org.nightlabs.base.ui.table.filter;

/**
 * Interface for creating an implementation of {@link ITableColumnFilter}.
 * 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface ITableColumnFilterCreator 
{
	/**
	 * Returns an implementation of {@link ITableColumnFilter} for the given column index.
	 * 
	 * @param columnIndex the columnIndex of the column for which a corresponding {@link ITableColumnFilter} 
	 * should be returned.
	 * @return an {@link ITableColumnFilter} for the given column index.
	 */
	public ITableColumnFilter createTableColumnFilter(int columnIndex);
}
