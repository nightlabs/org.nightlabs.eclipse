package org.nightlabs.eclipse.ui.control.export;

import java.io.FileWriter;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.nightlabs.eclipse.ui.control.export.model.Cell;
import org.nightlabs.eclipse.ui.control.export.model.Column;
import org.nightlabs.eclipse.ui.control.export.model.Container;
import org.nightlabs.eclipse.ui.control.export.model.Item;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class OpenCSVUtil
{
	public static void exportControlToCSV(String fileName, Control control, char seperator) {
		try {
			Container container = new Container(control);
			List<Column> columns = container.getColumns();
			List<Item> items = container.getItems();

			String[] columnNames = new String[columns.size()];
			for (int i = 0; i < columns.size(); i++) {
				columnNames[i] = columns.get(i).getName();
			}

			CSVWriter writer = new CSVWriter(new FileWriter(fileName), seperator);
			writer.writeNext(columnNames);

			for (Item item : items) {
				List<Cell> cells = item.getCells();
				String[] cellDatas = new String[cells.size()];
				for (int i = 0; i < cells.size(); i++ ) {
					cellDatas[i] = cells.get(i).getText();
				}
				writer.writeNext(cellDatas);
			}

			//Closes writer
			writer.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

//	private static String[] extractColumnNames(Control control) {
//		if (control instanceof Table) {
//			Table table = (Table) control;
//			TableColumn[] tableColumns = table.getColumns();
//			String[] columnNames = new String[tableColumns.length];
//			for (int i = 0; i < tableColumns.length; i++) {
//				columnNames[i] = tableColumns[i].getText();
//			}
//			return columnNames;
//		} else if (control instanceof Tree) {
//			Tree tree = (Tree) control;
//			java.util.List<String> columnNames = new ArrayList<String>();
//			for (int i = 0; i < tree.getColumnCount(); i++) {
//				columnNames.add(tree.getColumn(i).getText());
//			}
//			return columnNames.toArray(new String[columnNames.size()]);
//		}
//		return null;
//	}
//
//	private static void exportElements(Control control, CSVWriter writer) {
//		if (control instanceof Table) {
//			Table table = (Table) control;
//			for (TableItem tableItem : table.getItems()) {
//				String[] item = new String[table.getColumnCount()];
//				for (int i = 0; i < table.getColumnCount(); i++) {
//					item[i] = tableItem.getText(i);
//				}
//				writer.writeNext(item);
//			}
//		} else if (control instanceof Tree) {
//			Tree tree = (Tree) control;
//			for (TreeItem treeItem : tree.getItems()) {
//				generateSubTreeItems(treeItem, tree, writer);
//			}
//		}
//	}
//
//	//For Tree
//	private static void generateSubTreeItems(TreeItem treeItem, Tree tree, CSVWriter writer) {
//		String[] item = new String[tree.getColumnCount()];
//		for (int i = 0; i < tree.getColumnCount(); i++) {
//			item[i] = treeItem.getText(i);
//		}
//		writer.writeNext(item);
//
//		for (TreeItem subTreeItem : treeItem.getItems())
//			generateSubTreeItems(subTreeItem, tree, writer);
//	}
}