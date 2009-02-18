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
			new TableColumn(newTable, tableColumn.getStyle()).setText(tableColumn.getText());
		}

		TableItem[] tableItems = oldTable.getItems();
		for (TableItem tableItem : tableItems) {
			new TableItem(newTable, tableItem.getStyle()).setText(new String[]{"","","","","","","","","" });
		}

		return newTable;
	}
}