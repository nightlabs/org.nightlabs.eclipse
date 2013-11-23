/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.ui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.text.TableView;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.composite.ContextMenuReadyComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.util.Util;

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
	extends ContextMenuReadyComposite // XComposite
	implements ISelectionProvider
{
	/**
	 * Default set of styles to use when constructing a single-selection viewer without border.
	 */
	public static final int DEFAULT_STYLE_SINGLE = SWT.FULL_SELECTION | SWT.SINGLE;

	/**
	 * Default set of styles to use when constructing a single-selection viewer with border.
	 */
	public static final int DEFAULT_STYLE_SINGLE_BORDER = DEFAULT_STYLE_SINGLE | SWT.BORDER;

	/**
	 * Default set of styles to use when constructing a multi-selection viewer without border.
	 */
	public static final int DEFAULT_STYLE_MULTI = SWT.FULL_SELECTION | SWT.MULTI;

	/**
	 * Default set of styles to use when constructing a multi-selection viewer.
	 * This is used as default value when constructing an {@link AbstractTableComposite} without viewerStyle
	 */
	public static final int DEFAULT_STYLE_MULTI_BORDER = DEFAULT_STYLE_MULTI | SWT.BORDER;

	private TableViewer tableViewer;
	private Table table;
	private boolean editable = true;
	private int viewerStyle;
	private GridData gridData;

	public AbstractTableComposite(Composite parent, int style) {
		this(parent, style, true);
	}

	public AbstractTableComposite(Composite parent, int style, boolean initTable) {
		this(parent, style, initTable, DEFAULT_STYLE_MULTI_BORDER);
	}

	public AbstractTableComposite(Composite parent, int style, boolean initTable, int viewerStyle)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		if ((viewerStyle & SWT.BORDER) == SWT.BORDER)
		{
			int borderStyle = XComposite.getBorderStyle(parent);
			// remove original SWT.BORDER flag by negating the SWT.BORDER mask and &-ing it with the original
			viewerStyle &= ~SWT.BORDER;
			viewerStyle |= borderStyle;
		}
		this.viewerStyle = viewerStyle;
		createTableViewer(viewerStyle);
		init();
		if (initTable)
		{
			initTable();

			// set default minimum size
			if (gridData != null) {
				gridData.minimumWidth = tableViewer.getTable().getColumnCount() * 30;
				gridData.minimumHeight = 50;
			}
		}
	}

	protected int getViewerStyle() {
		return viewerStyle;
	}

	protected void createTableViewer(int viewerStyle) {
		tableViewer = new TableViewer(this, viewerStyle);
		tableViewer.setUseHashlookup(true);
		gridData = new GridData(GridData.FILL_BOTH);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gridData);
		table.setLayout(new TableLayout());

	}

	protected void initTable() {
		if (table.isDisposed())
			return;

		createTableColumns(tableViewer, table);
		setTableProvider(tableViewer);

		if (sortColumns) {
			for (int i = 0; i < tableViewer.getTable().getColumnCount(); i++) {
				TableColumn tableColumn = tableViewer.getTable().getColumn(i);
				new TableSortSelectionListener(tableViewer, tableColumn, new GenericInvertViewerSorter(i), SWT.UP);
			}
		}

		// Add selection listener to make the selection of an inactive table (! #isEditable()) impossible.
		// However the event will be propagated to the rest of the listeners if they were not added via
		// #addCheckStateChangedListener()!
		getTable().addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if ((e.detail & SWT.CHECK) == SWT.CHECK && ! isEditable())
				{
					final TableItem tableItem = (TableItem) e.item;
					tableItem.setChecked(! tableItem.getChecked());
					e.doit = false;
				}
			}
		});
		getTableViewer().addDoubleClickListener(doubleClickListener);
	}

	private boolean sortColumns = true;

	/**
	 * Sets the sortColumns state.
	 * If set to true all table columns of the {@link TableViewer} will be automatically
	 * sorted in an alphabetic manner
	 * @param sortColumns determines if the table columns should be sortable (true) or not (false)
	 */
	public void setSortColumns(boolean sortColumns) {
		this.sortColumns = sortColumns;
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void refresh() {
		tableViewer.refresh();
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void refresh(boolean updateLabels)
	{
		if (!tableViewer.getTable().isDisposed())
			tableViewer.refresh(updateLabels);
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * Override for initialization to be done
	 * before {@link #createTableColumns(TableViewer, Table)} and {@link #setTableProvider(TableViewer)}.
	 * Default implementation does nothing.
	 */
	public void init() {
	}

	/**
	 * Return the table viewer's selection in a Collection
	 * of the element types of this table.
	 * @return the table viewer's selection, if selection is null or empty will return an unmodifiable empty List
	 */
	public Collection<ElementType> getSelectedElements() {
		ISelection sel = tableViewer.getSelection();
		if (sel == null || sel.isEmpty())
			return Collections.emptyList();
		else if (sel instanceof IStructuredSelection) {
			Collection<ElementType> result = new ArrayList<ElementType>();
			IStructuredSelection selection = (IStructuredSelection) sel;
			for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				ElementType el = getSelectionObject(obj);
				if (el != null)
					result.add(el);
			}
			return result;
		} else
			return Collections.emptyList();
	}

	private Collection<ElementType> elements;
	private Object elementsCacheInput;

	/**
	 * Returns all elements of this table composite.
	 * @return All elements of this table composite or unmodifiable empty Set if there are none.
	 */
	@SuppressWarnings("unchecked")
	public synchronized Collection<ElementType> getElements() {
		if (!Util.equals(elementsCacheInput, getTableViewer().getInput())) {
			elementsCacheInput = getTableViewer().getInput();
			elements = new LinkedList<ElementType>();
			for (TableItem item : getTable().getItems()) {
				ElementType el = getSelectionObject(item.getData());
				if (el != null)
					elements.add((ElementType) item.getData());
			}
		}
		return (Collection<ElementType>) (elements != null ? elements : Collections.emptySet());
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
	public ElementType getFirstSelectedElement() {
		return getSelectionObject(getFirstSelectedElementUnchecked());
	}

	/**
	 * Returns the first selected element without casting
	 * it to the ElementType this table composite was
	 * typed with.
	 *
	 * @return The first selected element (of any type).
	 */
	public Object getFirstSelectedElementUnchecked() {
		ISelection sel = tableViewer.getSelection();
		if (sel == null || sel.isEmpty())
			return null;
		else if (sel instanceof IStructuredSelection)
			return ((IStructuredSelection) sel).getFirstElement();
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

	protected Table getTable() {
		return table;
	}

	/**
	 * Sets the tableViewers input.
	 */
	public void setInput(Object input) {
		if (tmpLabelProvider != null) {
			tableViewer.setInput(null);
			tableViewer.setLabelProvider(tmpLabelProvider);
			tableViewer.setContentProvider(tmpContentProvider);
			tmpLabelProvider = null;
			tmpContentProvider = null;
		}
		// we only set the input if the viewers control is there and not disposed
		if (tableViewer != null && tableViewer.getControl() != null && !tableViewer.getControl().isDisposed())
			tableViewer.setInput(input);
	}

	private IBaseLabelProvider tmpLabelProvider;
	private IContentProvider tmpContentProvider;

	/**
	 * Here you can set a string message displayed to notify the user about an asynchronous load process.
	 * <p>
	 * Note, that new label and content providers as well as a new input will be set to the {@link TableView}.
	 * The providers will be restored on the next call to {@link #setInput(Object)}.
	 * </p>
	 * <p>
	 * Also note, that using this method will cause the current content- and label-provider to be disposed
	 * as the implementation of {@link ContentViewer} disposes them when setting a new ones.
	 * </p>
	 * @param message The message to be shown.
	 */
	public void setLoadingMessage(String message) {
		if (!tableViewer.getTable().isDisposed()) {
			if (tmpLabelProvider == null) {
				// Set the label and content provider only they are not already set.
				tmpLabelProvider = tableViewer.getLabelProvider();
				tmpContentProvider = tableViewer.getContentProvider();
				tableViewer.setLabelProvider(new TableLabelProvider() {
					public String getColumnText(Object element, int columnIndex) {
						if (columnIndex == 0 && element != null)
							return element.toString();
						return ""; //$NON-NLS-1$
					}
				});
				tableViewer.setContentProvider(new ArrayContentProvider());
			}
			tableViewer.setInput(new String[] { message });
		}
	}

	/**
	 * Sets the selection to the given list of elements.
	 *
	 * @param elements The elements to select.
	 */
	public void setSelectedElements(Collection<ElementType> elements) {
		List<ElementType> elementList = null;
		if (elements instanceof List) {
			elementList = (List<ElementType>) elements;
		} else {
			elementList = new ArrayList<ElementType>(elements);
		}
		setSelection(elementList);
	}

	/**
	 * If the this table-composite's table was created with
	 * the {@link SWT#CHECK} flag this method will
	 * exclusively check the rows for the given element list.
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
	public List<ElementType> getCheckedElements() {
		if ((table.getStyle() & SWT.CHECK) == 0)
			throw new IllegalStateException("Table is not of type SWT.CHECK, can't return checked Items!"); //$NON-NLS-1$
		List<ElementType> checkedElements = new LinkedList<ElementType>();
		TableItem[] items = tableViewer.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked()) {
				ElementType el = getSelectionObject(items[i].getData());
				if (el != null)
					checkedElements.add(el);
			}
		}
		return checkedElements;
	}

	/**
	 * If the this table-composite's table was created with
	 * the {@link SWT#CHECK} flag this method will return a list of all
	 * unchecked elements.
	 *
	 * @return a list of all unchecked Elements.
	 */
	public List<ElementType> getUncheckedElements() {
		if ((table.getStyle() & SWT.CHECK) == 0)
			throw new IllegalStateException("Table is not of type SWT.CHECK, can't return checked Items!"); //$NON-NLS-1$
		List<ElementType> uncheckedElements = new LinkedList<ElementType>();
		TableItem[] items = tableViewer.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			if (!items[i].getChecked()) {
				ElementType el = getSelectionObject(items[i].getData());
				if (el != null)
					uncheckedElements.add(el);
			}
		}
		return uncheckedElements;
	}


	/**
	 * Should check if the given element (out of the selection of the underlying viewer)
	 * is a valid selection for this {@link AbstractTableComposite}, i.e. if it is of
	 * the expected class. If the element is valid the element itself should be returned,
	 * if not <code>null</code> should be returned.
	 * <p>
	 * This implementation tries to cast the element to the parameterized ElementType
	 * and returns <code>null</code> if this fails.
	 * </p>
	 *
	 * @param element The element to check.
	 * @return The given element if its a valid selection, <code>null</code> otherwise.
	 */
	@SuppressWarnings("unchecked")
	protected ElementType getSelectionObject(Object element) {
		try {
			return (ElementType) element;
		} catch (ClassCastException e) {
			return null;
		}
	}

	/**
	 * Set the viewers selection.
	 *
	 * @param elements The selection to set
	 * @see TableViewer#setSelection(ISelection)
	 */
	public void setSelection(List<ElementType> elements) {
		tableViewer.setSelection(new StructuredSelection(elements));
	}

	/**
	 * Set the viewers selection.
	 *
	 * @param selection The selection to set.
	 * @param reveal If true the selection will be made visible
	 * @see TableViewer#setSelection(ISelection, boolean)
	 */
	public void setSelection(List<ElementType> elements, boolean reveal) {
		tableViewer.setSelection(new StructuredSelection(elements), reveal);
	}

  private ListenerList selectionChangedListeners = new ListenerList();
  private ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener()
  {
		@Override
		public void selectionChanged(final SelectionChangedEvent event)
		{
      Object[] listeners = selectionChangedListeners.getListeners();
      for (int i = 0; i < listeners.length; ++i) {
          final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
          SafeRunnable.run(new SafeRunnable() {
              public void run() {
                  l.selectionChanged(event);
              }
          });
      }
		}
  };

	/**
	 * @see {@link TableViewer#addSelectionChangedListener(ISelectionChangedListener)}.
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (selectionChangedListeners.isEmpty() && isEditable())
		{
			tableViewer.addSelectionChangedListener(selectionChangedListener);
		}

		selectionChangedListeners.add(listener);
	}

	private Map<SelectionListener, CheckStateChangeAdapter> checkStateListener2Adapter =
		new HashMap<SelectionListener, CheckStateChangeAdapter>();

	/**
	 * Adds a selection listener that is triggered whenever the check state of a
	 * table item is changed.
	 *
	 * @param listener the actual SelectionListener that should handle the check state changed events.
	 */
	public void addCheckStateChangedListener(final SelectionListener listener)
	{
		assert listener != null;

		 // this listener instance is already registered.
		if (checkStateListener2Adapter.get(listener) != null)
			return;

		CheckStateChangeAdapter selectionAdapter = new CheckStateChangeAdapter(listener);
		table.addSelectionListener(selectionAdapter);
	}

	/**
	 * Small helper class for wrapping normal SelectionListeners in order to only propagate check
	 * state change events.
	 *
	 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
	 */
	public class CheckStateChangeAdapter extends SelectionAdapter
	{
		private SelectionListener listener;

		public CheckStateChangeAdapter(SelectionListener originalListener)
		{
			assert originalListener != null;
			this.listener = originalListener;
		}

		@Override
		public void widgetSelected(SelectionEvent e)
		{
			if (e.detail == SWT.CHECK && isEditable())
			{
				listener.widgetSelected(e);
			}
		}
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public ISelection getSelection() {
		ISelection viewerSelection = tableViewer.getSelection();
		if (viewerSelection instanceof IStructuredSelection) {
			IStructuredSelection structuredSel = (IStructuredSelection) viewerSelection;
			List<Object> newSelection = new ArrayList<Object>(structuredSel.size());
			for (Iterator<?> it = structuredSel.iterator(); it.hasNext();) {
				Object element = it.next();
				Object el = getSelectionObject(element);
				if (el != null)
					newSelection.add(el);
			}
			return new StructuredSelection(newSelection);
		}
		return tableViewer.getSelection();
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	/**
	 * Removes the given listener if it was registered before.
	 * @param listener the check state changed listener that shall be rmeoved.
	 */
	public void removeCheckStateChangedListener(SelectionListener listener)
	{
		final CheckStateChangeAdapter listenerAdapter = checkStateListener2Adapter.get(listener);
		if (listenerAdapter == null)
			return;

		table.removeSelectionListener(listenerAdapter);
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void setSelection(ISelection selection) {
		tableViewer.setSelection(selection);
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void setSelection(ISelection selection, boolean reveal) {
		tableViewer.setSelection(selection, reveal);
	}

	@Override
	public Menu getMenu() {
		return table.getMenu();
	}

	@Override
	public void setMenu(Menu menu) {
		table.setMenu(menu);
	}

	public int getItemCount() {
		return table.getItemCount();
	}

	public int getSelectionCount() {
		return table.getSelectionCount();
	}

	/**
	 * Selects the Element at the given index.
	 * <p>
	 * Note that using this method will invoke {@link ISelectionChangedListener}s linked to the TableViewer.
	 * </p>
	 *
	 * @param index The index of the Element to select.
	 */
	@SuppressWarnings("unchecked")
	public void select(int index) {
//		table.select(index);
		if (index >= 0 && index < table.getItemCount()) {
			setSelection((List<ElementType>) Collections.singletonList(table.getItem(index).getData()));
		}
	}

	@Override
	public boolean setFocus() {
		return table.setFocus();
	}

	private ListenerList doubleClickListeners = new ListenerList();
	private IDoubleClickListener doubleClickListener = new IDoubleClickListener()
	{
		@Override
		public void doubleClick(final DoubleClickEvent event)
		{
			Object[] listeners = doubleClickListeners.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
				final IDoubleClickListener l = (IDoubleClickListener) listeners[i];
				SafeRunnable.run(new SafeRunnable() {
					public void run() {
						l.doubleClick(event);
					}
				});
			}
		}
	};

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void addDoubleClickListener(IDoubleClickListener listener) {
//	What is this supposed to do?? No comments nothing, whoever wrote this please explain (marius, marco, khai)
//		if (doubleClickListeners.isEmpty() && isEditable())
//		{
//			tableViewer.addDoubleClickListener(listener);
//		}

		doubleClickListeners.add(listener);
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void removeDoubleClickListener(IDoubleClickListener listener)
	{
		doubleClickListeners.remove(listener);
	}

	public void setLinesVisible(boolean visible) {
		table.setLinesVisible(visible);
	}

	public void setHeaderVisible(boolean visible) {
		table.setHeaderVisible(visible);
	}

	public int getSelectionIndex() {
		return table.getSelectionIndex();
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public Control getControl() {
		return tableViewer.getControl();
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void setComparator(ViewerComparator comparator) {
		tableViewer.setComparator(comparator);
	}

	/**
	 * Adds an element to the {@link TableViewer}
	 * @param element the ElementType to add
	 */
	public void addElement(ElementType element) {
		tableViewer.add(element);
	}

	/**
	 * Removes an element from the {@link TableViewer}
	 * @param element the ElementType to remove
	 */
	public void removeElement(ElementType element) {
		tableViewer.remove(element);
	}

//	private ListenerList doubleClickListenerBackup;
	private ListenerList selectionChangedListenerBackup;
	private ListenerList postSelectionListenerBackup;
	private CellEditor[] cellEditors;
	private static final ListenerList emptyList = new ListenerList();

	/**
	 * Sets the flag whether the table may actively be modified or if it is "read-only".
	 * @param editable shall table be modifiable?
	 */
	public void setEditable(boolean editable)
	{
		if (editable == isEditable())
			return;

		this.editable = editable;

		if (editable)
		{ // getting active again
//			doubleClickListeners = doubleClickListenerBackup;
			selectionChangedListeners = selectionChangedListenerBackup;
			postSelectionChangedListeners = postSelectionListenerBackup;
			getTableViewer().setCellEditors(cellEditors);
			cellEditors = null;
//			doubleClickListenerBackup = null;
			selectionChangedListenerBackup = null;
			postSelectionListenerBackup = null;
		}
		else
		{ // becoming inactive
//			doubleClickListenerBackup = doubleClickListeners;
			selectionChangedListenerBackup = selectionChangedListeners;
			postSelectionListenerBackup = postSelectionChangedListeners;
			cellEditors = getTableViewer().getCellEditors();
			if (cellEditors != null)
				getTableViewer().setCellEditors(new CellEditor[cellEditors.length]);

//			doubleClickListeners = emptyList;
			selectionChangedListeners = emptyList;
			postSelectionChangedListeners = emptyList;
		}
	}

	/**
	 * Returns whether the table can actively be modified or not.
	 * @return whether the table can actively be modified or not.
	 */
	public boolean isEditable()
	{
		return editable;
	}

	private ListenerList postSelectionChangedListeners = new ListenerList();
	private ISelectionChangedListener postSelectionChangedListener = new ISelectionChangedListener()
	{
		@Override
		public void selectionChanged(final SelectionChangedEvent event)
		{
			Object[] listeners = postSelectionChangedListeners.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
				final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
				SafeRunnable.run(new SafeRunnable() {
					public void run() {
						l.selectionChanged(event);
					}
				});
			}
		}
	};

	/**
	 * @param listener
	 * @see org.eclipse.jface.viewers.StructuredViewer#addPostSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addPostSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (postSelectionChangedListeners.isEmpty())
		{
			postSelectionChangedListeners.add(postSelectionChangedListener);
		}
		postSelectionChangedListeners.add(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.jface.viewers.StructuredViewer#removePostSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removePostSelectionChangedListener(ISelectionChangedListener listener)
	{
		postSelectionChangedListeners.remove(listener);
	}
}
