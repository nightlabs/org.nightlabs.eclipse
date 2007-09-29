/**
 * 
 */
package org.nightlabs.base.action.registry.editor;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.nightlabs.base.action.ISelectionAction;
import org.nightlabs.base.action.IUpdateActionOrContributionItem;
import org.nightlabs.base.action.IWorkbenchPartAction;
import org.nightlabs.base.action.registry.AbstractActionRegistry;
import org.nightlabs.base.action.registry.ActionDescriptor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class XEditorActionBarContributor 
extends EditorActionBarContributor
implements ISelectionChangedListener
{
	private static final Logger logger = Logger.getLogger(XEditorActionBarContributor.class);
	
	private IEditorPart activeEditor;
	private AbstractActionRegistry actionRegistry;
	
	public XEditorActionBarContributor() {
	}
	
	private ISelectionListener selectionListener = new ISelectionListener(){	
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//			if (part.equals(activeEditor)) {
				updateSelectionSections(selection);
				updateActions();									
//			}
			logger.debug("selection changed"); //$NON-NLS-1$
		}	
	};
	
//	private ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener(){
		public void selectionChanged(SelectionChangedEvent event) {
			updateSelectionSections(event.getSelection());
			updateActions();	
			logger.debug("selection changed"); //$NON-NLS-1$
		}
//	};
	
//	public ISelectionChangedListener getSelectionChangedListener() {
//		return selectionChangedListener;
//	}
	
	public AbstractActionRegistry getActionRegistry() 
	{
		if (actionRegistry == null && getActiveEditor() != null) 
			actionRegistry = EditorActionBarContributorRegistry.sharedInstance().getActionRegistry(
					getActiveEditor().getEditorSite().getId());
		return actionRegistry;
	}
	
	protected void updateActions() 
	{
		if (getActionRegistry() != null) {
			for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
				if (actionDescriptor.getAction() instanceof IUpdateActionOrContributionItem) {
					boolean enabled = ((IUpdateActionOrContributionItem)actionDescriptor.getAction()).calculateEnabled();
					actionDescriptor.getAction().setEnabled(enabled);
//					actionDescriptor.setVisible(((IUpdateAction)actionDescriptor.getAction()).calculateVisible());
					
					if (logger.isDebugEnabled())
						logger.debug("enabled = "+enabled+" for action "+actionDescriptor.getAction().getId()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}			
		}
	}
	
	protected void updateSelectionSections(ISelection selection) 
	{
		if (getActionRegistry() == null)
			return;
		for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
			if (actionDescriptor.getAction() instanceof ISelectionAction) {
				ISelectionAction selectionAction = (ISelectionAction) actionDescriptor.getAction();
				selectionAction.setSelection(selection);
			}
		}		
	}
	
	@Override
	public void setActiveEditor(IEditorPart targetEditor) 
	{
		if (logger.isDebugEnabled())
			logger.debug("setActiveEditor called"); //$NON-NLS-1$
		
		if (activeEditor != null)
//			activeEditor.getSite().getPage().removeSelectionListener(selectionListener);
			if (activeEditor.getSite().getSelectionProvider() != null)
				activeEditor.getSite().getSelectionProvider().removeSelectionChangedListener(this);
		
		if (targetEditor == null) {
			activeEditor = null;
			return;
		}
		
		actionRegistry = null;
		IEditorPart oldEditorPart = activeEditor;
		super.setActiveEditor(targetEditor);
		this.activeEditor = targetEditor;
		
		if (getActionRegistry() != null) {
			for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
				if (actionDescriptor.getAction() instanceof IWorkbenchPartAction) {
					((IWorkbenchPartAction)actionDescriptor.getAction()).setActivePart(targetEditor);
				}
			}
		}
//		activeEditor.getSite().getPage().addSelectionListener(selectionListener);
//		updateSelectionSections(activeEditor.getSite().getPage().getSelection());
		if (activeEditor.getSite().getSelectionProvider() != null) {
			activeEditor.getSite().getSelectionProvider().addSelectionChangedListener(this);
			updateSelectionSections(activeEditor.getSite().getSelectionProvider().getSelection());			
		}

		updateActions();
		logger.debug("selectionListener added to activeEditor "+getActiveEditor().getEditorSite().getId()); //$NON-NLS-1$
		
		if (contributedToToolBar && !activeEditor.equals(oldEditorPart))
			contributeToToolBar(getActionBars().getToolBarManager());
		
		if (contributedToMenu && !activeEditor.equals(oldEditorPart))
			contributeToMenu(getActionBars().getMenuManager());		
	}
	
	public IEditorPart getActiveEditor() {
		return activeEditor;
	}
	
//	private boolean contributedToCoolBar = false;
	@Override
	public void contributeToCoolBar(ICoolBarManager coolBarManager) 
	{
//		contributedToCoolBar = true;
		updateActions();
		if (getActionRegistry() != null)
			getActionRegistry().contributeToCoolBar(coolBarManager);
	}
	
	private boolean contributedToToolBar = false;
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) 
	{
		contributedToToolBar = true;
		updateActions();
		if (getActionRegistry() != null)
			getActionRegistry().contributeToToolBar(toolBarManager);
	}
	
	private boolean contributedToMenu = false;
	@Override
	public void contributeToMenu(IMenuManager menuManager) 
	{
		contributedToMenu = true;
		updateActions();
		if (getActionRegistry() != null)
			getActionRegistry().contributeToMenuBar(menuManager);
	}
	
	@Override
	public void dispose() {
		if (activeEditor != null)
			activeEditor.getSite().getPage().removeSelectionListener(selectionListener);
		super.dispose();
	}
		
}
