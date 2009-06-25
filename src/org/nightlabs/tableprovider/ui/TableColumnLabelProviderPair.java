package org.nightlabs.tableprovider.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableColumnLabelProviderPair
{
	private TableColumn tableColumn;
	private ColumnLabelProvider columnLabelProvider;

	/**
	 * @param tableColumn
	 * @param columnLabelProvider
	 */
	public TableColumnLabelProviderPair(TableColumn tableColumn,
			ColumnLabelProvider columnLabelProvider)
	{
		this.tableColumn = tableColumn;
		this.columnLabelProvider = columnLabelProvider;
	}

	/**
	 * Returns the tableColumn.
	 * @return the tableColumn
	 */
	public TableColumn getTableColumn() {
		return tableColumn;
	}
	/**
	 * Sets the tableColumn.
	 * @param tableColumn the tableColumn to set
	 */
	public void setTableColumn(TableColumn tableColumn) {
		this.tableColumn = tableColumn;
	}
	/**
	 * Returns the columnLabelProvider.
	 * @return the columnLabelProvider
	 */
	public ColumnLabelProvider getColumnLabelProvider() {
		return columnLabelProvider;
	}
	/**
	 * Sets the columnLabelProvider.
	 * @param columnLabelProvider the columnLabelProvider to set
	 */
	public void setColumnLabelProvider(ColumnLabelProvider columnLabelProvider) {
		this.columnLabelProvider = columnLabelProvider;
	}

}
