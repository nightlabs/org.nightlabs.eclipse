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

package org.nightlabs.base.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.composite.XComposite;


/**
 * A base class for Composites with a Table that takes care of creating a 
 * TableViewer and placing its control within a layout.
 * <p>
 * Additionally this class provides access to typed selections in the table.
 * </p>
 * <p>
 * You would usually extend this class and customize the table
 * in its methods {@link #createTableColumns(TableViewer, Table)} and {@link #setTableProvider(TableViewer)}
 * </p>
 * <p>
 * The implementations would usually perform the following steps:
 * <ul>
 *   <li>Create table-columns (createTableColumns)</li>
 *   <li>Create table-layout (createTableColumns)</li>
 *   <li>Assign content-provider (setTableProvider)</li>
 *   <li>Assign label-provider (setTableProvider)</li>
 * </ul>
 * </p>
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public abstract class AbstractTableComposite<ElementType>
extends XComposite
implements ISelectionProvider
{

	/**
	 * Default set of styles to use when constructing a single-selection viewer with border. 
	 */
	public static int DEFAULT_STYLE_SINGLE_BORDER = SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE;
	
	/**
	 * Default set of styles to use when constructing a single-selection viewer without border. 
	 */
	public static int DEFAULT_STYLE_SINGLE = SWT.FULL_SELECTION | SWT.SINGLE;
	
	/**
	 * Default set of styles to use when constructing a multi-selection viewer. 
	 * This is used as default value when constructing an {@link AbstractTableComposite} without viewerStyle
	 */
	public static int DEFAULT_STYLE_MULTI_BORDER = SWT.FULL_SELECTION | SWT.MULTI;
	
	protected TableViewer tableViewer;
	protected Table table;

	public AbstractTableComposite(Composite parent, int style) {
		this(parent, style, true);
	}
	
	public AbstractTableComposite(Composite parent, int style, boolean initTable) {
		this(parent, style, initTable, DEFAULT_STYLE_MULTI_BORDER | XComposite.getBorderStyle(parent));
	}
	
	public AbstractTableComposite(Composite parent, int style, boolean initTable, int viewerStyle) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
//		GridLayout thisLayout = new GridLayout();
//		this.setLayout(thisLayout);
//
//		GridData gd = new GridData(GridData.FILL_BOTH);
//		this.setLayoutData(gd);
		
		tableViewer = new TableViewer(this, viewerStyle);
		tableViewer.setUseHashlookup(true);
		GridData tgd = new GridData(GridData.FILL_BOTH);
		table = tableViewer.getTable(); 
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(tgd);		
		table.setLayout(new TableLayout());

		init();
		if (initTable)
			initTable();
	}
	
	protected void initTable() {
		createTableColumns(tableViewer, table);
		setTableProvider(tableViewer);
		
		if (sortColumns) {
			for (int i=0; i<tableViewer.getTable().getColumnCount(); i++) {
				TableColumn tableColumn = tableViewer.getTable().getColumn(i);
				new TableSortSelectionListener(tableViewer, tableColumn, new GenericInvertViewerSorter(i), SWT.UP);
			}
		}
	}
	
	private boolean sortColumns = true;
	
	public void setSortColumns(boolean sortColumns) {
		this.sortColumns = sortColumns;
	}
	
	/**
	 * Calls refresh for the TableViewer.
	 */
	public void refresh() {
		tableViewer.refresh();		
	}
	
	public void refresh(boolean updateLabels) {
		tableViewer.refresh(updateLabels);
	}
	
	public TableViewer getTableViewer() {
		return tableViewer;
	}
	
	/**
	 * Override for initialisation to be done
	 * before {@link #createTableColumns(TableViewer, Table)} and {@link #setTableProvider(TableViewer)}.
	 * Default implementation does nothing.
	 */
	public void init() {}
	
	
	/**
	 * Return the table viewer's selection in a Collection
	 * of the element types of this table.  
	 * @return the table viewer's selection
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public Collection<ElementType> getSelectedElements() {
		ISelection sel = getTableViewer().getSelection();
		if (sel == null || sel.isEmpty())
			return Collections.emptyList();
		else if (sel instanceof IStructuredSelection) {
			Collection<ElementType> result = new ArrayList<ElementType>();
			IStructuredSelection selection = (IStructuredSelection) sel;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = (Object) iter.next();
				result.add((ElementType)obj);
			}
			return result;
		}
		else
			return Collections.emptyList();
	}
	
	/**
	 * The first selected element. Or <code>null</code> if none selected.
	 * <p>
	 * Note that this method will cast the selection found
	 * to the ElementType this table composite was typed with.
	 * If the selected element is not of this type a
	 * {@link ClassCastException} will be thrown.
	 * 
	 * @return The first selected element.
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public ElementType getFirstSelectedElement() {
		return (ElementType) getFirstSelectedElementUnchecked();
	}

	/**
	 * Returns the first selected element without casting
	 * it to the ElementType this table composite was
	 * typed with.
	 * 
	 * @return The first selected element (of any type).
	 */
	public Object getFirstSelectedElementUnchecked() {
		ISelection sel = getTableViewer().getSelection();
		if (sel == null || sel.isEmpty())
			return null;
		else if (sel instanceof IStructuredSelection)
			return ((IStructuredSelection)sel).getFirstElement();
		else
			return null;
	}
	
	/**
	 * Add your columns here to the Table.
	 * @param tableViewer The TableViewer.
	 * @param table A shortcut to <code>tableViewer.getTable()</code>.
	 */
	protected abstract void createTableColumns(TableViewer tableViewer, Table table);

	/**
	 * Set your content and label provider for the tableViewer.
	 * 
	 * @param tableViewer The TableViewer.
	 */
	protected abstract void setTableProvider(TableViewer tableViewer);

	public Table getTable() {
		return table;
	}

	/**
	 * Sets the tableViewers input.
	 *
	 */
	public void setInput(Object input) {
		if (tableViewer != null)
			tableViewer.setInput(input);
	}

	/**
	 * Sets the selection to the given list of elements.
	 *  
	 * @param elements The elements to select.
	 */
	public void setSelectedElements(List<ElementType> elements) {
		tableViewer.setSelection(new StructuredSelection(elements));

//		TableItem[] items = tableViewer.getTable().getItems();
//		List<Integer> selIndexes = new ArrayList<Integer>();
//		for (int i = 0; i < items.length; i++) {
//			if (elements.contains(items[i].getData()))
//				selIndexes.add(i);
//		}
//		int[] selection = new int[selIndexes.size()];
//		for (int i = 0; i < selection.length; i++) {
//			selection[i] = selIndexes.get(i);
//		}
//		tableViewer.getTable().setSelection(new int[] {});
//		tableViewer.getTable().select(selection);
	}

	/**
	 * If the this table-composite's table was created with
	 * the {@link SWT#CHECK} flag this method will
	 * exlusively check the rows for the given element list.
	 * 
	 * @param elements The element to check.
	 */
	public void setCheckedElements(Collection<ElementType> elements) {
		if ((table.getStyle() & SWT.CHECK) == 0)
			return;
		TableItem[] items = tableViewer.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setChecked(false);
			if (elements.contains(items[i].getData()))
				items[i].setChecked(true);
		}
	}
	
	/**
	 * If the this table-composite's table was created with
	 * the {@link SWT#CHECK} flag this method will
	 * check all rows in the table
	 */
	public void checkAll() {
		if ((table.getStyle() & SWT.CHECK) == 0)
			return;
		TableItem[] items = tableViewer.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setChecked(true);
		}
	}
	
	/**
	 * If the this table-composite's table was created with
	 * the {@link SWT#CHECK} flag this method will
	 * uncheck all rows in the table
	 */
	public void uncheckAll() {
		if ((table.getStyle() & SWT.CHECK) == 0)
			return;
		TableItem[] items = tableViewer.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setChecked(false);
		}
	}
	
	/**
	 * If the this table-composite's table was created with
	 * the {@link SWT#CHECK} flag this method will return a list of all
	 * checked elements.
	 * 
	 * @return a list of all checked Elements.
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List<ElementType> getCheckedElements() {
		if ((table.getStyle() & SWT.CHECK) == 0)
			throw new IllegalStateException("Table is not of type SWT.CHECK, can't return checked Items!"); //$NON-NLS-1$
		List<ElementType> checkedElements = new LinkedList<ElementType>();
		TableItem[] items = tableViewer.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked())
				checkedElements.add((ElementType)items[i].getData());
		}
		return checkedElements;
	}
	
	/**
	 * Set the viewers selection.
	 * 
	 * @param elements The selection to set
	 * @see TableViewer#setSelection(ISelection)
	 */
	public void setSelection(List<ElementType> elements) {
		getTableViewer().setSelection(new StructuredSelection(elements));
	}

	/**
	 * Set the viewers selection.
	 * 
	 * @param selection The selection to set.
	 * @param reveal If true the selection will be made visible 
	 * @see TableViewer#setSelection(ISelection, boolean)
	 */
	public void setSelection(List<ElementType> elements, boolean reveal) {
		getTableViewer().setSelection(new StructuredSelection(elements), reveal);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		tableViewer.addSelectionChangedListener(listener);
	}
	
	/**
	 * Adds a selection listener that is triggered whenever the check state of a
	 * table item is gets changed.
	 * 
	 * @param listener
	 */
	public void addCheckStateChangedListener(final SelectionListener listener) {
		table.addSelectionListener(new SelectionListener () {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.CHECK)
					listener.widgetSelected(e);
			}
		});
	}

	public ISelection getSelection()
	{
		return tableViewer.getSelection();
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		tableViewer.removeSelectionChangedListener(listener);
	}
	
	public void removeCheckStateChangedListener(SelectionListener listener) {
		table.removeSelectionListener(listener);
	}

	public void setSelection(ISelection selection)
	{
		tableViewer.setSelection(selection);
	}

	@Override
	public Menu getMenu() {
		return getTable().getMenu();
	}

	@Override
	public void setMenu(Menu menu) {
		getTable().setMenu(menu);
	}
	
}
