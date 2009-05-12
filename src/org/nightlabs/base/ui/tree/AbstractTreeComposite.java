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

package org.nightlabs.base.ui.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.table.GenericInvertViewerSorter;
import org.nightlabs.eclipse.ui.treestate.StatableTree;
import org.nightlabs.eclipse.ui.treestate.TreeStateController;

/**
 * A composite with a {@link TreeViewer} to be used as base for tree-composites.
 * It will create a treeViewer and trigger callback-methods for its configuration
 * (see {@link #setTreeProvider(TreeViewer)}) and {@link #createTreeColumns(Tree)}.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractTreeComposite<ElementType>
extends XComposite
implements ISelectionProvider, StatableTree
{
	/**
	 * The internally used {@link TreeViewer}.
	 */
	private TreeViewer treeViewer;
	/**
	 * Default set of styles to use when constructing a single-selection viewer.
	 */
	public static int DEFAULT_STYLE_SINGLE = SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
	/**
	 * Default set of styles to use when constructing a multi-selection viewer.
	 */
	public static int DEFAULT_STYLE_MULTI = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL  | SWT.FULL_SELECTION;
	/**
	 * Default set of styles to use when constructing a single-selection viewer.
	 */
	public static int DEFAULT_STYLE_SINGLE_BORDER = SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER;
	/**
	 * Default set of styles to use when constructing a multi-selection viewer.
	 */
	public static int DEFAULT_STYLE_MULTI_BORDEER = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL  | SWT.FULL_SELECTION | SWT.BORDER;

	private boolean editable = true;
	private ListenerList doubleClickListenerBackup;
	private ListenerList selectionChangedListenerBackup;
	private ListenerList postSelectionListenerBackup;
	private CellEditor[] cellEditors;
	private static final ListenerList emptyList = new ListenerList();
	private boolean sortColumns = true;
	private ListenerList postSelectionChangedListeners = new ListenerList();
	private ListenerList selectionChangedListeners = new ListenerList();
	private ListenerList doubleClickListeners = new ListenerList();

	/**
	 * Convenience parameter with {@link #DEFAULT_STYLE_SINGLE}, a GridData,
	 * directly initialised and visible headers for the tree.
	 * @see #AbstractTreeComposite(Composite, int, boolean, boolean, boolean)
	 *
	 * @param parent The parent to use.
	 */
	public AbstractTreeComposite(Composite parent) {
		this(parent, DEFAULT_STYLE_SINGLE, true, true, true);
	}

	/**
	 * Convenience parameter with a GridData,
	 * directly initialised and visible headers for the tree.
	 * @see #AbstractTreeComposite(Composite, int, boolean, boolean, boolean)
	 *
	 * @param parent The parent to use.
	 * @param style the SWT style flag for the table
	 */
	public AbstractTreeComposite(Composite parent, int style) {
		this(parent, style, true, true, true);
	}

	/**
	 * Creates a new tree composite with the given parent.
	 * The init parameter controls whether or not the tree providers
	 * and colums should be configured already.
	 *
	 * @param parent The parent to use.
	 * @param init Whether to call {@link #init()}
	 */
	public AbstractTreeComposite(Composite parent, boolean init) {
		this(parent, DEFAULT_STYLE_SINGLE, true, init, true);
	}

	/**
	 * See {@link #AbstractTreeComposite(Composite, boolean)}. The other
	 * parameters are used to control the trees look.
	 *
	 * @param parent The parent to use.
	 * @param style The style to use for treeViewer. The style of the wrapping Composite will be SWT.NONE.
	 * @param setLayoutData Whether to set a LayoutData (of fill both) for the wrapping Composite.
	 * @param init Whether to call init directly.
	 * @param headerVisible Whether the header of the TreeViewer should be visible.
	 */
	public AbstractTreeComposite(Composite parent, int style, boolean setLayoutData, boolean init, boolean headerVisible)
	{
		this(parent, style, setLayoutData, init, headerVisible, true);
	}

	/**
	 * See {@link #AbstractTreeComposite(Composite, boolean)}. The other
	 * parameters are used to control the trees look.
	 *
	 * @param parent The parent to use.
	 * @param style The style to use for treeViewer. The style of the wrapping Composite will be SWT.NONE.
	 * @param setLayoutData Whether to set a LayoutData (of fill both) for the wrapping Composite.
	 * @param init Whether to call init directly.
	 * @param headerVisible Whether the header of the TreeViewer should be visible.
	 * @param sortColumns determines if the header is automatically sorted
	 */
	public AbstractTreeComposite(Composite parent, int style, boolean setLayoutData,
			boolean init, boolean headerVisible, boolean sortColumns)
	{
		super(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER, setLayoutData ? XComposite.LayoutDataMode.GRID_DATA : XComposite.LayoutDataMode.NONE);
		treeViewer = createTreeViewer(style);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.getTree().setHeaderVisible(headerVisible);
		TreeStateController.sharedInstance().registerTree(this);
		this.sortColumns = sortColumns;
		if (init)
			init();
	}

	/**
	 * Init calls {@link #setTreeProvider(TreeViewer)} and {@link #createTreeColumns(Tree)}
	 * with the appropriate parameters.
	 */
	public void init()
	{
		setTreeProvider(treeViewer);
		createTreeColumns(treeViewer.getTree());

		if (sortColumns)
		{
			for (int i=0; i<treeViewer.getTree().getColumns().length; i++) {
				TreeColumn treeColumn = treeViewer.getTree().getColumn(i);
				new TreeSortSelectionListener(treeViewer, treeColumn, new GenericInvertViewerSorter(i), SWT.UP);
			}
		}

		// Add selection listener to make the selection of an inactive table (! #isEditable()) impossible.
		// However the event will be propagated to the rest of the listeners if they were not added via
		// #addCheckStateChangedListener()!
		getTree().addSelectionListener(new SelectionAdapter()
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
	}

	protected TreeViewer createTreeViewer(int style) {
		TreeViewer tv = new TreeViewer(this, style);
		if ((style & SWT.VIRTUAL) != 0)
			tv.setUseHashlookup(true);

		return tv;
	}

	/**
	 * Set your content and label provider for the {@link TreeViewer}.
	 *
	 * @param treeViewer the TreeViewer which is internally used.
	 */
	public abstract void setTreeProvider(TreeViewer treeViewer);

	/**
	 * Add your columns here to the {@link Tree}.
	 *
	 * @param tree the SWT tree.
	 */
	public abstract void createTreeColumns(Tree tree);

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public Tree getTree() {
		if (treeViewer != null)
			return treeViewer.getTree();
		return null;
	}

	public void refresh() {
		treeViewer.refresh();
	}

	public void refresh(boolean updateLabels) {
		treeViewer.refresh(updateLabels);
	}

	public void refresh(Object element, boolean updateLabels) {
		treeViewer.refresh(element, updateLabels);
	}

	public void refresh(Object element) {
		treeViewer.refresh(element);
	}

	public Control getControl() {
		return treeViewer.getControl();
	}

	public Object getInput() {
		return treeViewer.getInput();
	}

	public ISelection getSelection() {
		return treeViewer.getSelection();
	}

	/**
	 * Selects the given elements in the list if they exist.
	 * @param elements the elements to be selected.
	 */
	public void setSelection(List<ElementType> elements, boolean reveal)
	{
		if (elements == null || elements.size() == 0)
			return;
		getTreeViewer().setSelection(new StructuredSelection(elements), true);
	}

	/**
	 * Sets and reveals the selection to the given elements.
	 *
	 * @param elements The elements to select.
	 * @see #setSelection(List, boolean)
	 */
	public void setSelection(List<ElementType> elements) {
		setSelection(elements, true);
	}

	/**
	 * Selects the given element in the tree. (Shortcut to #setSelection(List)).
	 * @param element the element to be selected
	 */
	public void setSelection(ElementType element)
	{
		getTreeViewer().setSelection(new StructuredSelection(element), true);
	}

	public void setInput(Object input) {
		// we only set the input when the viewers control is there and not disposed
		if (treeViewer != null && treeViewer.getControl() != null && !treeViewer.getControl().isDisposed())
			treeViewer.setInput(input);

		TreeStateController.sharedInstance().loadTreeState(getTree());
	}

	public void setSelection(ISelection selection)
	{
		treeViewer.setSelection(selection);
	}

	/**
	 * The tree normally holds a special type of node objects that contain the real objects.
	 * This method is used to intercept when the selection is read in order to return the
	 * real selection object.
	 * <p>
	 * The default implementation returns the given obj if it can be casted to the type the tree was parameterized with,
	 * it will return <code>null</code> otherwise.
	 * </p>
	 * <p>
	 * When overriding this method, you should return <code>null</code>, if the passed object <code>obj</code>
	 * cannot be transformed into an instance of <code>ElementType</code> (for example, if it's a temporary "loading data..." node).
	 * </p>
	 *
	 * @see #getSelectedElements()
	 * @see #getFirstSelectedElement()
	 *
	 * @param obj The viewers selection object.
	 * @return The selection object that should be passed as selection.
	 */
	protected ElementType getSelectionObject(Object obj)
	{
		// TODO maybe we should make this method abstract, since the tree holds almost always nodes and not the managed objects directly. Marco.
		// I think the base implementation is ok, as there are now already many usages that do not override this method, Alex
		try {
			ElementType elem = null;
			return this.naiveCast(elem, obj);
		} catch (ClassCastException e) {
			return null;
		}
	}

	/**
	 * Returns the first selected element.
	 * Note that the element returned here might not be
	 * of the (node)type of elements managed by this viewer.
	 * The result might have been replaced by an element extracted from the selected tree node.
	 *
	 * @return The (first) selected element or null.
	 */
	public ElementType getFirstSelectedElement() {
		if (getTree().getSelectionCount() >= 1) {
			for (int idx = 0; idx < getTree().getSelectionCount(); ++idx) {
				ElementType res = getSelectionObject(getTree().getSelection()[idx].getData());
				if (res != null)
					return res;
			}
		}
		return null;
	}

	/**
	 * Returns all selected elements in a Set.
	 * Note that the elements returned here might not be
	 * of the (node)type of elements managed by this viewer.
	 * The results might have been replaced by an element extracted from the selected tree nodes.
	 *
	 * @return All selected elements in a Set.
	 */
	public Set<ElementType> getSelectedElements() {
		TreeItem[] items = getTree().getSelection();
		Set<ElementType> result = new HashSet<ElementType>();
		for (int i = 0; i < items.length; i++) {
			ElementType e = getSelectionObject(items[i].getData());
			if (e != null)
				result.add(e);
		}
		return result;
	}

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
			doubleClickListeners = doubleClickListenerBackup;
			selectionChangedListeners = selectionChangedListenerBackup;
			postSelectionChangedListeners = postSelectionListenerBackup;
			getTreeViewer().setCellEditors(cellEditors);
			cellEditors = null;
			doubleClickListenerBackup = null;
			selectionChangedListenerBackup = null;
			postSelectionListenerBackup = null;
		}
		else
		{ // becoming inactive
			doubleClickListenerBackup = doubleClickListeners;
			selectionChangedListenerBackup = selectionChangedListeners;
			postSelectionListenerBackup = postSelectionChangedListeners;
			cellEditors = getTreeViewer().getCellEditors();
			if (cellEditors != null)
				getTreeViewer().setCellEditors(new CellEditor[cellEditors.length]);

			doubleClickListeners = emptyList;
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

	/**
	 * Maps from the actual check state changed listeners to the adapters used.
	 */
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
		getTree().addSelectionListener(selectionAdapter);
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

		getTree().removeSelectionListener(listenerAdapter);
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
	 * @see {@link TreeViewer#addSelectionChangedListener(ISelectionChangedListener)}.
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (selectionChangedListeners.isEmpty() && isEditable())
		{
			getTreeViewer().addSelectionChangedListener(selectionChangedListener);
		}

		selectionChangedListeners.add(listener);
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

//	private IDoubleClickListener doubleClickListener = new IDoubleClickListener()
//	{
//		@Override
//		public void doubleClick(final DoubleClickEvent event)
//		{
//			for (Object listener : doubleClickListeners.getListeners())
//			{
//				Object[] listeners = doubleClickListeners.getListeners();
//				for (int i = 0; i < listeners.length; ++i) {
//					final IDoubleClickListener l = (IDoubleClickListener) listeners[i];
//					SafeRunnable.run(new SafeRunnable() {
//						public void run() {
//							l.doubleClick(event);
//						}
//					});
//				}
//			}
//		}
//	};

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void addDoubleClickListener(IDoubleClickListener listener) {
		if (doubleClickListeners.isEmpty() && isEditable())
		{
			getTreeViewer().addDoubleClickListener(listener);
		}

		doubleClickListeners.add(listener);
	}

	/**
	 * Delegating method for {@link TableViewer}
	 */
	public void removeDoubleClickListener(IDoubleClickListener listener)
	{
		doubleClickListeners.remove(listener);
	}

	@Override
	public String getID() {
		return this.getClass().getName();
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	private <T> T naiveCast(T t, Object obj) {
		return (T) obj;
	}
}