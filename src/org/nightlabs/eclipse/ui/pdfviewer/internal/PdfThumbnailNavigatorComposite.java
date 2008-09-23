package org.nightlabs.eclipse.ui.pdfviewer.internal;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.eclipse.ui.pdfviewer.OneDimensionalPdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfThumbnailNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;

/**
 * This composite displays a scrollable list of thumbnails of a PDF file
 * in order to navigate a {@link PdfViewerComposite}.
 *
 * @author frederik loeser - frederik at nightlabs dot de
 */
public class PdfThumbnailNavigatorComposite extends Composite {

	private PdfThumbnailNavigator pdfThumbnailNavigator;
	private Composite viewPanelComposite;
	private PdfViewer pdfViewer;
	private Control pdfViewerControl;


	public PdfThumbnailNavigatorComposite(Composite parent, int style, PdfThumbnailNavigator pdfThumbnailNavigator) {
		super(parent, style);
		this.pdfThumbnailNavigator = pdfThumbnailNavigator;
		this.setLayout(new FillLayout());

		pdfViewer = new PdfViewer();
		pdfViewerControl = pdfViewer.createControl(this, SWT.NONE);

		// TODO NOT IN CONSTRUCTOR! Use listeners!
		setPdfDocument(pdfThumbnailNavigator.getMainPdfViewer().getPdfDocument());
	}

	public void setPdfDocument(PdfDocument pdfDocument)
	{
		if (pdfDocument == null) {
			pdfViewer.setPdfDocument(null);
			return;
		}

		// we force a OneDimensionalPdfDocument, because we have no idea, how the PdfDocument of the main viewer looks like.
		OneDimensionalPdfDocument oneDimensionalPdfDocument = new OneDimensionalPdfDocument(
				pdfDocument.getPdfFile(),
				OneDimensionalPdfDocument.Layout.vertical,
				new NullProgressMonitor()
		);
		pdfViewer.setPdfDocument(oneDimensionalPdfDocument);

		// TODO do NOT hard-code zoom factor, but extend PdfViewer API to have flags like "zoom to page width"
		pdfViewer.setZoomFactorPerMill(200);
	}

	public PdfViewer getPdfViewer() {
    	return pdfViewer;
    }

	public void setPdfViewer(PdfViewer pdfViewer) {
    	this.pdfViewer = pdfViewer;
    }
}
