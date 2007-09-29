package org.nightlabs.base.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public class SelectionProvider
implements ISelectionProvider
{
	private ListenerList selectionListeners = new ListenerList();

	public static enum CollectionType {
		hashSet,
		arrayList
	}

	public SelectionProvider()
	{
		this(CollectionType.hashSet);
	}

	public SelectionProvider(CollectionType collectionType)
	{
		this.collectionType = collectionType;
		switch (collectionType) {
			case hashSet:
				selectedObjects = new HashSet();
			break;
			case arrayList:
				selectedObjects = new ArrayList();
			break;
			default:
				throw new IllegalStateException("Unknown collectionType: " + collectionType); //$NON-NLS-1$
		}
	}

	private CollectionType collectionType;
	public CollectionType getCollectionType()
	{
		return collectionType;
	}

	private boolean fireSelectionChangedEventDeferred = false;
	private int selectionChangeDeferredCounter = 0;

	public void beginSelectionChange()
	{
		if (selectionChangeDeferredCounter == Integer.MAX_VALUE)
			throw new IllegalStateException("selectionChangeDeferredCounter has already reached its maximum value!"); //$NON-NLS-1$

		++selectionChangeDeferredCounter;
	}

	public void endSelectionChange()
	{
		if (selectionChangeDeferredCounter == 0)
			throw new IllegalStateException("endSelectionChange() called without a prior call to beginSelectionChange()!!!"); //$NON-NLS-1$

		--selectionChangeDeferredCounter;		

		if (selectionChangeDeferredCounter == 0 && fireSelectionChangedEventDeferred)
			fireSelectionChangedEvent();
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionListeners.add(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionListeners.remove(listener);
	}

	private Collection selectedObjects;

	public ISelection getSelection()
	{
		return getStructuredSelection();
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public void setSelection(ISelection selection)
	{
		if (selection == null) { // TODO unfortunately, it is not documented in the ISelectionProvider, whether this method must support null parameters - I simply do it - I hope it doesn't hurt ;-) Marco.
			clearSelection();
			return;
		}

		if (!(selection instanceof IStructuredSelection))
			throw new IllegalArgumentException("selection must be an instanceof IStructuredSelection, but is "+(selection == null ? null : selection.getClass().getName())+": " + selection); //$NON-NLS-1$ //$NON-NLS-2$

		structuredSelection = (IStructuredSelection) selection;
		selectedObjects.clear();
		selectedObjects.addAll(structuredSelection.toList());
		fireSelectionChangedEvent();
	}

	protected void fireSelectionChangedEvent()
	{
		if (selectionChangeDeferredCounter > 0) {
			fireSelectionChangedEventDeferred = true;
			return;
		}

		Object[] listeners = selectionListeners.getListeners();
		for (Object listener : listeners) {
			ISelectionChangedListener l = (ISelectionChangedListener) listener;
			l.selectionChanged(new SelectionChangedEvent(this, getSelection()));
		}
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	protected void addSelectedObject(Object o)
	{
		selectedObjects.add(o);
		structuredSelection = null;
		fireSelectionChangedEvent();
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	protected void addSelectedObjects(Collection objects)
	{
		selectedObjects.addAll(objects);
		structuredSelection = null;
		fireSelectionChangedEvent();
	}

	protected void clearSelection()
	{
		selectedObjects.clear();
		structuredSelection = null;
		fireSelectionChangedEvent();
	}

	protected boolean contains(Object o)
	{
		return selectedObjects.contains(o);
	}

	/**
	 * serves as a cache
	 */
	private IStructuredSelection structuredSelection;

	protected IStructuredSelection getStructuredSelection()
	{
		if (structuredSelection == null)
			structuredSelection = new StructuredSelection(selectedObjects.toArray());

		return structuredSelection;
	}

	protected void removeSelectedObject(Object o)
	{
		selectedObjects.remove(o);
		structuredSelection = null;
		fireSelectionChangedEvent();
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	protected void removeSelectedObjects(Collection objects)
	{
		selectedObjects.removeAll(objects);
		structuredSelection = null;
		fireSelectionChangedEvent();
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	protected void setSelection(Collection selectedObjects)
	{
		selectedObjects.clear();
		selectedObjects.addAll(selectedObjects);
		structuredSelection = null;
		fireSelectionChangedEvent();
	}

}
