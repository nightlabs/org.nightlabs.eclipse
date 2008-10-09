package org.nightlabs.eclipse.ui.pdfviewer.internal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.eclipse.ui.pdfviewer.AutoZoom;
import org.nightlabs.eclipse.ui.pdfviewer.MouseEvent;
import org.nightlabs.eclipse.ui.pdfviewer.OneDimensionalPdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PaintAdapter;
import org.nightlabs.eclipse.ui.pdfviewer.PaintEvent;
import org.nightlabs.eclipse.ui.pdfviewer.PaintListener;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfThumbnailNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.Point2DDouble;

/**
 * This composite displays a scrollable list of thumbnails of a PDF file
 * in order to navigate a {@link PdfViewerComposite}.
 *
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfThumbnailNavigatorComposite extends Composite
{
//	private static final Logger logger = Logger.getLogger(PdfThumbnailNavigatorComposite.class);
	@SuppressWarnings("unused")
	private PdfThumbnailNavigator pdfThumbnailNavigator;
	private PdfViewer thumbnailPdfViewer;
	private Control thumbnailPdfViewerControl;
	private PdfDocument thumbnailPdfDocument;

	public PdfThumbnailNavigatorComposite(Composite parent, int style, PdfThumbnailNavigator pdfThumbnailNavigator) {
		super(parent, style);
		this.pdfThumbnailNavigator = pdfThumbnailNavigator;
		this.setLayout(new FillLayout());

		thumbnailPdfViewer = new PdfViewer();
		thumbnailPdfViewer.setUpdateCurrentPageOnScrolling(false);
		thumbnailPdfViewer.setAutoZoomHorizontalMargin(24);
		thumbnailPdfViewer.setAutoZoomVerticalMargin(24);
		thumbnailPdfViewer.setAutoZoom(AutoZoom.pageWidth);
		thumbnailPdfViewer.setMouseWheelZoomEnabled(false);
		thumbnailPdfViewer.addPaintToViewListener(currentPageDrawListener);
		thumbnailPdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_ZOOM_FACTOR, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Integer newZoom = (Integer) evt.getNewValue();
				zoomFactorPerMill = newZoom;
			}
		});
		thumbnailPdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_VIEW_ORIGIN, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Point2D newViewOrigin = (Point2D) evt.getNewValue();
				viewOrigin = newViewOrigin;
			}
		});
		thumbnailPdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_CURRENT_PAGE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (thumbnailPdfViewerControl != null)
					thumbnailPdfViewerControl.redraw();
			}
		});
		thumbnailPdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_MOUSE_CLICKED, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				final MouseEvent pdfMouseEvent = (MouseEvent) evt.getNewValue();

				// calculate current page from pdfMouseEvent.getPointInRealCoordinate() and set it
				Collection<Integer> visiblePages = pdfDocument.getVisiblePages(
						new Rectangle2D.Double(pdfMouseEvent.getPointInRealCoordinate().getX(), pdfMouseEvent.getPointInRealCoordinate().getY(), 1, 1)
				);

				final Integer newCurrentPage;
				if (visiblePages.isEmpty())
					newCurrentPage = null;
				else
					newCurrentPage = visiblePages.iterator().next();

				if (newCurrentPage != null)
					setCurrentPage(newCurrentPage);
			}
		});

		zoomFactorPerMill = thumbnailPdfViewer.getZoomFactorPerMill();
		viewOrigin = thumbnailPdfViewer.getViewOrigin();
		thumbnailPdfViewerControl = thumbnailPdfViewer.createControl(this, SWT.NONE);
		setPdfDocument(pdfThumbnailNavigator.getPdfDocument());
	}

	private Point2D viewOrigin;
	private int zoomFactorPerMill;

	private static final double borderWidthTopBottom = 15;
	private static final double borderWidthLeftRight = 15;

	private PaintListener currentPageDrawListener = new PaintAdapter() {
		@Override
		public void postPaint(PaintEvent event) {
			PdfViewer thumbnailPdfViewer = event.getSource();
			Graphics2D graphics2D = event.getGraphics2D();
			PdfDocument pdfDocument = PdfThumbnailNavigatorComposite.this.pdfDocument;
			if (pdfDocument == null)
				return;

			int currentPage = thumbnailPdfViewer.getCurrentPage();
			if (currentPage < 1)
				return;

			Rectangle2D borderBoundsTop, borderBoundsLeft, borderBoundsBottom, borderBoundsRight;

			// get the page bounds of the chosen page in real coordinates
			Rectangle2D pageBoundsReal = pdfDocument.getPageBounds(currentPage);

			Point2DDouble zoomScreenResolutionFactor = new Point2DDouble(thumbnailPdfViewer.getZoomScreenResolutionFactor());
			double zoomFactor = (double)zoomFactorPerMill / 1000;
			Point2D viewOrigin = PdfThumbnailNavigatorComposite.this.viewOrigin;

			// get the page bounds of the chosen page in image coordinates
			Rectangle2D pageBoundsView = new Rectangle2D.Double();
//			pageBoundsView.setRect(
//					(pageBoundsReal.getMinX() - viewOrigin.getX()) * zoomScreenResolutionFactor.getX() * zoomFactor,
//					(pageBoundsReal.getMinY() - viewOrigin.getY()) * zoomScreenResolutionFactor.getY() * zoomFactor,
//					pageBoundsReal.getWidth() * zoomScreenResolutionFactor.getX() * zoomFactor,
//					pageBoundsReal.getHeight() * zoomScreenResolutionFactor.getY() * zoomFactor
//			);

			pageBoundsView.setRect(
					(int) ((pageBoundsReal.getMinX() - viewOrigin.getX()) * zoomScreenResolutionFactor.getX() * zoomFactor),
					(int) ((pageBoundsReal.getMinY() - viewOrigin.getY()) * zoomScreenResolutionFactor.getY() * zoomFactor),
					(int) (pageBoundsReal.getWidth() * zoomScreenResolutionFactor.getX() * zoomFactor),
					(int) (pageBoundsReal.getHeight() * zoomScreenResolutionFactor.getY() * zoomFactor)
			);

			int borderWidthScreenLeftRight = (int) (borderWidthLeftRight * zoomScreenResolutionFactor.getX() * zoomFactor);
			int borderWidthScreenTopBottom = (int) (borderWidthTopBottom * zoomScreenResolutionFactor.getY() * zoomFactor);

			borderBoundsTop = new Rectangle2D.Double();
			borderBoundsLeft = new Rectangle2D.Double();
			borderBoundsBottom = new Rectangle2D.Double();
			borderBoundsRight = new Rectangle2D.Double();
			borderBoundsTop.setRect(
					pageBoundsView.getX() - borderWidthScreenLeftRight + 1,
					pageBoundsView.getY() - borderWidthScreenTopBottom + 1,
					pageBoundsView.getWidth() + 2 * borderWidthScreenLeftRight - 1,
					borderWidthScreenTopBottom
			);
			borderBoundsLeft.setRect(
					pageBoundsView.getX() - borderWidthScreenLeftRight + 1,
					pageBoundsView.getY() - borderWidthScreenTopBottom + 1,
					borderWidthScreenLeftRight,
					pageBoundsView.getHeight() + 2 * borderWidthScreenTopBottom - 1
			);
			borderBoundsBottom.setRect(
					pageBoundsView.getX() - borderWidthScreenLeftRight + 1,
					pageBoundsView.getMaxY(),
					pageBoundsView.getWidth() + 2 * borderWidthScreenLeftRight - 1,
					borderWidthScreenTopBottom
			);
			borderBoundsRight.setRect(
					pageBoundsView.getMaxX(),
					pageBoundsView.getY() - borderWidthScreenTopBottom + 1,
					borderWidthScreenLeftRight,
					pageBoundsView.getHeight() + 2 * borderWidthScreenTopBottom - 1
			);

			graphics2D.setColor(Color.BLUE);
			graphics2D.fill(borderBoundsTop);
			graphics2D.fill(borderBoundsLeft);
			graphics2D.fill(borderBoundsBottom);
			graphics2D.fill(borderBoundsRight);
		}
	};

	private PdfDocument pdfDocument;

	public void setPdfDocument(PdfDocument pdfDocument)
	{
		this.pdfDocument = pdfDocument;
		if (pdfDocument == null) {
			thumbnailPdfViewer.setPdfDocument(null);
			return;
		}

		// we force a OneDimensionalPdfDocument, because we have no idea, how the PdfDocument of the main viewer looks like.
		thumbnailPdfDocument = new OneDimensionalPdfDocument(
				pdfDocument.getPdfFile(),
				OneDimensionalPdfDocument.Layout.vertical,
				new NullProgressMonitor()
		);
		thumbnailPdfViewer.setPdfDocument(thumbnailPdfDocument);

	}

	public PdfViewer getThumbnailPdfViewer() {
		return thumbnailPdfViewer;
	}

	public void setThumbnailPdfViewer(PdfViewer pdfViewer) {
		this.thumbnailPdfViewer = pdfViewer;
	}

	public void setCurrentPage(int pageNumber) {
		thumbnailPdfViewer.setCurrentPage(pageNumber);
	}

}
