/**
 * 
 */
package org.nightlabs.base.ui.exceptionhandler;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.statushandlers.WorkbenchErrorHandler;
import org.nightlabs.base.ui.app.AbstractWorkbenchAdvisor;

/**
 * A {@link WorkbenchErrorHandler} invoked by the Workbench to handle {@link IStatus} results.
 * This implementation will delegate to its super-class if no Exception was set for the {@link IStatus}
 * and to the {@link ExceptionHandlerRegistry} otherwise.
 * <p>
 * This class can be registered as extension to the point 'org.eclipse.ui.statushandlers'
 * or returned in {@link WorkbenchAdvisor#getWorkbenchErrorHandler()}, note that
 * {@link AbstractWorkbenchAdvisor} does this by default.
 * </p>  
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ErrorStatusHandler extends WorkbenchErrorHandler {

	private static final Logger logger = Logger.getLogger(ErrorStatusHandler.class);
	
	/**
	 * Create a new {@link ErrorStatusHandler} 
	 */
	public ErrorStatusHandler() {
	}
	
	/**
	 * Checks if the status to handle has an {@link Exception} set
	 * and delegate the handling to the {@link ExceptionHandlerRegistry}.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void handle(StatusAdapter statusAdapter, int style) {
		IStatus status = statusAdapter.getStatus();
		if ((style & (StatusManager.BLOCK | StatusManager.SHOW)) > 0) {
			if (status.getException() != null) {
				// if the status has an exception set, we let the ExceptionHandlerRegistry handle
				if (ExceptionHandlerRegistry.syncHandleException(status.getException())) {
					return;
				}
			}			
		}
		else if ((style & StatusManager.LOG) > 0) {
			logger.error("Exception with style StatusManager.LOG", status.getException()); //$NON-NLS-1$
		}
		super.handle(statusAdapter, style);
	}

}
