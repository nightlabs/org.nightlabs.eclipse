/**
 *
 */
package org.nightlabs.eclipse.ui.pdfrenderer.internal;

import org.nightlabs.eclipse.ui.pdfrenderer.IPDFProgressMonitor;

/**
 * Implementation of {@link IPDFProgressMonitor} doing nothing.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class NullPDFProgressMonitor implements IPDFProgressMonitor {

	/**
	 *
	 */
	public NullPDFProgressMonitor() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfrenderer.IPdfProgressMonitor#beginTask(java.lang.String, int)
	 */
	@Override
	public void beginTask(final String taskName, final int work) {
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
	public void worked(final int work) {
	}

}
