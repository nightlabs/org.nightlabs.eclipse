package org.nightlabs.eclipse.ui.control.export;

import java.io.FileWriter;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class OpenCSVUtil {
	public static void exportControlToCSV(String fileName, Control control) {
		try {
			String[] columnNames = extractColumnNames(control);
			if (columnNames == null) {
				return;
			}
			CSVWriter writer = new CSVWriter(new FileWriter(fileName), ',');
			writer.writeNext(columnNames);

			exportElements(control, writer);

			//Closes writer
			writer.close();
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}
	}

	private static String[] extractColumnNames(Control control) {
		if (control instanceof Table) {
			Table table = (Table) control;
			TableColumn[] tableColumns = table.getColumns();
			String[] columnNames = new String[tableColumns.length];
			for (int i = 0; i < tableColumns.length; i++) {
				columnNames[i] = tableColumns[i].getText();
			}
			return columnNames;
		} else if (control instanceof Tree) {
			Tree tree = (Tree) control;
			java.util.List<String> columnNames = new ArrayList<String>();
			for (int i = 0; i < tree.getColumnCount(); i++) {
				columnNames.add(tree.getColumn(i).getText());
			}
			return columnNames.toArray(new String[columnNames.size()]);
		}
		return null;
	}
	
	private static void exportElements(Control control, CSVWriter writer) {
		if (control instanceof Table) {
			Table table = (Table) control;
			for (TableItem tableItem : table.getItems()) {
				String[] item = new String[table.getColumnCount()];
				for (int i = 0; i < table.getColumnCount(); i++) {
					item[i] = tableItem.getText(i); 
				}
				writer.writeNext(item);
			}
		} else if (control instanceof Tree) {
			Tree tree = (Tree) control;
			for (TreeItem treeItem : tree.getItems()) { 
				String[] item = new String[tree.getColumnCount()];
				for (int i = 0; i < tree.getColumnCount(); i++) {
					item[i] = treeItem.getText(i); 
				}
				writer.writeNext(item);
			}
		}
	}
}