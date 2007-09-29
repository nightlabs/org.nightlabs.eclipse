package org.nightlabs.base.selection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public class SelectionProviderProxy
extends SelectionProvider
implements ISelectionSupport
{
	private Set<ISelectionProvider> realSelectionProviders = new HashSet<ISelectionProvider>();

	public boolean addRealSelectionProvider(ISelectionProvider selectionProvider)
	{
		boolean res = realSelectionProviders.add(selectionProvider);
		if (res)
			selectionProvider.addSelectionChangedListener(selectionChangedListener);

		return res;
	}
	public boolean removeRealSelectionProvider(ISelectionProvider selectionProvider)
	{
		boolean res = realSelectionProviders.remove(selectionProvider);
		if (res)
			selectionProvider.removeSelectionChangedListener(selectionChangedListener);

		return res;
	}

	private ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event)
		{
			setSelection(event.getSelection());
			fireSelectionChangedEvent();
		}
	};

	@Override
	public void addSelectedObject(Object o)
	{
		super.addSelectedObject(o);
	}

	@Override
	public void addSelectedObjects(Collection objects)
	{
		super.addSelectedObjects(objects);
	}

	@Override
	public void clearSelection()
	{
		super.clearSelection();
	}

	@Override
	public boolean contains(Object o)
	{
		return super.contains(o);
	}

	@Override
	public IStructuredSelection getStructuredSelection()
	{
		return super.getStructuredSelection();
	}

	@Override
	public void removeSelectedObject(Object o)
	{
		super.removeSelectedObject(o);
	}

	@Override
	public void removeSelectedObjects(Collection objects)
	{
		super.removeSelectedObjects(objects);
	}

	@Override
	public void setSelection(Collection selectedObjects)
	{
		super.setSelection(selectedObjects);
	}

	public StructuredSelection getSelectedObjects()
	{
		return (StructuredSelection) getStructuredSelection();
	}
}
