/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.composite.groupedcontent;

import java.util.Collection;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.table.AbstractTableComposite;
import org.nightlabs.base.table.TableContentProvider;
import org.nightlabs.base.table.TableLabelProvider;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class GroupedContentSwitcherTable extends AbstractTableComposite {
	
	private String groupTitle;
//	private TableColumn column;
	
	// TODO: dispose sometime
	private static final Color COLOR_HIGHLIGHT = new Color(null, 200, 200, 200);
	private static final Color COLOR_SELECTED = new Color(null, 11, 5, 180);
	private static final Color COLOR_BACKGROUND = new Color(null, 111, 111, 111);

//	private MouseMoveListener mouseMoveListener = new MouseMoveListener() {
//		public void mouseMove(MouseEvent event) {
//			TableItem item = getTableItem(event);
//			highLightItem(item);
//		}
//		protected TableItem getTableItem(MouseEvent event)
//		{
//			Point pt = new Point(event.x, event.y);
//			TableItem tableItem = getTable().getItem(pt);
//			return tableItem;
//		}
//	};
	
//	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
//		public void selectionChanged(SelectionChangedEvent event) {
//			int idx = getTable().getSelectionIndex();			
//			if (idx >= 0)
//				selectItem(getTable().getItem(idx));
//		}
//	};
	
	private class ContentProvider extends TableContentProvider  {
		/**
		 * @see org.nightlabs.base.table.TableContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Collection)
				return ((Collection)inputElement).toArray();
			return super.getElements(inputElement);
		}
	}
	
	private class LabelProvider extends TableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof GroupedContentProvider)
				return ((GroupedContentProvider)element).getGroupIcon();
			return super.getColumnImage(element, columnIndex);
		}
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof GroupedContentProvider)
				return ((GroupedContentProvider)element).getGroupTitle();
			throw new IllegalArgumentException("LabelProvider for GroupedContentSwitcherTable is restricted to GroupedContentProvider elements."); //$NON-NLS-1$
		}
	}	
	
	public GroupedContentSwitcherTable(Composite parent, int style) {
		super(parent, style, true, getBorderStyle(parent) | SWT.FULL_SELECTION | SWT.V_SCROLL);
		getGridLayout().marginHeight = 2;
		getGridLayout().marginWidth = 2;
//		super(parent, style, true, SWT.FULL_SELECTION | SWT.V_SCROLL);
		getTable().setLinesVisible(false);
		getTable().setHeaderVisible(false);
//		getTable().setBackground(COLOR_BACKGROUND);
//		getTable().addMouseMoveListener(mouseMoveListener);
//		getTableViewer().addSelectionChangedListener(selectionListener);
//		getTable().setForeground(parent.getForeground());
	}

	protected void createTableColumns(TableViewer tableViewer, Table table) {
//		column = new TableColumn(table, SWT.LEFT); 
//		if (groupTitle != null)
//			column.setText(groupTitle);
//		table.setLayout(new WeightedTableLayout(new int[] {1}));		
	}

	protected void setTableProvider(TableViewer tableViewer) {		
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

	/**
	 * @return Returns the groupTitle.
	 */
	public String getGroupTitle() {
		return groupTitle;
	}

	/**
	 * @param groupTitle The groupTitle to set.
	 */
	public void setGroupTitle(String groupTitle) {			
		this.groupTitle = groupTitle;
//		if (groupTitle != null){
//			column.setText(groupTitle);
//			layout();
//		}			
	}
	
	protected void selectItem(TableItem item) {
		TableItem[] items = getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setBackground(COLOR_BACKGROUND);
		}
		if (item != null)
			item.setBackground(COLOR_SELECTED);
	}
	
	protected void highLightItem(TableItem item) {
		TableItem[] items = getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getBackground() != COLOR_SELECTED)
				items[i].setBackground(COLOR_BACKGROUND);
		}
		if (item != null)
			item.setBackground(COLOR_HIGHLIGHT);
	}
}
