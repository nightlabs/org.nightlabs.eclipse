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
}