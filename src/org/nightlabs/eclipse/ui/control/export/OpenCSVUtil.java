package org.nightlabs.eclipse.ui.control.export;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.eclipse.ui.control.export.model.Cell;
import org.nightlabs.eclipse.ui.control.export.model.Column;
import org.nightlabs.eclipse.ui.control.export.model.Container;
import org.nightlabs.eclipse.ui.control.export.model.Item;
import org.nightlabs.eclipse.ui.control.export.resource.Messages;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class OpenCSVUtil
{
	public static void exportControlToCSV(String fileName, Control control, char seperator) {
		try {
			Container container = new Container(control, false);
			List<Column> columns = container.getColumns();
			List<Item> items = container.getItems();

			CSVWriter writer = new CSVWriter(new FileWriter(fileName), seperator);

			exportColumnNames(control, columns, writer);
			exportItems(control, items, writer);

			//Closes writer
			writer.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void exportColumnNames(Control control, List<Column> columns, CSVWriter writer) {
		List<String> columnNames = new ArrayList<String>();

		if (control instanceof Tree) {
			columnNames.add(Messages.getString("org.nightlabs.eclipse.ui.control.export.OpenCSVUtil.column.level.name")); //$NON-NLS-1$
		}

		for (Column column : columns) {
			columnNames.add(column.getName());
		}

		writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
	}

	private static void exportItems(Control control, List<Item> items, CSVWriter writer) {
		boolean isTree = false;
		if (control instanceof Tree)
			isTree = true;

		for (Item item : items) {
			List<Cell> cells = item.getCells();
			List<String> cellDatas = new ArrayList<String>();

			if (isTree) {
				cellDatas.add(Integer.toString(item.getLevel()));
			}

			for (int i = 0; i < cells.size(); i++ ) {
				cellDatas.add(cells.get(i).getText());
			}
			writer.writeNext(cellDatas.toArray(new String[cellDatas.size()]));
		}
	}
}