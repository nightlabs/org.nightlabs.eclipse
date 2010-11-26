/**
 * 
 */
package org.nightlabs.eclipse.ui.pdfrenderer.internal;

import org.nightlabs.eclipse.ui.pdfrenderer.IPdfProgressMonitor;

/**
 * Implementation of {@link IPdfProgressMonitor} doing nothing.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class NullPdfProgressMonitor implements IPdfProgressMonitor {

	/**
	 * 
	 */
	public NullPdfProgressMonitor() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfrenderer.IPdfProgressMonitor#beginTask(java.lang.String, int)
	 */
	@Override
	public void beginTask(String taskName, int work) {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfrenderer.IPdfProgressMonitor#done()
	 */
	@Override
	public void done() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfrenderer.IPdfProgressMonitor#worked(int)
	 */
	@Override
	public void worked(int work) {
	}

}
