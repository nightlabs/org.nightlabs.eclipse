/**
 * 
 */
package org.nightlabs.base.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.progress.ProgressMonitorWrapper;
import org.nightlabs.progress.ProgressMonitor;

/**
 * A {@link org.eclipse.core.runtime.jobs.Job} that will wrap around
 * the normal {@link IProgressMonitor} of an Eclipse Job and provide
 * an {@link ProgressMonitorWrapper} instead.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class Job extends org.eclipse.core.runtime.jobs.Job {

	private ProgressMonitorWrapper progressMonitorWrapper;
	private IProgressMonitor progressMonitor;
	private ProgressMonitorWrapper subProgressMonitorWrapper;
	private IProgressMonitor subProgressMonitor;
	
	/**
	 * Create a new Job with the given name.
	 * 
	 * @param name The name of the new Job.
	 */
	public Job(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation invokes {@link #run(ProgressMonitor)} with
	 * a {@link ProgressMonitorWrapper} wrapping around the {@link IProgressMonitor} passed.
	 * </p>
	 * <p>
	 * Note that the error handling is not done by the Job API but by the {@link ExceptionHandlerRegistry}.
	 * </p>
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		this.progressMonitor = monitor;
		IStatus status;
		try {
			status = run(getProgressMonitorWrapper());
		} catch (Throwable t) {
			status = Status.CANCEL_STATUS;
			ExceptionHandlerRegistry.asyncHandleException(t);
		}
		return status;
	}
	
	/**
	 * Implement this method to do the Jobs work.
	 * Note that this method might throw and exception if 
	 * something fails during the job. The calling method
	 * will catch all exceptions and handle it with 
	 * the {@link ExceptionHandlerRegistry} instead of the Job 
	 * API error handler.
	 * 
	 * @param monitor The monitor to report progress.
	 * @return The status the Job finished with.
	 * @throws Exception If something fails during the Job.
	 */
	protected abstract IStatus run(ProgressMonitor monitor) throws Exception;
	
	/**
	 * Returns a {@link ProgressMonitorWrapper} wrapping around the {@link IProgressMonitor} of this Job.
	 * <p> 
	 * Note that this is set when the Job runs and will not
	 * be accessible before {@link #run(IProgressMonitor)} was invoked and 
	 * an {@link IllegalStateException} will be thrown then.
	 * </p>
	 * 
	 * @return A {@link ProgressMonitorWrapper} wrapping arount the {@link IProgressMonitor} of this Job.
	 */
	public ProgressMonitorWrapper getProgressMonitorWrapper() {
		if (progressMonitorWrapper == null) {
			if (progressMonitor == null)
				throw new IllegalStateException("getProgressMonitorWrapper must not be called before run(IProgressMonitor) was invoked."); //$NON-NLS-1$
			progressMonitorWrapper = new ProgressMonitorWrapper(progressMonitor);
		}
		return progressMonitorWrapper;
	}
	
	/**
	 * Returns a {@link ProgressMonitorWrapper} wrapping around a {@link SubProgressMonitor} to the {@link IProgressMonitor} of this Job.
	 * <p> 
	 * Note that this is set when the Job runs and will not
	 * be accessible before {@link #run(IProgressMonitor)} was invoked and 
	 * an {@link IllegalStateException} will be thrown then.
	 * </p>
	 * 
	 * @param subTicks The number of ticks the sub-task wants to notify.
	 * @return A {@link ProgressMonitorWrapper} wrapping around a {@link SubProgressMonitor} to the {@link IProgressMonitor} of this Job.
	 */
	public ProgressMonitorWrapper getSubProgressMonitorWrapper(int subTicks) {
		if (subProgressMonitorWrapper == null) {
			if (progressMonitor == null)
				throw new IllegalStateException("getSubProgressMonitorWrapper must not be called before run(IProgressMonitor) was invoked."); //$NON-NLS-1$
			subProgressMonitor = new SubProgressMonitor(progressMonitor, subTicks);
			subProgressMonitorWrapper = new ProgressMonitorWrapper(subProgressMonitor);
		}
		return subProgressMonitorWrapper;
	}
	
	/**
	 * Retuns the {@link IProgressMonitor} this Job runs with.
	 * <p> 
	 * Note that this is set when the Job runs and will not
	 * be accessible before {@link #run(IProgressMonitor)} was invoked and 
	 * an {@link IllegalStateException} will be thrown then.
	 * </p>
	 * @return The {@link IProgressMonitor} this Job runs with.
	 */
	public IProgressMonitor getProgressMonitor() {
		if (this.progressMonitor == null)
			throw new IllegalStateException("getProgressMonitor must not be called before run(IProgressMonitor) was invoked."); //$NON-NLS-1$
		return progressMonitor;
	}
}
