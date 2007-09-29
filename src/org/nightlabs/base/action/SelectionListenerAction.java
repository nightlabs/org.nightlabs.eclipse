/**
 * 
 */
package org.nightlabs.base.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * This class represents an {@link Action} that listens to an {@link ISelectionProvider} and on each
 * {@link #selectionChanged(ISelection)} sets the enabled state according to 
 * {@link IUpdateActionOrContributionItem#calculateEnabled()}.
 * 
 * @author Marius Heinzmann [marius<at>NightLabs<dot>de]
 */
public abstract class SelectionListenerAction 
	extends SelectionProviderAction 
	implements IUpdateActionOrContributionItem 
{
	
	protected ISelection selection;
	
	public SelectionListenerAction(ISelectionProvider selectionProvider) {
		this(selectionProvider, ""); //$NON-NLS-1$
	}
	
	public SelectionListenerAction(ISelectionProvider selectionProvider, String text) {
		this(selectionProvider, text, null);
	}
	
	public SelectionListenerAction(ISelectionProvider selectionProvider, String text, ImageDescriptor image) {
		super(selectionProvider, text);
		setImageDescriptor(image);
	}

	/**
	 * Stores the selection from a selection event and sets the enabled state according to the 
	 * {@link IUpdateActionOrContributionItem#calculateEnabled()}. <br>
	 * 
	 * If you want to react to an {@link ISelection} or an {@link IStructuredSelection} you can override
	 * this method and {@link #selectionChanged(IStructuredSelection)}, respectively. 
	 * But remember to call this method first!
	 */
	@Override
	public void selectionChanged(ISelection selection) {
		this.selection = selection;
		setEnabled( calculateEnabled() );
	}
	
	@Override
	public void selectionChanged(IStructuredSelection selection) {
		selectionChanged((ISelection)selection);
	}
}
