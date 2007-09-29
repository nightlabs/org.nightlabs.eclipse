/**
 * 
 */
package org.nightlabs.base.entity.editor.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.nightlabs.base.action.ISelectionAction;
import org.nightlabs.base.action.IUpdateActionOrContributionItem;
import org.nightlabs.base.action.IWorkbenchPartAction;
import org.nightlabs.base.action.registry.AbstractActionRegistry;
import org.nightlabs.base.action.registry.ActionDescriptor;

/**
 * @author Daniel Mazurek <!-- daniel [AT] nightlabs [DOT] de -->
 *
 */
public class EntityEditorActionBarContributor 
extends MultiPageEditorActionBarContributor 
{	
	private Logger logger = Logger.getLogger(EntityEditorActionBarContributor.class);

	public EntityEditorActionBarContributor() {
		super();
	}
		
	private IFormPage activePage = null;
	private IFormPage oldActivePage = null;
	@Override
	public void setActivePage(IEditorPart activeEditor) 
	{
//		if (logger.isDebugEnabled())
//			logger.debug("setActivePage: "+activeEditor);
		
		actionRegistry = null;
		if (getActiveEditor() != null && getActiveEditor() instanceof FormEditor) {
			FormEditor formEditor = (FormEditor) getActiveEditor();
			oldActivePage = activePage;
			activePage = formEditor.getActivePageInstance();	
			updateSelectionSections(getActiveEditor().getSite().getPage().getSelection());
			updateUpdateActions();
			removeContributions();
			contributeAll();
		}
//		logger.debug("ActivePage: "+activePage);
	}
	public IFormPage getActivePage() {
		return activePage;
	}	
			
	private IEditorPart oldEditorPart = null;
	private IEditorPart activeEditor = null;
	public IEditorPart getActiveEditor() {
		return activeEditor;
	}
	
	@Override
	public void setActiveEditor(IEditorPart targetEditor) 
	{
//		if (logger.isDebugEnabled())
//			logger.debug("setActiveEditor: "+targetEditor);
		
		if (activeEditor != null)
			activeEditor.getSite().getPage().removeSelectionListener(selectionListener);
				
		if (targetEditor == null) {
			activeEditor = null;
			return;
		}
		
		actionRegistry = null;
		oldEditorPart = activeEditor;
		super.setActiveEditor(targetEditor);
		this.activeEditor = targetEditor;
		activeEditor.getSite().getPage().addSelectionListener(selectionListener);
//		if (logger.isDebugEnabled())
//			logger.debug("selectionListener added to activeEditor "+activeEditor.getEditorSite().getId());		

		updateWorkbenchPartActions();
		setActivePage(this.activeEditor);
	}
		
	protected void removeContributions() 
	{
		if (contributedToToolBar) {
			getActionBars().getToolBarManager().removeAll();
			getActionBars().getToolBarManager().update(true);
		}
		
		if (contributedToMenu) {			
			getActionBars().getMenuManager().removeAll();
			getActionBars().getMenuManager().updateAll(true);
		}			
		
//		if (contributedToCoolBar && getActionBars() instanceof IActionBars2) {
//			((IActionBars2)getActionBars()).getCoolBarManager().removeAll();
//			((IActionBars2)getActionBars()).getCoolBarManager().update(true);
//		}
		
		if (getActivePage() != null && !getActivePage().getPartControl().isDisposed()) {
			IToolBarManager toolBarManager = getActivePage().getManagedForm().getForm().getToolBarManager();
			toolBarManager.removeAll();
			toolBarManager.update(true);
		}
	}
	
	protected void contributeAll() 
	{
		if (contributedToToolBar && !getActiveEditor().equals(oldEditorPart))
			contributeToToolBar(getActionBars().getToolBarManager());
	
		if (contributedToMenu && !getActiveEditor().equals(oldEditorPart))
			contributeToMenu(getActionBars().getMenuManager());
				
//		if (getActionBars() instanceof IActionBars2 && contributedToCoolBar && !activeEditor.equals(oldEditorPart))
//			contributeToCoolBar(((IActionBars2)getActionBars()).getCoolBarManager());
		
		if (getActivePage() != null) {
			IToolBarManager toolBarManager = getActivePage().getManagedForm().getForm().getToolBarManager();
			contributeToToolBar(toolBarManager);
		}
	}
	
	private AbstractActionRegistry actionRegistry = null;
	public AbstractActionRegistry getActionRegistry() 
	{
		if (actionRegistry == null && getActiveEditor() != null && getActivePage() != null) {
			actionRegistry = EntityEditorActionBarContributorRegistry.sharedInstance().getActionRegistry(
					getActiveEditor().getEditorSite().getId(), 
					getActivePage().getClass().getName());				
		}
//		if (logger.isDebugEnabled())
//			logger.debug(actionRegistry != null ? "actionRegistry != null" : "actionRegistry == null");
		
		return actionRegistry;
	}
	
	private ISelectionListener selectionListener = new ISelectionListener(){	
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//			if (part.equals(activeEditor)) {
				updateSelectionSections(selection);
				updateUpdateActions();									
//			}
//			logger.debug("selection changed");
		}	
	};	
	
	protected void updateUpdateActions() 
	{
		if (getActionRegistry() != null) {
			for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
				if (actionDescriptor.getAction() instanceof IUpdateActionOrContributionItem) {
					boolean enabled = ((IUpdateActionOrContributionItem)actionDescriptor.getAction()).calculateEnabled();
					actionDescriptor.getAction().setEnabled(enabled);
//					actionDescriptor.setVisible(true);
					actionDescriptor.setVisible(((IUpdateActionOrContributionItem)actionDescriptor.getAction()).calculateVisible());
					
//					if (logger.isDebugEnabled())
//						logger.debug("enabled = "+enabled+" for action "+actionDescriptor.getAction().getId());
				}
			}			
		}
	}
	
	protected void updateSelectionSections(ISelection selection) 
	{
		if (getActionRegistry() != null) {
			for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
				if (actionDescriptor.getAction() instanceof ISelectionAction) {
					ISelectionAction selectionAction = (ISelectionAction) actionDescriptor.getAction();
					selectionAction.setSelection(selection);
//					if (logger.isDebugEnabled())
//						logger.debug("selection changed for action "+actionDescriptor.getAction().getId());					
				}
			}			
		}
	}
	
	protected void updateWorkbenchPartActions() 
	{
		if (getActionRegistry() != null) {
			for (ActionDescriptor actionDescriptor : getActionRegistry().getActionDescriptors()) {
				if (actionDescriptor.getAction() instanceof IWorkbenchPartAction) {
					((IWorkbenchPartAction)actionDescriptor.getAction()).setActivePart(activeEditor);
//					if (logger.isDebugEnabled())
//						logger.debug("activePart changed for action "+actionDescriptor.getAction().getId());										
				}
			}			
		}
	}
	
	private boolean contributedToCoolBar = false;
	@Override
	public void contributeToCoolBar(ICoolBarManager coolBarManager) 
	{
		contributedToCoolBar = true;
//		updateActions();
		if (getActionRegistry() != null) {
			int visibleItems = getActionRegistry().contributeToCoolBar(coolBarManager);
//			logger.debug("contributeToCoolBar "+visibleItems+" visibleItems!");
		}
	}
	
	private boolean contributedToToolBar = false;
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) 
	{
		contributedToToolBar = true;
//		updateActions();
		if (getActionRegistry() != null) {
			int visibleItems = getActionRegistry().contributeToToolBar(toolBarManager);
//			logger.debug("contributeToToolBar "+visibleItems+" visibleItems!");
		}		
	}
	
	private boolean contributedToMenu = false;
	@Override
	public void contributeToMenu(IMenuManager menuManager) 
	{
		contributedToMenu = true;
//		updateActions();
		if (getActionRegistry() != null) {
			int visibleItems = getActionRegistry().contributeToMenuBar(menuManager);
//			logger.debug("contributeToMenu "+visibleItems+" visibleItems!");
		}
	}
	
	@Override
	public void dispose() 
	{
		if (activeEditor != null)
			activeEditor.getSite().getPage().removeSelectionListener(selectionListener);
		
		removeContributions();		
//		logger.debug("Dispose");		
		super.dispose();
	}	
}
