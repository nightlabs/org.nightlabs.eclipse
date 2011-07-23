/**
 * 
 */
package org.nightlabs.base.ui.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.nightlabs.base.ui.context.IUIContextRunner;
import org.nightlabs.base.ui.context.UIContext;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.progress.ProgressMonitor;

/**
 * A {@link org.eclipse.core.runtime.jobs.Job} that will wrap around the normal {@link IProgressMonitor} of an Eclipse Job and provide an
 * {@link ProgressMonitorWrapper} instead.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class Job extends org.eclipse.core.runtime.jobs.Job {

	private ProgressMonitorWrapper progressMonitorWrapper;
	private IProgressMonitor progressMonitor;
	private IUIContextRunner contextRunner;
	
	// private ProgressMonitorWrapper subProgressMonitorWrapper;
	// private IProgressMonitor subProgressMonitor;

	/**
	 * Create a new Job with the given name.
	 * 
	 * @param name
	 *            The name of the new Job.
	 */
	public Job(String name) {
		super(name);
		contextRunner = getThreadRunner();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation invokes {@link #run(ProgressMonitor)} with a {@link ProgressMonitorWrapper} wrapping around the
	 * {@link IProgressMonitor} passed.
	 * </p>
	 * <p>
	 * Note that the error handling is not done by the Job API but by the {@link ExceptionHandlerRegistry}.
	 * </p>
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// The context runner for the current thread will be the one responsible
		// for the thread the Job was created on.
		UIContext.sharedInstance().registerRunner(Thread.currentThread(), contextRunner);
		try {
			final IStatus[] status = new Status[1];
			this.progressMonitor = monitor;
			try {
				contextRunner.runInUIContext(new Runnable() {
					@Override
					public void run() {
						try {
							status[0] = Job.this.run(getProgressMonitorWrapper());
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});
			} catch (final Throwable t) {
				status[0] = Status.CANCEL_STATUS;
				contextRunner.runInUIContext(new Runnable() {
					@Override
					public void run() {
						ExceptionHandlerRegistry.asyncHandleException(t);
					}
				});
			} finally {
				 this.progressMonitor = null;
				 this.progressMonitorWrapper = null;
			}
			return status[0];
		} finally {
			UIContext.sharedInstance().unregister(Thread.currentThread());
		}
	}
	
	private static IUIContextRunner getThreadRunner() {
		IUIContextRunner runner = UIContext.sharedInstance().getRunner(Thread.currentThread());
		if (runner == null) {
			throw new IllegalStateException("No IUIContextRunner was setup for this thread " + Thread.currentThread());
		}
		return runner;
	}

	/**
	 * Implement this method to do the Jobs work. Note that this method might throw and exception if something fails during the job. The
	 * calling method will catch all exceptions and handle it with the {@link ExceptionHandlerRegistry} instead of the Job API error
	 * handler.
	 * 
	 * @param monitor
	 *            The monitor to report progress.
	 * @return The status the Job finished with.
	 * @throws Exception
	 *             If something fails during the Job.
	 */
	protected abstract IStatus run(ProgressMonitor monitor) throws Exception;

	/**
	 * Returns a {@link ProgressMonitorWrapper} wrapping around the {@link IProgressMonitor} of this Job.
	 * <p>
	 * Note that this is set when the Job runs and will not be accessible before {@link #run(IProgressMonitor)} was invoked and an
	 * {@link IllegalStateException} will be thrown then.
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
	 * Note that this is set when the Job runs and will not be accessible before {@link #run(IProgressMonitor)} was invoked and an
	 * {@link IllegalStateException} will be thrown then.
	 * </p>
	 * 
	 * @param subTicks
	 *            The number of ticks the sub-task wants to notify.
	 * @return A {@link ProgressMonitorWrapper} wrapping around a {@link SubProgressMonitor} to the {@link IProgressMonitor} of this Job.
	 * @deprecated Use {@link #createSubProgressMonitorWrapper(int)} instead
	 */
	@Deprecated
	public ProgressMonitorWrapper getSubProgressMonitorWrapper(int subTicks) {
		return createSubProgressMonitorWrapper(subTicks);
	}

	/**
	 * Returns a {@link ProgressMonitorWrapper} wrapping around a {@link SubProgressMonitor} to the {@link IProgressMonitor} of this Job.
	 * <p>
	 * Note that this is set when the Job runs and will not be accessible before {@link #run(IProgressMonitor)} was invoked and an
	 * {@link IllegalStateException} will be thrown then.
	 * </p>
	 * 
	 * @param subTicks
	 *            The number of ticks the sub-task wants to notify.
	 * @return A {@link ProgressMonitorWrapper} wrapping around a {@link SubProgressMonitor} to the {@link IProgressMonitor} of this Job.
	 */
	public ProgressMonitorWrapper createSubProgressMonitorWrapper(int subTicks) {
		if (progressMonitor == null)
			throw new IllegalStateException("getSubProgressMonitorWrapper must not be called before run(IProgressMonitor) was invoked."); //$NON-NLS-1$
		return new ProgressMonitorWrapper(createSubProgressMonitor(subTicks));
	}

	private SubProgressMonitor createSubProgressMonitor(int subTicks) {
		return new SubProgressMonitor(progressMonitor, subTicks);
	}

	/**
	 * Retuns the {@link IProgressMonitor} this Job runs with.
	 * <p>
	 * Note that this is set when the Job runs and will not be accessible before {@link #run(IProgressMonitor)} was invoked and an
	 * {@link IllegalStateException} will be thrown then.
	 * </p>
	 * 
	 * @return The {@link IProgressMonitor} this Job runs with.
	 */
	public IProgressMonitor getProgressMonitor() {
		if (this.progressMonitor == null)
			throw new IllegalStateException("getProgressMonitor must not be called before run(IProgressMonitor) was invoked."); //$NON-NLS-1$
		return progressMonitor;
	}
}
