package org.nightlabs.base.ui.entity.editor.overview;

import org.eclipse.core.runtime.IStatus;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public interface IOverviewPageStatusProvider  
{	
	/**
	 * Loads the {@link IStatus}, which can be obtained by {@link #getStatus()} when loading is finished. 
	 * @param monitor the {@link ProgressMonitor} to display the loading progress
	 */
	void resolveStatus(ProgressMonitor monitor);
	
	/**
	 * @return <code>null</code> before {@link #resolveStatus(ProgressMonitor)} has been called - afterwards an instance of {@link IStatus}.
	 */
	IStatus getStatus(); 
	
	/**
	 * Determines whether loading the status is deferred, because it is along running operation. 
	 * @return true if loading the status is deferred or false if not.
	 */
	boolean isResolveStatusDeferred();

	/**
	 * 
	 * @param entityEditor the {@link EntityEditor} which holds the page where this statusProvider belongs to.
	 */
	void setEntityEditor(EntityEditor entityEditor);
}
