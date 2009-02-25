package org.nightlabs.eclipse.ui.control.export.copy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.eclipse.ui.control.export.model.Cell;
import org.nightlabs.eclipse.ui.control.export.model.Column;
import org.nightlabs.eclipse.ui.control.export.model.Container;
import org.nightlabs.eclipse.ui.control.export.model.Item;

public class WidgetCopyUtil
{
	private static final Logger logger = Logger.getLogger(WidgetCopyUtil.class);

	//Table
	public static Table copyTable(Composite composite, Table oldTable, boolean isCopyStyle) {
		Table newTable = new Table(composite, oldTable.getStyle());
		Container container = new Container(oldTable);

		List<Column> columns = container.getColumns();
		List<Item> items = container.getItems();

		for (Column column : columns) {
			TableColumn tc = new TableColumn(newTable, SWT.NONE);
			tc.setText(column.getName());
			if (column.getImageData() != null)
				tc.setImage(new Image(composite.getDisplay(), column.getImageData()));
			tc.pack();
		}

		for (Item item : items) {
			TableItem ti = new TableItem(newTable, SWT.NONE);
			List<Cell> cells = item.getCells();
			for (int i = 0; i < cells.size(); i++) {
				ti.setText(i, cells.get(i).getText());
				if (cells.get(i).getImageData() != null)
					ti.setImage(i, new Image(composite.getDisplay(), cells.get(i).getImageData()));
			}
		}
		newTable.setHeaderVisible(true);
		return newTable;
	}

	//Tree
	private static int cLevel;
	public static Tree copyTree(Composite composite, Tree oldTree, boolean isCopyStyle) {
		Tree newTree = new Tree(composite, oldTree.getStyle());
		Container container = new Container(oldTree);

		List<Column> columns = container.getColumns();
		List<Item> items = container.getItems();

		for (Column column : columns) {
			TreeColumn newTreeColumn = new TreeColumn(newTree, SWT.NONE);
			newTreeColumn.setText(column.getName());
			if (column.getImageData() != null)
				newTreeColumn.setImage(new Image(composite.getDisplay(), column.getImageData()));

			newTreeColumn.pack();
		}

		Map<Integer, TreeItem> lastParentTreeItemMap = new HashMap<Integer, TreeItem>();
		for (Item item : items) {
			cLevel = item.getLevel();
			TreeItem treeItem = null;
			//check for the parent
			if (cLevel == 0) {
				treeItem = new TreeItem(newTree, SWT.NONE);
			}
			else {
				TreeItem lastParentTreeItem = lastParentTreeItemMap.get(cLevel - 1);
				treeItem = new TreeItem(lastParentTreeItem, SWT.NONE);
			}

			//create cells
			List<Cell> cells = item.getCells();
			for (int i = 0; i < columns.size(); i++) {
				treeItem.setText(i, cells.get(i).getText());
				if (cells.get(i).getImageData() != null)
					treeItem.setImage(i, new Image(composite.getDisplay(), cells.get(i).getImageData()));
			}

			//put current parent
			lastParentTreeItemMap.put(cLevel, treeItem);
		}

		//expand tree
		TreeItem[] newTreeItems = newTree.getItems();
		for (TreeItem newTreeItem : newTreeItems) {
			expandTreeItemRecursively(newTreeItem);
		}

		return newTree;
	}

	private static void expandTreeItemRecursively(TreeItem treeItem) {
		treeItem.setExpanded(true);
		for (TreeItem child : treeItem.getItems()) {
			expandTreeItemRecursively(child);
		}
	}
}