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

import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.progress.CompoundProgressMonitor;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.util.RCPUtil;

/**
 * <p>This implementation of {@link IEntityEditorPageController} can be used
 * as controllers for entity entityEditor pages registered by the "pageFactory" element.
 * This base class allows to schedule a job when the controller is created, so that when the
 * page is shown and needs to access the controller's data it might be
 * already (partially) loaded.</p>
 * 
 * <p>The loading job can be started at any time via {@link #startLoadJob()} 
 * or you can use the constructor {@link #EntityEditorPageController(boolean)}</p>
 * 
 * <p>If a thread needs to access this controllers data it should 
 * use the {@link #load(IProgressMonitor)} method instead of invoking 
 * {@link IEntityEditorPageController#doLoad(IProgressMonitor)} directly.</p> 
 * 
 * <p>{@link EntityEditorPageController} extends {@link PropertyChangeSupport} and will
 * pass the {@link EntityEditor} this controller was created with as source to
 * all property change listeners.</p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class EntityEditorPageController
implements IEntityEditorPageController 
{
	private static final Logger logger = Logger.getLogger(EntityEditorPageController.class);
	
	/**	
	 * The entityEditor controller this page controller is registered to.
	 * Used to put the loadJob in the entityEditor controllers job pool.
	 */
	private EntityEditorController editorController;
	
	private ListenerList listeners = new ListenerList();

	/**
	 * The actual background loading job.
	 * This calls {@link IEntityEditorPageController#doLoad(IProgressMonitor)} 
	 * and notifies the wrapping page controller
	 * of its end by the controller's {@link EntityEditorPageController#mutex}.
	 */
	public static class LoadJob extends Job {
		
		private EntityEditorPageController controller;
		private CompoundProgressMonitor cMonitor;
		private Throwable loadException;
		private boolean loaded = false;
		
		public LoadJob(EntityEditorPageController controller) {
			super(Messages.getString("org.nightlabs.base.entity.editor.EntityEditorPageController.LoadJob.name")); //$NON-NLS-1$
			this.controller = controller;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			cMonitor = new CompoundProgressMonitor(monitor);
			try {
				controller.doLoad(cMonitor);
				controller.setLoaded(true);
				this.loaded = true;
			} catch (Throwable t) {
				logger.error("LoadJob failed!", t); //$NON-NLS-1$
				// Workaround as we can not get a grip of exceptions within jobs
				this.loadException = t;
				controller.setLoaded(false);
				this.loaded = false;
				return Status.CANCEL_STATUS; // TODO: Write error status
			}
			synchronized (controller.getMutex()) {
				controller.getMutex().notifyAll();
			}
			return Status.OK_STATUS;
		}
		
		public CompoundProgressMonitor getCompoundProgressMonitor() {
			return cMonitor;
		}
		
		public Throwable getLoadException() {
			return loadException;
		}
		
		public boolean isLoaded() {
			return loaded;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default implementation in <code>EntityEditorPageController</code> returns
	 * a composition of the class name and the {@link System#identityHashCode(Object)}.
	 * Therefore, all instances have different IDs.
	 * </p>
	 */
	@Implement
	public String getControllerID()
	{
		return this.getClass().getName() + '@' + System.identityHashCode(this);
	}
	
//	/**
//	 * The page this controller is associated with
//	 */
//	private IFormPage page;
	
	/**
	 * Stores the loading status
	 */
	private boolean loaded = false;
	
	/**
	 * Mutex used to synchronise the background load with
	 * other threads that have to wait for its end.
	 */
	private Object mutex = new Object();
	
	/**
	 * The load job member. If null, its assumed that no background
	 * loading will be done by this controller.
	 */
	private LoadJob loadJob = null;
	
	/**
	 * Whether the load job is currently running
	 */
	private boolean loadJobRunning = false;
	/**
	 * Whether the load job is already finished
	 */
	private boolean loadJobDone = false;
	
	/**
	 * if the controller is dirty or not
	 */
	private boolean dirty = false;

	private EntityEditor entityEditor;

	/**
	 * Create a new page controller that
	 * will not do background loading. 
	 */
	public EntityEditorPageController(EntityEditor editor) {
		this(editor, false);
	}
	
	/**
	 * Create a new {@link EntityEditorController} and
	 * decide whether to start background loading instantly.
	 * 
	 * @param startBackgroundLoading Whether to start the load job instantly.
	 */
	public EntityEditorPageController(EntityEditor editor, boolean startBackgroundLoading) {
//		super(entityEditor);
		this.entityEditor = editor;
		if (startBackgroundLoading)
			startLoadJob();
	}

	public EntityEditor getEntityEditor()
	{
		return entityEditor;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.entity.editor.IEntityEditorPageController#setEntityEditorController(org.nightlabs.base.entity.editor.EntityEditorController)
	 */
	public void setEntityEditorController(EntityEditorController editorController) {
		this.editorController = editorController;
		if (loadJob != null)
			editorController.putLoadJob(this, loadJob);
	}

	private Set<IFormPage> pages = new HashSet<IFormPage>();

	public Set<IFormPage> getPages() {
		return Collections.unmodifiableSet(pages);
	}

	public void addPage(IFormPage page) {
		pages.add(page);
	}
	
	/**
	 * Returns the entityEditor controller this page controller is linked to. 
	 * @return Tthe entityEditor controller this page controller is linked to.
	 */
	public EntityEditorController getEntityEditorController() {
		return editorController;
	}

	/**
	 * Check whether this controller already loaded its data.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Set the loading status.
	 */
	protected void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	/**
	 * Return the mutex used to synchronize the background loading.
	 * @return The mutex used to synchronize the background loading.
	 */
	protected Object getMutex() {
		return mutex;
	}

	/**
	 * Starts the load job for this controller's
	 * data if not already done.
	 */
	public void startLoadJob() {
		if (loadJob == null) {
			loadJob = new LoadJob(this);
			loadJob.addJobChangeListener(new IJobChangeListener() {
				public void aboutToRun(IJobChangeEvent arg0) {
				}
				public void awake(IJobChangeEvent arg0) {
				}
				public void done(IJobChangeEvent arg0) {
					loadJobDone = true;
				}
				public void running(IJobChangeEvent arg0) {
					loadJobRunning = true;
				}
				public void scheduled(IJobChangeEvent arg0) {
				}
				public void sleeping(IJobChangeEvent arg0) {
				}
			});
			// when not called from constructor 
			if (editorController != null)
				editorController.putLoadJob(this, loadJob);
		}
	}

	/**
	 * Runnable that waits until the mutex is notified.
	 * Called from different scopes
	 */
	private IRunnableWithProgress waitForLoadJob = new IRunnableWithProgress() {
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			if (!loaded)
				loadJob.getCompoundProgressMonitor().addProgressMonitor(monitor);
				synchronized (mutex) {					
					while (!loaded)
						mutex.wait(200);
				}
		}
	};
	
	/**
	 * <p>Ensures that this controller's {@link IEntityEditorPageController#doLoad(IProgressMonitor)}
	 * method has fully run and thus the controller is ready for use.</p> 
	 * 
	 * <p>If {@link #isLoaded()} returns true this method immeditiately returns.
	 * If not, it will first check, if the data already began to load in the 
	 * background. If so, it will 'join' the loading job and wait until its finished.</p>
	 * 
	 * <p>If the background job was not created or started yet, the data will
	 * be loaded on the current thread.
	 * <p>Note that invoking this form the gui thread will cause a blocking 
	 * progress dialog to appear.</p>
	 */
	public synchronized void load(IProgressMonitor monitor)
	{
		if (isLoaded())
			return;
		try {
			if (loadJob != null) {
				editorController.popLoadJob(this);
				if (loadJobRunning && !loadJobDone) {
					if (RCPUtil.isDisplayThread()) {
						ProgressMonitorDialog dlg = new ProgressMonitorDialog(null);
						dlg.run(true, false, waitForLoadJob);
					}
					else
						waitForLoadJob.run(monitor);
				}
			}
			else {
				if (RCPUtil.isDisplayThread()) {
					ProgressMonitorDialog dlg = new ProgressMonitorDialog(null);
					dlg.run(true, false, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException	{
							monitor.beginTask(Messages.getString("org.nightlabs.base.entity.editor.EntityEditorPageController.load.monitor.taskName"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
							doLoad(monitor);
							monitor.done();
							setLoaded(true);
						}
					});
				}
				else {
					doLoad(monitor);
					setLoaded(true);
				}
			}
		} catch (InvocationTargetException e) {
			logger.error("Loading entity failed", e); //$NON-NLS-1$
			throw new RuntimeException("Loading entity failed", e); //$NON-NLS-1$
		} catch (InterruptedException e) {
			logger.error("Loading entity failed", e); //$NON-NLS-1$
			throw new RuntimeException("Loading entity failed", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * determines if the controller is dirty, 
	 * once the page or one of the pages were dirty the controller
	 * stys sirty even if the page has been cleaned 
	 * (e.g. by a calling of commit of an included section)
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * marks the controller dirty
	 */
	public void markDirty() {
		dirty = true;
	}

	/**
	 * removes the dirty state, after calling this method 
	 * {@link #isDirty()} returns false
	 * 
	 */
	public void markUndirty() {
		dirty = false;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation does nothing.
	 * </p>
	 * @see org.nightlabs.base.entity.editor.IEntityEditorPageController#dispose()
	 */
	@Implement
	public void dispose() {
		// does nothing
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation does nothing.
	 * </p>
	 * @see org.nightlabs.base.entity.editor.IEntityEditorPageController#editorFocussed()
	 */
	public void editorFocussed() {
		// does nothing
	}

//	/**
//	 * @deprecated {@link #addModifyListener(IEntityEditorPageControllerModifyListener)} should be used instead.
//	 * 		This should not be used any more, as PropertyChangeSupport check if 
//	 * 		oldValue.equals(newValue) for the notified objects and will not propagete
//	 * 		if this is true. For most JDO, that check their id in equals, this is 
//	 * 		not applicable.
//	 */
//	public synchronized void addPropertyChangeListener(PropertyChangeListener arg0) {
//		propertyChangeSupport.addPropertyChangeListener(arg0);
//	}
//
//	/**
//	 * @deprecated {@link #removeModifyListener(IEntityEditorPageControllerModifyListener)} should be used instead.
//	 */
//	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
//		propertyChangeSupport.removePropertyChangeListener(listener);
//	}

	/**
	 * Adds a new {@link IEntityEditorPageControllerModifyListener} to this controller.
	 * This will immediately cause the listener to be triggered with the last event - in case there is one.
	 *
	 * @param listener The listener to be added.
	 */
	public void addModifyListener(IEntityEditorPageControllerModifyListener listener) {
		listeners.add(listener);
		EntityEditorPageControllerModifyEvent lastModifyEvent = this.lastModifyEvent;
		if (lastModifyEvent != null)
			listener.controllerObjectModified(lastModifyEvent);
	}

	/**
	 * Remove the given {@link IEntityEditorPageControllerModifyListener} from this controller.
	 * 
	 * @param listener The listener to remove.
	 */
	public void removeModifyListener(IEntityEditorPageControllerModifyListener listener) {
		listeners.remove(listener);
	}

	private volatile EntityEditorPageControllerModifyEvent lastModifyEvent = null;

	/**
	 * Use this to notify all listeners of a changed object.
	 * 
	 * @param oldObject The old object value.
	 * @param newObject The new object value.
	 */
	protected void fireModifyEvent(Object oldObject, Object newObject)
	{
		EntityEditorPageControllerModifyEvent lastModifyEvent = new EntityEditorPageControllerModifyEvent(this, oldObject, newObject);
		this.lastModifyEvent = lastModifyEvent;

		Object[] list = listeners.getListeners();
		for (Object listener : list) {
			((IEntityEditorPageControllerModifyListener) listener).controllerObjectModified(lastModifyEvent);
		}
	}
	
}
	
