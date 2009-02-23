package org.nightlabs.eclipse.ui.control.export.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
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
		extractColumnNames(control);
		exportElements(control);
	}

	private void extractColumnNames(Control control) {
		if (control instanceof Table) {
			Table table = (Table) control;
			TableColumn[] tableColumns = table.getColumns();
			for (int i = 0; i < tableColumns.length; i++) {
				Image image = tableColumns[i].getImage();
				columns.add(new Column(tableColumns[i].getText(), image == null?null:image.getImageData()));
			}
		} else if (control instanceof Tree) {
			Tree tree = (Tree) control;
			java.util.List<String> columnNames = new ArrayList<String>();
			for (int i = 0; i < tree.getColumnCount(); i++) {
				Image image = tree.getColumn(i).getImage();
				columnNames.add(tree.getColumn(i).getText());
				columns.add(new Column(tree.getColumn(i).getText(), image == null?null:image.getImageData()));
			}
		}
	}

	private void exportElements(Control control) {
		if (control instanceof Table) {
			Table table = (Table) control;
			for (TableItem tableItem : table.getItems()) {
				Item item = new Item(null);
				for (int i = 0; i < table.getColumnCount(); i++) {
					Image image = tableItem.getImage(i);
					item.addCell(new Cell(tableItem.getText(i), image == null?null:image.getImageData()));
				}
				items.add(item);
			}
		} else if (control instanceof Tree) {
			Tree tree = (Tree) control;
			for (TreeItem treeItem : tree.getItems()) {
				Item item = new Item(null);
				for (int i = 0; i < tree.getColumnCount(); i++) {
					Image image = treeItem.getImage(i);
					item.addCell(new Cell(treeItem.getText(i), image == null?null:image.getImageData()));
				}
				items.add(item);
				generateSubTreeItems(treeItem, tree, item);
			}
		}
	}

	//For Tree
	private void generateSubTreeItems(TreeItem treeItem, Tree tree, Item parentItem) {
		Item item = new Item(parentItem);
		for (int i = 0; i < tree.getColumnCount(); i++) {
			item.addCell(new Cell(treeItem.getText(), treeItem.getImage().getImageData()));
		}
		items.add(item);
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
