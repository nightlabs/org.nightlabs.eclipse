/**
 * 
 */
package org.eclipse.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TypedListener;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 *
 */
public class TableCursor {

	private TableItem selectedItem;
	private int selectedColumn = -1;
	private Table table;
	
	/**
	 * 
	 */
	public TableCursor(final Table table, int style) {
		this.table = table;
		table.addListener(SWT.MouseDown, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				TableItem[] allItems = table.getItems();
				System.out.println(new Point(event.x, event.y));
				selectedItem = table.getItem(new Point(event.x, event.y));
				selectedColumn = -1;
				if (selectedItem != null) {
					int totalWidth = 0;
					for (int i = 0; i < table.getColumnCount(); i++) {
						int itemWidth = table.getColumn(i).getWidth();
						if (event.x < totalWidth + itemWidth) {
							selectedColumn = i;
							return;
						}
						totalWidth += itemWidth;
						
					}
//					int totalWidth = 0;
//					for (int i = 0; i < .length; i++) {
//						int itemWidth = selection[i].getBounds().x;
//						if (event.x < totalWidth + itemWidth) {
//							selectedItem = selection[i];
//							return;
//						}
//						totalWidth += itemWidth;
//					}
					System.out.println(selectedItem.getText());
				}
//				for (TableItem tableItem : allItems) {
//				}
//				int totalWidth = 0;
//				for (int i = 0; i < selection.length; i++) {
//					int itemWidth = selection[i].getBounds().x;
//					if (event.x < totalWidth + itemWidth) {
//						selectedItem = selection[i];
//						return;
//					}
//					totalWidth += itemWidth;
//				}
			}
		});		
	}
	
	public void addKeyListener(KeyListener listener) {
		
	}
	
	public void removeKeyListener(KeyListener listener) {
		
	}
	
	public int getColumn() {
		return selectedColumn;
	}
	
	public TableItem getRow() {
		return selectedItem;
	}
	
	public int indexOf(TableColumn tableColumn) {
		return table.indexOf(tableColumn);
	}
	
	public void setBackground(Color background) {
		
	}

	public void setForeground(Color background) {
		
	}
	
	public void addSelectionListener(final SelectionListener listener) {
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		table.addListener(SWT.Selection, typedListener);
		table.addListener(SWT.DefaultSelection, typedListener);
	}
	
	
}
