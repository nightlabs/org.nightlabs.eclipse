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
import org.nightlabs.eclipse.ui.pdfviewer.Point2DDouble;

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
	private Point2DDouble zoomScreenResolutionFactor;
	private OneDimensionalPdfDocument oneDimensionalPdfDocument;


	public PdfThumbnailNavigatorComposite(Composite parent, int style, PdfThumbnailNavigator pdfThumbnailNavigator) {
		super(parent, style);
		this.pdfThumbnailNavigator = pdfThumbnailNavigator;
		this.setLayout(new FillLayout());

		thumbnailPdfViewer = new PdfViewer();
		thumbnailPdfViewer.createControl(this, SWT.NONE);
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

    public void zoomToPageWidth() {

    	// not needed at the moment => delete later

/*
		Point screenDPI = getDisplay().getDPI();
		zoomScreenResolutionFactor = new Point2DDouble(
				(double)screenDPI.x / 72,
				(double)screenDPI.y / 72
		);

		UIDefaults uidef = UIManager.getDefaults();
		int swidth = Integer.parseInt(uidef.get("ScrollBar.width").toString());

		// compute zoom factor for fitting the document into thumb-nail composite (considering width)
		// TODO make horizontal scroll-bar of PDF thumb-nail composite invisible as it is not used in this composite
		int pdfThumbnailNavigatorCompositeWidth = getBounds().width - swidth;
		double documentWidth = oneDimensionalPdfDocument.getDocumentDimension().getWidth();
		double pdfThumbnailNavigatorCompositeWidthReal = pdfThumbnailNavigatorCompositeWidth / (zoomScreenResolutionFactor.getX());

		int zoomFactorPerMill = (int) (pdfThumbnailNavigatorCompositeWidthReal / documentWidth * 1000);

		if (logger.isDebugEnabled()) {
			logger.info("pdfThumbnailNavigatorCompositeWidth " + pdfThumbnailNavigatorCompositeWidth);
			logger.info("pdfThumbnailNavigatorCompositeWidthReal " + pdfThumbnailNavigatorCompositeWidthReal);
			logger.info("documentWidth " + documentWidth);
			logger.info("zoomFactorPerMill " + zoomFactorPerMill);
		}

		thumbnailPdfViewer.setAutozoom(AutoZoom.pageWidth);
		thumbnailPdfViewer.setZoomFactorPerMill(zoomFactorPerMill);
*/
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
