package org.nightlabs.base.ui.composite;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.nightlabs.base.ui.action.ViewActionDelegateWrapperAction;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;

/**
 * A top-level class for {@link XComposite}s that may want to use our efficient context-menu management methodologies.
 *
 * @author khaireel at nightlabs dot de
 */
public abstract class ContextMenuReadyComposite extends XComposite {
	/**
	 * Contains instances of {@link IContributionItem}s, {@link IAction}s, and {@link IViewActionDelegate}s.
	 * In addition, the (menu) items in this list must be ordered in accordance to 'first-available-default' priority;
	 * i.e. if they are so set, then they can automatically be used in the double-click behavioural context. See notes 2010.03.08. Kai.
	 */
	private List<Object> priorityOrderedContextMenuContributions;
	
	/**
	 * A useful feature to have when implementing this class as a Tree.
	 * @see AbstractTreeComposite
	 */
	private DrillDownAdapter drillDownAdapter;

	/**
	 * Initialises the context-menu of this {@link XComposite}.
	 * @param drillDownAdapter used mainly for classes extending the {@link AbstractTreeComposite}. Set this to null to ignore the drillDownAdapter.
	 */
	protected void createContextMenu(DrillDownAdapter drillDownAdapter, Control parent) {
		createContextMenu(parent);
		
		// Instantiate the drillDownAdapter, if one is supplied. We shall ignore this later on if the value is null.
		this.drillDownAdapter = drillDownAdapter; // <-- Special navigation menus, used mainly in Trees.
	}
	
	/**
	 * Creates a default context menu without a {@link DrillDownAdapter}.
	 */
	protected void createContextMenu(Control parent) {
		// General context-menu setup.
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				ContextMenuReadyComposite.this.fillContextMenu(manager);
			}
		});
		
		// Attach the menu to the control.
		Menu menu = menuMgr.createContextMenu(parent);
		parent.setMenu(menu);
	}
	
	/**
	 * Prepares the menu items to display when requested by the UI.
	 * Extended classes may want to register (or prepare) their own 'dynamic' menu-items through here without compromising on the framework.
	 */
	protected void fillContextMenu(IMenuManager manager) {
		if (priorityOrderedContextMenuContributions != null) {
			for (Object contextMenuContribution : priorityOrderedContextMenuContributions) {
				if (contextMenuContribution instanceof IContributionItem)
					manager.add((IContributionItem) contextMenuContribution);
				else if (contextMenuContribution instanceof IAction)
					manager.add((IAction) contextMenuContribution);
				else
					throw new IllegalStateException("How the hell got an instance of " + (contextMenuContribution == null ? "null" : contextMenuContribution.getClass()) + " in the contextMenuContributions list?!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		if (drillDownAdapter != null) {
			manager.add(new Separator()); // <-- This separates the above default menu-items from the drillDownAdapter's own navigational menus.
			drillDownAdapter.addNavigationActions(manager);
		}

		// Other plug-ins can contribute their actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	

	// ----------------------------------------------------------------------------------- || -------------------------------------------->>
	// [Section] Integrating the menu setup.
	// ----------------------------------------------------------------------------------- || -------------------------------------------->>
	/**
	 * Initialises the set of priorityOrderedContextMenuContributions by blending them into the given {@link ISelectionProvider};
	 * i.e. mainly, this controls the UI's enabled (or disabled) state for which ever (context) item has been selected.
	 * Call this once, only when we are ready with all the menu items we want. 
	 * 
	 * This also sets up the double-click behaviour, where in this setup, we assume that the (menu) items registered in here
	 * has been ordered in accordance to 'first-available-default' priority, this in turn will
	 * make them to be automatically used in this double-click context. See notes 2010.03.08. Kai.
	 *
	 * See first independent application usage in org.nightlabs.jfire.personrelation.ui.AbstractPersonRelationTreeView.
	 * 
	 * @return an {@link IDoubleClickListener} ready for use with the registered (priority-ordered) menu-items. 
	 */
	public IDoubleClickListener integratePriorityOrderedContextMenu(ISelectionProvider selectionProvider) {
		List<Object> orderedContextMenuContributions = getPriorityOrderedContextMenuContributions();
		if (orderedContextMenuContributions == null || orderedContextMenuContributions.isEmpty())
			return null;

		// On selection changes.
		selectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty())
					return;

				for (Object menuItem : getPriorityOrderedContextMenuContributions()) {
					if (menuItem instanceof IViewActionDelegate)
						((IViewActionDelegate) menuItem).selectionChanged((IAction) menuItem, event.getSelection());
				}
			}
		});

		// On double-click: 'first-available-default' priority execution.
		return new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (event.getSelection().isEmpty())
					return;

				for (Object menuItem : getPriorityOrderedContextMenuContributions())
					if (menuItem instanceof IAction) {
						IAction menuAction = (IAction) menuItem;
						if (menuAction.isEnabled()) {
							menuAction.run();
							return;
						}
					}
			}
		};
	}
	
	

	// ----------------------------------------------------------------------------------- || -------------------------------------------->>
	// [Section] Access to the menu-items.
	// ----------------------------------------------------------------------------------- || -------------------------------------------->>
	/**
	 * Adds an {@link IContributionItem} to the priority-ordered context menu contribution items.
	 */
	public void addContextMenuContribution(IContributionItem contributionItem) {
		if (priorityOrderedContextMenuContributions == null)
			priorityOrderedContextMenuContributions = new LinkedList<Object>();

		priorityOrderedContextMenuContributions.add(contributionItem);
	}

	/**
	 * Adds an {@link IAction} to the priority-ordered context menu contribution items.
	 */
	public void addContextMenuContribution(IAction action) {
		if (priorityOrderedContextMenuContributions == null)
			priorityOrderedContextMenuContributions = new LinkedList<Object>();

		priorityOrderedContextMenuContributions.add(action);
	}

	/**
	 * Adds an {@link IViewActionDelegate} to the priority-ordered context menu contribution items.
	 */
	public void addContextMenuContribution(IViewPart view, IViewActionDelegate actionDelegate, String id, String text, ImageDescriptor imageDescriptor) {
		IAction action = new ViewActionDelegateWrapperAction(view, actionDelegate, id, text, imageDescriptor);
		addContextMenuContribution(action); // <-- Adds to the current framework as an IAction.
	}

	/**
	 * @return the list of priority-ordered context menu contribution items.
	 */
	protected List<Object> getPriorityOrderedContextMenuContributions() { return priorityOrderedContextMenuContributions; }


	
	// ----------------------------------------------------------------------------------- || -------------------------------------------->>
	// [Section] Known constructors of the super class, used by the two composites; AbstractTreeComposite and AbstractTableComposite.
	// ----------------------------------------------------------------------------------- || -------------------------------------------->>
	public ContextMenuReadyComposite(Composite parent, int style) { super(parent, style); }
	public ContextMenuReadyComposite(Composite parent, int style, LayoutMode layoutMode) { super(parent, style, layoutMode); }
	public ContextMenuReadyComposite(Composite parent, int style, LayoutMode layoutMode, int cols) { super(parent, style, layoutMode, cols); }
	public ContextMenuReadyComposite(Composite parent, int style, LayoutDataMode layoutDataMode) { super(parent, style, layoutDataMode); } 
	public ContextMenuReadyComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) { super(parent, style, layoutMode, layoutDataMode); }
	public ContextMenuReadyComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode, int cols) { super(parent, style, layoutMode, layoutDataMode, cols); }
}
