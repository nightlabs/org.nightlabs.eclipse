package org.nightlabs.eclipse.ui.control.export.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class Container
{
	private List<Column> columns;
	private List<Item> items;

	public Container(Control control) {
		columns = new ArrayList<Column>();
		items = new ArrayList<Item>();

		generateContainer(control);
	}

	private void generateContainer(Control control) {
		extractColumns(control);
		extractElements(control);
	}

	private static Column DEFAULT_COLUMN = new Column("Default", null);
	private void extractColumns(Control control) {
		columns.clear();

		if (control instanceof Table) {
			Table table = (Table) control;
			TableColumn[] tableColumns = table.getColumns();
			for (TableColumn tableColumn : tableColumns) {
				Image image = tableColumn.getImage();
				columns.add(new Column(tableColumn.getText(), image == null ? null : image.getImageData()));
			}
		} else if (control instanceof Tree) {
			Tree tree = (Tree) control;
			TreeColumn[] treeColumns = tree.getColumns();
			for (TreeColumn treeColumn : treeColumns) {
				Image image = treeColumn.getImage();
				columns.add(new Column(treeColumn.getText(), image == null ? null : image.getImageData()));
			}
		}

		if (columns.isEmpty())
			columns.add(DEFAULT_COLUMN);
	}

	private void extractElements(Control control) {
		if (control instanceof Table) {
			Table table = (Table) control;
			for (TableItem tableItem : table.getItems()) {
				Item item = new Item(null);
				for (int i = 0; i < columns.size(); i++) {
					Image image = tableItem.getImage(i);
					item.addCell(new Cell(tableItem.getText(i), image == null?null:image.getImageData()));
				}
				items.add(item);
			}
		} else if (control instanceof Tree) {
			Tree tree = (Tree) control;
			for (TreeItem treeItem : tree.getItems()) {
				generateSubTreeItems(treeItem, tree, null);
			}
		}
	}

	//For Tree
	private void generateSubTreeItems(TreeItem treeItem, Tree tree, Item parentItem) {
		Item item = new Item(parentItem);
		for (int i = 0; i < columns.size(); i++) {
			Image image = treeItem.getImage(i);
			String itemText = treeItem.getText(i);
			item.addCell(new Cell(itemText, image == null?null:image.getImageData()));
		}

		//Filter
		if (!item.isEmpty()) {
			items.add(item);
		}

		for (TreeItem subTreeItem : treeItem.getItems())
			generateSubTreeItems(subTreeItem, tree, item);
	}

	//Getter-Setter
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public List<Item> getItems() {
		return items;
	}
}
