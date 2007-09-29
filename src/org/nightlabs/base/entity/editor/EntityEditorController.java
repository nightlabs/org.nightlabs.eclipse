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

package org.nightlabs.base.entity.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.entity.editor.EntityEditorPageController.LoadJob;
import org.nightlabs.base.resource.Messages;

/**
 * <p>Controller used by {@link EntityEditor} to hold the
 * {@link IEntityEditorPageController} for the displayed pages.</p>
 * 
 * <p>In its default implementation an {@link EntityEditor} will call
 * its controller's {@link #doSave(IProgressMonitor)} method
 * when it needs to be saved. The controller will then delegate to all
 * it's registered {@link IEntityEditorPageController}s.</p>
 * 
 * <p>Additionally {@link EntityEditorController} provides a pool of Jobs
 * that will be scheduled by the controller taking care that not more than 
 * a configurable number of jobs run simultaneously.</p> 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class EntityEditorController
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EntityEditorController.class);

	/**
	 * The editor this controller is linked to.
	 */
	private EntityEditor editor;
	
	/**
	 * Map to hold the page controllers for the pages of this controller
	 * editor. All work of this controller (load save get) will be delegated
	 * to the page controllers. The key of the controllers is the pageID of
	 * the pageFactory registered by extension-point.
	 * <p>
	 * key: String pageID<br/>
	 * value: IEntityEditorPageController controller
	 * </p>
	 */
	private Map<String, IEntityEditorPageController> pageID2pageController = new HashMap<String, IEntityEditorPageController>();

	/**
	 * Map to hold the controllers organized by their IDs.
	 * <p>
	 * key: String pageControllerID (as returned by {@link IEntityEditorPageController#getControllerID()})<br/>
	 * value: IEntityEditorPageController controller
	 * </p>
	 */
	private Map<String, IEntityEditorPageController> controllerID2pageController = new HashMap<String, IEntityEditorPageController>();

	private Map<Class<?>, Set<IEntityEditorPageController>> class2pageControllers = new HashMap<Class<?>, Set<IEntityEditorPageController>>();

//	/**
//	 * Reverse lookup map for {@link #pageID2pageController}.
//	 */
//	private Map<IEntityEditorPageController, Collection<IFormPage>> controllerPages = new HashMap<IEntityEditorPageController, Collection<IFormPage>>();
	
	private List<IEntityEditorPageController> dirtyPageControllers = new LinkedList<IEntityEditorPageController>();
	
	/**
	 * Currently the maximum number of simultaneously running jobs is configured with this constant.
	 * The current value is 3.
	 */
	public static final int MAX_RUNNING_JOB_COUNT = 3;
	
	/**
	 * Map of one job per page controller that could be scheduled in background. 
	 * The number of allowed simultaneously running jobs can be configured by {@link #MAX_RUNNING_JOB_COUNT}.
	 * The jobs will be sorted by the index of the IFormPage associated to the page controller
	 * given as key.
	 */
	private Map<IEntityEditorPageController, Job> loadJobPool = new TreeMap<IEntityEditorPageController, Job>(
			new Comparator<IEntityEditorPageController>() {
				public int compare(IEntityEditorPageController o1, IEntityEditorPageController o2) {
					Collection<IFormPage> pages1 = o1.getPages();
					Collection<IFormPage> pages2 = o2.getPages();
					IFormPage page1 = pages1 == null ? null : pages1.iterator().next();
					IFormPage page2 = pages2 == null ? null : pages2.iterator().next();
					if (page1 == null || page2 == null)
						return 0;
					return new Integer(page1.getIndex()).compareTo(page2.getIndex());
				}
			}
		);
	
	/**
	 * Set of jobs that were in the pool and already scheduled.
	 */
	private Set<Job> processedJobs = new HashSet<Job>();
	/**
	 * Counter for scheduled and unfinished (un-done()) jobs.
	 */
	private int runningJobs = 0;
	
	/**
	 * Create an instance of this controller for
	 * an {@link EntityEditor} and load the data.
	 */
	public EntityEditorController(EntityEditor editor)
	{
		this.editor = editor;
		this.editor.addPropertyListener(dirtyStateListener);
	}

	/**
	 * Add a new page controller to this editor controller.
	 * The controller can later be referenced by the given page.
	 * If the given pageFactory does not return a valid page controller
	 * nothing will be done.
	 * 
	 * @param page The page the controller should be created for.
	 * @param pageFactory The factory the controller for the page should be created from.
	 */
	public void addPageController(IFormPage page, IEntityEditorPageFactory pageFactory) {
		IEntityEditorPageController pageController = pageFactory.createPageController(editor);
		if (pageController == null || page == null)
			return;
		pageController.addPage(page);
//		editor.addPropertyListener(dirtyStateListener);
//		page.addPropertyListener(dirtyStateListener);

		pageController.setEntityEditorController(this);
		pageID2pageController.put(page.getId(), pageController);
		controllerID2pageController.put(pageController.getControllerID(), pageController);

		addPageControllerToClassMap(pageController.getClass(), pageController);

//		Collection<IFormPage> controllerPageCollection = controllerPages.get(pageController);
//		if (controllerPageCollection == null) {
//			controllerPageCollection = new HashSet<IFormPage>();
//			controllerPages.put(pageController, controllerPageCollection);
//		}
//		controllerPageCollection.add(page);
	}

	private void addPageControllerToClassMap(Class<?> clazz, IEntityEditorPageController pageController)
	{
		while (clazz != null) {
			Set<IEntityEditorPageController> controllers = class2pageControllers.get(clazz);
			if (controllers == null) {
				controllers = new HashSet<IEntityEditorPageController>();
				class2pageControllers.put(clazz, controllers);
			}
			controllers.add(pageController);

			Class<?>[] interfaces = clazz.getInterfaces();
			for (Class<?> interfaze : interfaces) {
				addPageControllerToClassMap(interfaze, pageController);
			}

			clazz = clazz.getSuperclass();
		}
	}

	private IPropertyListener dirtyStateListener = new IPropertyListener(){
		public void propertyChanged(Object source, int propId) {
			if (EditorPart.PROP_DIRTY == propId) {
				if (EntityEditorController.this.isSaving()) {
					EntityEditorController.this.unsetSavingState();
					return;
				}
				
				if (source instanceof EntityEditor) {
					EntityEditor editor = (EntityEditor) source;
//					if (editor.isDirty()) {
					IFormPage page = editor.getActivePageInstance();						
					IEntityEditorPageController pageController = pageID2pageController.get(page.getId());
					if (pageController != null) {
						logger.debug("pageControler.markDirty() for page "+page.getId()); //$NON-NLS-1$
						pageController.markDirty();
					}		
//					}
				}				
			}
		}
	};
	
	/**
	 * Returns the page controller registered to the given page.
	 * If no page controller was linked or none could be found <code>null</code>
	 * will be returned.
	 * 
	 * @param page The page a controller should be searched for.
	 * @return The page controller registered to the given page or <code>null</code> if none found.
	 */
	public IEntityEditorPageController getPageController(IFormPage page) {
		return pageID2pageController.get(page.getId());
	}

	/**
	 * @param pageID The id of the page for which a controller is desired.
	 * @return The page controller registered to the specified page or <code>null</code> if none found.
	 */
	public IEntityEditorPageController getPageControllerByPageID(String pageID) {
		return pageID2pageController.get(pageID);
	}
	/**
	 * @param controllerID The id of the controller. 
	 * @return
	 */
	public IEntityEditorPageController getPageControllerByControllerID(String controllerID) {
		return controllerID2pageController.get(controllerID);
	}

//	/**
//	 * Return the pages the given controller is linked to.
//	 * 
//	 * @param pageController The page controller to search a page for.
//	 * @return The pages the given controller is linked to.
//	 */
//	protected Collection<IFormPage> getPages(IEntityEditorPageController pageController) {
//		return controllerPages.get(pageController);
//	}

	/**
	 * Returns a collection of all pageControllers associated with this'
	 * editor {@link EntityEditorController}.
	 * 
	 * @return All page controllers.
	 */
	public Collection<IEntityEditorPageController> getPageControllers() {
		return controllerID2pageController.values();
	}

//	/**
//	 * Returns the current pageID2pageController for this editorControllers.
//	 * 
//	 * @return The current pageID2pageController for this editorControllers.
//	 */
//	protected Map<String, IEntityEditorPageController> getPageID2pageController() {
//		return pageID2pageController;
//	}

	/**
	 * Adds a new job to the pool, that might be scheduled instantly or 
	 * some time later, depending on the numer of already running jobs.
	 * 
	 * This method synchronized the job pool so other modifications
	 * must wait.
	 * 
	 * @param pageController The pageController the job to add is linked to.
	 * @param loadJob The job to add.
	 */
	public void putLoadJob(IEntityEditorPageController pageController, Job loadJob) {
		synchronized (loadJobPool) {
			loadJobPool.put(pageController, loadJob);		
			loadJob.addJobChangeListener(loadJobChangeListener);
			runPossibleJobs();
		}
	}
	
	/**
	 * Remove and return the job linked to the given page controller from
	 * the pool. Note that the job returned might be running (or finished) already.
	 * If the job is currently running (checked by the isAlive() method of its thread)
	 * the counter of running jobs will be decremented and 
	 * 
	 * @param pageController
	 * @return
	 */
	public Job popLoadJob(IEntityEditorPageController pageController) {
		synchronized (loadJobPool) {
			Job loadJob = loadJobPool.get(pageController);
			if (loadJob != null) {
				loadJobPool.remove(pageController);
				loadJob.removeJobChangeListener(loadJobChangeListener);				
				if (loadJob.getThread() != null && loadJob.getThread().isAlive()) {
					runningJobs--;
					runPossibleJobs();
				}
			}
			return loadJob;
		}
	}

	/**
	 * Schedules as many jobs that were not already scheduled
	 * and allowed according to the setting of maximal simultanous jobs.  
	 */
	private void runPossibleJobs() {
		synchronized (loadJobPool) {
			for (Job loadJob : loadJobPool.values()) {
				if (runningJobs >= MAX_RUNNING_JOB_COUNT)
					return;
				if (processedJobs.contains(loadJob))
					continue;
				loadJob.schedule();
				runningJobs++;
				processedJobs.add(loadJob);
			}
		}
	}
	
	/**
	 * Job listener to track finished jobs. 
	 */
	private IJobChangeListener loadJobChangeListener = new IJobChangeListener() {
		public void aboutToRun(IJobChangeEvent event) {
		}
		public void awake(IJobChangeEvent event) {
		}
		public void done(IJobChangeEvent event) {
			runningJobs--;
			runPossibleJobs();
			Job job = event.getJob();
			if (job != null && (job instanceof LoadJob)) {
				LoadJob loadJob = (LoadJob) job;
				if (!loadJob.isLoaded() && loadJob.getLoadException() != null)
					throw new RuntimeException(loadJob.getLoadException());
			}
		}
		public void running(IJobChangeEvent event) {
		}
		public void scheduled(IJobChangeEvent event) {
		}
		public void sleeping(IJobChangeEvent event) {
		}
	};
	
	/**
	 * Does nothing at the moment.
	 * @param monitor The progress monitor to use.
	 */
	public void doLoad(IProgressMonitor monitor)
	{
	}
	
	/**
	 * Iterates through all IFormPages, and if a page is dirty the corresponding controller is added
	 * to the dirtyPageControllers 
	 */
	public void populateDirtyPageControllers() 
	{
		this.dirtyPageControllers.clear();
//		for (Entry<IEntityEditorPageController, Collection<IFormPage>> entry : controllerPages.entrySet()) {
//			IEntityEditorPageController controller = entry.getKey();
		for (IEntityEditorPageController controller : getPageControllers()) {
			if (controller.isDirty()) {
				dirtyPageControllers.add(controller);
			}			
		}
	}

	/**
	 * @return <code>true</code> if at least one of the {@link IEntityEditorPageController}s associated
	 * with this controller is dirty, <code>false</code> otherwise.
	 */
	public boolean hasDirtyPageControllers() {
//		for (IEntityEditorPageController controller : controllerPages.keySet()) {
		for (IEntityEditorPageController controller : getPageControllers()) {
			if (controller.isDirty())
				return true;
		}
		return false;
	}

	private boolean saving = false;

	public boolean isSaving() {
		return saving;
	}

	public void unsetSavingState() {
		this.saving = false;
	}

	/**
	 * Delegates to the {@link IEntityEditorPageController#doSave(IProgressMonitor)}
	 * method of all known dirty {@link IEntityEditorPageController}s.
	 * 
	 * @param monitor The progress monitor to use.
	 */
	public void doSave(IProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.base.entity.editor.EntityEditorController.doSave.monitor.taskName"), dirtyPageControllers.size()); //$NON-NLS-1$
		logger.debug("Calling all page controllers doSave() method."); //$NON-NLS-1$
		saving = true;
		for (IEntityEditorPageController dirtyController : dirtyPageControllers) {
			dirtyController.doSave(new SubProgressMonitor(monitor, 1));
			dirtyController.markUndirty();
		}
	}

	/**
	 * Called when the associated editor gets the focus.
	 * It delegates the 
	 *
	 */
	public void editorFocussed() {
//		for (IEntityEditorPageController controller : controllerPages.keySet()) {
		for (IEntityEditorPageController controller : getPageControllers()) {
			controller.editorFocussed();
		}
	}
	
	/**
	 * Dispatches to all {@link IEntityEditorPageController}s registered with this controller and
	 * calls {@link IEntityEditorPageController#editorFocussed()}.
	 */
	public void dispose() {
//		for (IEntityEditorPageController controller : controllerPages.keySet()) {
		for (IEntityEditorPageController controller : getPageControllers()) {
			controller.dispose();
		}
	}

	/**
	 * Get those {@link IEntityEditorPageController}s that are instances of a given class
	 * or implement a given interface. This method takes subclasses into account as well
	 * as the complete interface hierarchy.
	 * <p>
	 * For the resulting instances, the condition <code>entityPageController instanceof clazz</code>
	 * is always true.
	 * </p>
	 *
	 * @param clazz A class or interface.
	 * @return a <code>Set</code> of <code>IEntityEditorPageController</code> holding those
	 *		instances that either implement or are instances of the given class/interface or a subclass
	 *		of it. Never returns <code>null</code>.
	 */
	public Set<IEntityEditorPageController> getPageControllers(Class<?> clazz)
	{
		Set<IEntityEditorPageController> res = class2pageControllers.get(clazz);
		if (res != null)
			return res;

		return Collections.emptySet();
	}
}
