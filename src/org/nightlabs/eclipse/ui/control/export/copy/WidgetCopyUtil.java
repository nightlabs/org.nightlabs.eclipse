package org.nightlabs.eclipse.ui.control.export.copy;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

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

	public static Tree createCopy(Composite composite, Tree oldTree) {
		Tree newTree = new Tree(composite, oldTree.getStyle());

		TreeItem[] treeItems = oldTree.getItems();
		for (TreeItem treeItem : treeItems) {
			TreeItem newTreeItem = new TreeItem(newTree, treeItem.getStyle());
			newTreeItem.setText(treeItem.getText());
			generateChildItems(newTreeItem, treeItem);
		}
		return newTree;
	}

	private static void generateChildItems(TreeItem newParentItem, TreeItem treeItem) {
		TreeItem newTreeItem = new TreeItem(newParentItem, treeItem.getStyle());
		for (TreeItem ti : treeItem.getItems()) {
			newTreeItem.setText(ti.getText());
			generateChildItems(newTreeItem, ti);
		}
	}
}