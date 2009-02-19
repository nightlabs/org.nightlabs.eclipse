package org.nightlabs.eclipse.ui.control.export.copy;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class WidgetCopyUtil
{
	public static Table createCopy(Composite composite, Table oldTable) {
		Table newTable = new Table(composite, oldTable.getStyle());

		TableColumn[] tableColumns = oldTable.getColumns();
		for (TableColumn tableColumn : tableColumns) {
			TableColumn tc = new TableColumn(newTable, tableColumn.getStyle());
			tc.setText(tableColumn.getText());
			tc.pack();
		}

		TableItem[] tableItems = oldTable.getItems();
		for (TableItem tableItem : tableItems) {
			TableItem ti = new TableItem(newTable, tableItem.getStyle());
			for (int i = 0; i < tableColumns.length; i++)
				if (tableColumns[i] != null)
					ti.setText(i, tableItem.getText(i));
		}

		newTable.setHeaderVisible(true);
		return newTable;
	}
}