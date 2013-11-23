package org.nightlabs.eclipse.ui.pdfrenderer;

public interface IPDFProgressMonitor {

	void beginTask(String taskName, int work);
	void worked(int work);
	void done();
}
