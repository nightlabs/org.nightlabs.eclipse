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

package org.nightlabs.base.ui.entity.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.base.ui.composite.Fadeable;
import org.nightlabs.base.ui.editor.CommitableFormEditor;
import org.nightlabs.base.ui.entity.EntityEditorRegistry;
import org.nightlabs.base.ui.entity.editor.overview.OverviewPageFactory;
import org.nightlabs.base.ui.job.FadeableCompositeJob;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.progress.RCPProgressMonitor;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * A base class for entity editors. It provides
 * the method {@link #addPages()} using the EntityEditorRegistry
 * to add pages registered via extension point.
 * 
 * Each {@link EntityEditor} will have a {@link EntityEditorController} that
 * holds one {@link IEntityEditorPageController} for each page it displays.
 * 
 * This means that combined with {@link IEntityEditorPageFactory}s that return
 * controllers this class can be used as is and registered as editor (of course with an unique id).
 * All work will be delegated to the pages (visible representation) and the
 * page controller (model loading and saving) that were created by the pageFactory.
 * 
 * However {@link EntityEditor} can be subclassed to configure its appearance (title, tooltip).
 * 
 * @version $Revision$ - $Date$
 * 
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Daniel Mazurek - daniel[at]nightlabs[dot]de
 */
public class EntityEditor extends CommitableFormEditor
{
	/**
	 * This editor's controller, that will delegate
	 * loading and saving of the entity to the
	 * page controllers of the registered pages.
	 * 
	 */
	private EntityEditorController controller;
	
	/**
	 * A handler that can show a dialog when
	 * the editor is stale.
	 */
	private EntityEditorStaleHandler staleHandler;
	
	private String LAST_ACTIVE_PAGE_KEY_PREFIX = EntityEditor.class.getSimpleName() + ".lastPage."; //$NON-NLS-1$
	private String lastPageId = null;
	private IPageChangedListener pageChangedListener = new IPageChangedListener() {
		@Override
		public void pageChanged(PageChangedEvent event) {
			Object page = event.getSelectedPage();
			if (page instanceof IFormPage) {
				lastPageId = ((IFormPage) page).getId();
			}
		}
	};
	
	private boolean showOverviewPage = false;
	
	public EntityEditor()
	{	}
	
	/**
	 * This method creates the form pages of the editor and adds them to it. 
	 * Furthermore it adds page controllers for each registered page.
	 * <p>
	 * For storing the last active page of the editor a listener for page changes is added in
	 * this page change provider.
	 * </p>
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages()
	{
		try {
			List<EntityEditorPageSettings> pageSettings = getPageSettingsOrdered();
			for (EntityEditorPageSettings pageSetting : pageSettings) {
				IEntityEditorPageFactory factory = pageSetting.getPageFactory();
				IFormPage page = factory.createPage(this);
				controller.addPageController(page, factory);
				addPage(page);
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		addPageChangedListener(pageChangedListener);
		selectLastActivePage();
	}
	
	/**
	 * This method is called when the editor creates its pages.
	 * It should return all {@link EntityEditorPageSettings} for the pages to 
	 * be displayed in this editor.
	 * <p>
	 * This method uses the {@link EntityEditorRegistry} and the id of the editor
	 * to build the settings but it might be overridden in order to customise
	 * this behaviour. 
	 * </p>
	 * @return An ordered list of the {@link EntityEditorPageSettings} 
	 *         for the pages to be displayed in this editor.
	 */
	protected List<EntityEditorPageSettings> getPageSettingsOrdered() {
		List<EntityEditorPageSettings> pageSettingsOrdered = EntityEditorRegistry.sharedInstance().getPageSettingsOrdered(getEditorID());
		if (showOverviewPage) {
			EntityEditorPageSettings overviewPageSettings = new EntityEditorPageSettings(getEditorID(), 
					0, new OverviewPageFactory(), null, null, Messages.getString("org.nightlabs.base.ui.entity.editor.EntityEditor.page.overview.name")); //$NON-NLS-1$
			pageSettingsOrdered.add(0, overviewPageSettings);
		}
		return pageSettingsOrdered;
	}
	
	/**
	 * Get the editor id.
	 * @return The editor id
	 */
	public String getEditorID() {
		return getEditorSite().getId();
	}
	
	/**
	 * This method sets the active page of an editor.
	 * Therefore, the ID of the recently active page of an editor before closing it is utilized by the use
	 * of the preference store of the corresponding plugin. The ID has been saved in the store at the same time 
	 * the editor has been closed.
	 * <p>   
	 * In {@link ArticleContainerEditor} controls are not available in the case a page other than the first one is chosen as 
	 * active at the beginning. For this reason the FIRST page of this editor is always chosen as active, i.e. the last page ID 
	 * is not maintained.    
	 * </p>
	 */
	protected void selectLastActivePage() {
		if (!maintainLastPageID())
			return;
		String pageID = NLBasePlugin.getDefault().getPluginPreferences().getString(getLastPageIDPreferenceKey());
		if (pageID != null && !pageID.isEmpty()) {
			setActivePage(pageID);
		}
	}
	
	/**
	 * Determines if the ID of the last active page before closing a certain editor will be chosen as active page when 
	 * starting this editor again.
	 * <p>
	 * In {@link ArticleContainerEditor} control is not available in the case a page other than the first one is chosen as 
	 * active at the beginning. For this reason the FIRST page of this editor is always chosen as active, i.e. the last page ID 
	 * is not maintained.    
	 * </p>
	 * 
	 * @return true if the last page ID shall be maintained, otherwise false.
	 */
	protected boolean maintainLastPageID() {
		return true;
	}
	
	/**
	 * This method returns the key under which the last active page id
	 * will be stored in the preferences store.
	 * <p>
	 * The default implementation will include a prefix and the id of
	 * the current editor. Subclasses may override to change this key
	 * (for example to add more details about the editor instance).
	 * </p>
	 *
	 * @return The key under which the last active page id
	 * will be stored in the preferences store.
	 */
	protected String getLastPageIDPreferenceKey() {
		return LAST_ACTIVE_PAGE_KEY_PREFIX + getEditorID();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// Save as not supported by entity editor
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * This implementation additionally creates this editor's controller.
	 */
	@Override
	public void init(IEditorSite arg0, IEditorInput arg1) throws PartInitException {
		super.init(arg0, arg1);
		controller = new EntityEditorController(this);
		staleHandler = new EntityEditorStaleHandler(this);
	}

	private IRunnableWithProgress saveRunnable = new IRunnableWithProgress() {
		public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			controller.doSave(new ProgressMonitorWrapper(monitor));
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					editorDirtyStateChanged();
				}
			});
		}
	};

	/**
	 * {@inheritDoc}
	 * <p>
	 * In spite of the super implementation that checks the {@link IFormPage}s for their dirty state,
	 * this implementation asks the {@link EntityEditorController} associated with this Editor
	 * whether one of its {@link IEntityEditorPageControllerController}s is dirty.
	 * (See {@link EntityEditorController#hasDirtyPageControllers()})
	 * </p>
	 * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
	 */
	@Override
	public boolean isDirty() {
		boolean controllerDirty = controller != null ? controller.hasDirtyPageControllers() : false;
		return controllerDirty;
//		if (controllerDirty)
//			return true;
//		return super.isDirty();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * This implementation will start a job to save the
	 * editor. It will first let all pages commit and then
	 * call its controllers doSave() method. This will
	 * cause all page controllers to save their model.
	 * If the active page appears to be {@link Fadeable} it will
	 * be faded until the save operation is finished.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (controller != null)
			controller.populateDirtyPageControllers();
		super.doSave(monitor);
		
//		try {
//			saveRunnable.run(monitor);
//		} catch (Throwable t) {
//
//		}
		
		// FIXME: check why saving is not done when workbench is shutdown
		int active = getActivePage();
		Job saveJob = null;
		if (active >= 0) {
			IFormPage page = getFormPages()[active];
			if (page instanceof Fadeable)
				saveJob = new FadeableCompositeJob(Messages.getString("org.nightlabs.base.ui.entity.editor.EntityEditor.saveJob.name"), ((Fadeable)page), this) { //$NON-NLS-1$
					@Override
					protected IStatus run(ProgressMonitor monitor, Object source) throws Exception {
						try {
							saveRunnable.run(new RCPProgressMonitor(monitor));
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						return Status.OK_STATUS;
					}
			};
		}
		if (saveJob == null) {
			saveJob = new Job(Messages.getString("org.nightlabs.base.ui.entity.editor.EntityEditor.saveJob.name")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) {
					try {
						saveRunnable.run(getProgressMonitor());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					return Status.OK_STATUS;
				}
			};
		}
//		saveJob.setUser(true);
		saveJob.schedule();
	}
	
	/**
	 * Return the controller associated with this editor.
	 * The controller is created in {@link #init(IEditorSite, IEditorInput)}.
	 * 
	 * See {@link #getPageController(IFormPage)} on how to access a single page's controller.
	 * 
	 * @return The controller associated with this editor.
	 */
	public EntityEditorController getController() {
		return controller;
	}

	/**
	 * Get the stale handler for this editor.
	 * You can register {@link IEntityEditorPageStaleHandler}s with this
	 * handler that will be executed after being presented
	 * to the user.
	 * @return The stale handler for this editor.
	 */
	public EntityEditorStaleHandler getStaleHandler() {
		return staleHandler;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Additionally to its super implementation this implementation
	 * invokes {@link EntityEditorController#dispose()} on the controller
	 * associated with this editor.
	 * </p>
	 * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
	 */
	@Override
	public void dispose() {
		removePageChangedListener(pageChangedListener);
		if (lastPageId != null && !lastPageId.isEmpty()) {
			NLBasePlugin.getDefault().getPluginPreferences().setValue(getLastPageIDPreferenceKey(), lastPageId);
			NLBasePlugin.getDefault().savePluginPreferences();
		}
		super.dispose();
		if (controller != null)
			controller.dispose();
	}

	/**
	 * 
	 * @return all IFormPages attached to this editor.
	 */
	public List<IFormPage> getPages() {
//		List<IFormPage> pages = new LinkedList<IFormPage>();
//		for (IEntityEditorPageController pageController : controller.getPageControllers()) {
//			for (IFormPage pageControllerPage : pageController.getPages()) {
//				if (this == pageControllerPage.getEditor())
//					pages.add(pageControllerPage);
//			}
//		}
//		return pages;
		return Arrays.asList(getFormPages());
	}
	
	/**
	 * Returns the showOverviewPage.
	 * @return the showOverviewPage
	 */
	public boolean isShowOverviewPage() {
		return showOverviewPage;
	}

	/**
	 * Sets the showOverviewPage.
	 * @param showOverviewPage the showOverviewPage to set
	 */
	public void setShowOverviewPage(boolean showOverviewPage) {
		this.showOverviewPage = showOverviewPage;
	}
	
}
