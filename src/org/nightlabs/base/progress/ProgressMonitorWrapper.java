/**
 * 
 */
package org.nightlabs.base.progress;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

/**
 * This is a wrapper for {@link IProgressMonitor}s to be used inside our {@link ProgressMonitor} based
 * framework. The counter wrapper is {@link RCPProgressMonitor}.
 * <br> <br>
 * Note: Try to not wrap a monitor too many times! Try an {@link INSTANCEOF} and use the getter to 
 * 	extract the wrapped monitor.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Marius Heinzmann [marius<at>NightLabs<dot>de]
 */
public class ProgressMonitorWrapper implements ProgressMonitor {

	private IProgressMonitor monitor;
	
	public ProgressMonitorWrapper(IProgressMonitor monitor) {
		if (monitor == null)
			throw new IllegalArgumentException("The wrapped monitor must not be null."); //$NON-NLS-1$
		this.monitor = monitor;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int totalWork) {
		monitor.beginTask(name, totalWork);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#done()
	 */
	public void done() {
		monitor.done();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) {
		monitor.setTaskName(name);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String name) {
		monitor.subTask(name);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.progress.ProgressMonitor#worked(int)
	 */
	public void worked(int work) {
		monitor.worked(work);
	}

	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	public void setCanceled(boolean canceled) {
		monitor.setCanceled(canceled);
	}

	public void internalWorked(double worked) {
		monitor.internalWorked(worked);
	}
	
	/**
	 * Returns the wrapped {@link IProgressMonitor}.
	 * @return the wrapped {@link IProgressMonitor}.
	 */
	public IProgressMonitor getIProgressMonitor() {
		return monitor;
	}
}
