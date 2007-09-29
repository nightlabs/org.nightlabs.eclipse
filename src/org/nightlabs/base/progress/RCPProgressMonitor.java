/**
 * 
 */
package org.nightlabs.base.progress;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

/**
 * A wrapper for {@link ProgressMonitor}s to be used as {@link IProgressMonitor}s.
 * This is the counter wrapper to {@link ProgressMonitorWrapper}.
 * <br> <br>
 * Note: Try to not wrap a monitor too many times! Try an {@link INSTANCEOF} and use the getter to 
 * 	extract the wrapped monitor.
 * 
 * @author Marius Heinzmann [marius<at>NightLabs<dot>de]
 */
public class RCPProgressMonitor implements IProgressMonitor {

	private ProgressMonitor monitor;
	
	public RCPProgressMonitor(ProgressMonitor monitor) {
		this.monitor = monitor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#done()
	 */
	public void done() {
		monitor.done();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
	 */
	public void internalWorked(double work) {
		monitor.internalWorked(work);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
	 */
	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
	 */
	public void setCanceled(boolean value) {
		monitor.setCanceled(value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) {
		monitor.setTaskName(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String name) {
		monitor.subTask(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
	 */
	public void worked(int work) {
		monitor.worked(work);
	}
	
	/**
	 * Returns the wrapper {@link ProgressMonitor}.
	 * @return the wrapper {@link ProgressMonitor}.
	 */
	public ProgressMonitor getProgressMonitor() {
		return monitor;
	}

}
