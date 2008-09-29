package org.nightlabs.eclipse.ui.pdfviewer.internal;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
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

	private static final Logger logger = Logger.getLogger(PdfThumbnailNavigatorComposite.class);
	private PdfThumbnailNavigator pdfThumbnailNavigator;
	private PdfViewer thumbnailPdfViewer;
	private OneDimensionalPdfDocument oneDimensionalPdfDocument;


	public PdfThumbnailNavigatorComposite(Composite parent, int style, PdfThumbnailNavigator pdfThumbnailNavigator) {
		super(parent, style);
		this.pdfThumbnailNavigator = pdfThumbnailNavigator;
		this.setLayout(new FillLayout());

		thumbnailPdfViewer = new PdfViewer();
		thumbnailPdfViewer.createControl(this, SWT.NONE);
		thumbnailPdfViewer.setZoomIsAllowed(false);
		setPdfDocument(pdfThumbnailNavigator.getPdfDocument());
	}

	public void setPdfDocument(PdfDocument pdfDocument)
	{
		if (pdfDocument == null) {
			thumbnailPdfViewer.setPdfDocument(null);
			return;
		}

		// we force a OneDimensionalPdfDocument, because we have no idea, how the PdfDocument of the main viewer looks like.
		oneDimensionalPdfDocument = new OneDimensionalPdfDocument(
				pdfDocument.getPdfFile(),
				OneDimensionalPdfDocument.Layout.vertical,
				new NullProgressMonitor()
		);
		thumbnailPdfViewer.setPdfDocument(oneDimensionalPdfDocument);

	}

	public PdfViewer getThumbnailPdfViewer() {
		return thumbnailPdfViewer;
	}

	public void setThumbnailPdfViewer(PdfViewer pdfViewer) {
		this.thumbnailPdfViewer = pdfViewer;
	}

	public void setCurrentPage(int pageNumber, boolean doFire) {
		thumbnailPdfViewer.setCurrentPage(pageNumber, doFire);
	}
}
