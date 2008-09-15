package org.nightlabs.eclipse.ui.pdfviewer.composite;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.PdfViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.model.PdfDocument;

public class PdfViewer
{
	private PdfDocument pdfDocument;
	private PdfViewerComposite pdfViewerComposite;

	private void assertValidThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!");
	}

	public PdfViewer(final PdfDocument pdfDocument) {
		if (pdfDocument == null)
			throw new IllegalArgumentException("pdfDocument must not be null!");

	    this.pdfDocument = pdfDocument;
    }

	public Control createControl(Composite parent)
	{
		assertValidThread();

		pdfViewerComposite = new PdfViewerComposite(parent, pdfDocument);
		return pdfViewerComposite;
	}

	public Control getPdfViewerComposite() {
		assertValidThread();

	    return pdfViewerComposite;
    }

	public PdfDocument getPdfDocument() {
		assertValidThread();

	    return pdfDocument;
    }

	public Point getViewOrigin() {
		assertValidThread();

	    return pdfViewerComposite.getViewOrigin();
    }

	public void setViewOrigin(Point viewOrigin) {
		assertValidThread();

	    pdfViewerComposite.setViewOrigin(viewOrigin);
    }

	// TODO more API, like:
}
