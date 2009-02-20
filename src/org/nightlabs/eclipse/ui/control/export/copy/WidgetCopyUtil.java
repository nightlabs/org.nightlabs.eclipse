package org.nightlabs.eclipse.ui.control.export.copy;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class WidgetCopyUtil
{
	private static final Logger logger = Logger.getLogger(WidgetCopyUtil.class);

	//Table
	public static Table copyTable(Composite composite, Table oldTable) {
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

	//Tree
	public static Tree copyTree(Composite composite, Tree oldTree) {
		Tree newTree = new Tree(composite, oldTree.getStyle());
		TreeColumn[] treeColumns = oldTree.getColumns();
		for (TreeColumn treeColumn : treeColumns) {
			TreeColumn newTreeColumn = new TreeColumn(newTree, treeColumn.getStyle());
			newTreeColumn.setWidth(treeColumn.getWidth());
			newTreeColumn.setText(treeColumn.getText());
			newTreeColumn.setImage(treeColumn.getImage());
		}

		TreeItem[] treeItems = oldTree.getItems();
		for (TreeItem oldTreeItem : treeItems) {
			if (logger.isDebugEnabled())
				logger.debug("copyTree: copying top-level item: " + oldTreeItem.getText() + " (having "+ oldTreeItem.getItemCount() +" children)");

			TreeItem newTreeItem = new TreeItem(newTree, oldTreeItem.getStyle());
			copyTreeProperties(newTreeItem, oldTreeItem);
			generateChildItems(newTreeItem, oldTreeItem);
			expandTreeItemRecursively(newTreeItem);
		}


//		newTree.update();
		return newTree;
	}

	private static void expandTreeItemRecursively(TreeItem treeItem) {
		treeItem.setExpanded(true);
		for (TreeItem child : treeItem.getItems()) {
			expandTreeItemRecursively(child);
		}
	}

	private static void generateChildItems(TreeItem newParentItem, TreeItem oldParentItem) {
//		newTreeItem.clearAll(true);
		for (TreeItem oldTreeItem : oldParentItem.getItems()) {
			TreeItem newTreeItem = new TreeItem(newParentItem, oldParentItem.getStyle());
			if (logger.isDebugEnabled())
				logger.debug("generateChildItems: copying child item: " + oldTreeItem.getText() + " (having "+ oldTreeItem.getItemCount() +" children)");

			copyTreeProperties(newTreeItem, oldTreeItem);
			generateChildItems(newTreeItem, oldTreeItem);
		}
	}

	private static void copyTreeProperties(TreeItem newItem, TreeItem oldItem) {
		TreeColumn[] treeColumns = oldItem.getParent().getColumns();
		for (int idx = 0; idx < treeColumns.length; ++idx) {
//			newItem.setData(oldItem.getData());
			String text = oldItem.getText(idx);
			newItem.setText(idx, text);
			newItem.setImage(idx, oldItem.getImage(idx));
		}
		String text = oldItem.getText();
		newItem.setText(text);
		newItem.setImage(oldItem.getImage());
	}
}