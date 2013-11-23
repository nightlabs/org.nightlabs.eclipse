package org.nightlabs.base.ui.table.filter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Default implementation of {@link ITableColumnFilterCreator} which obtains all the possible
 * values for the {@link TableColumnFilter} on basis of the text values of all {@link TableItem}
 * of an {@link TableColumn} of a specific column index. 
 * 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableColumnFilterCreator implements ITableColumnFilterCreator 
{
	private Table table;
	private ITableLabelProvider labelProvider;
	
	public TableColumnFilterCreator(Table table, ITableLabelProvider labelProvider) 
	{
		this.table = table;
		this.labelProvider = labelProvider;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.filter.ITableColumnFilterCreator#createTableColumnFilter(int)
	 */
	@Override
	public ITableColumnFilter createTableColumnFilter(int columnIndex) 
	{
		Set<String> possibleValues = getPossibleValues(table, columnIndex);
		ITableColumnFilter filter = new TableColumnFilter(columnIndex, labelProvider, possibleValues);
		return filter;
	}

	public static Set<String> getPossibleValues(Table table, int columnIndex) {
		Set<String> possibleValues = new HashSet<String>();
		for (TableItem item : table.getItems()) {
			String itemText = item.getText(columnIndex);
			if (itemText != null)
				possibleValues.add(itemText);
		}
		return possibleValues;
	}

}
