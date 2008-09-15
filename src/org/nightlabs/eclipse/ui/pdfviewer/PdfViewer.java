package org.nightlabs.eclipse.ui.pdfviewer;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.internal.PdfViewerComposite;

public class PdfViewer
{
	private PdfDocument pdfDocument;
	private PdfViewerComposite pdfViewerComposite;

	private void assertValidThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!");
	}

	public PdfViewer() { }

	public Control createControl(Composite parent)
	{
		assertValidThread();
		if (this.pdfViewerComposite != null)
			this.pdfViewerComposite.dispose();

		this.pdfViewerComposite = new PdfViewerComposite(parent);
		this.pdfViewerComposite.setPdfDocument(pdfDocument); // just in case, the document was set before this method.
		return this.pdfViewerComposite;
	}

	public Control getControl() {
		assertValidThread();

		return this.pdfViewerComposite;
	}

	public PdfDocument getPdfDocument() {
		assertValidThread();

		return pdfDocument;
	}

	public void setPdfDocument(PdfDocument pdfDocument) {
		this.pdfDocument = pdfDocument;
		if (pdfViewerComposite != null)
			pdfViewerComposite.setPdfDocument(pdfDocument);
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
	// - get the zoom (in per mill)

	// - set the zoom
	// - get visible dimension (i.e. width + height) of the view panel in real coordinates
}
