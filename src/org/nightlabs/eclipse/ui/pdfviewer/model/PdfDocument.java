package org.nightlabs.eclipse.ui.pdfviewer.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;


/**
 * Wrapper around a {@link PDFFile} with additional meta-data. An instance of this class knows the
 * real coordinate system of the complete viewing area including each page's position and bounds and
 * some other meta-data.
 *
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfDocument implements IPdfDocument
{
	private static final Logger logger = Logger.getLogger(PdfDocument.class);
	private static final int MARGIN = 20; // DOT = 1/72 inch

	public enum Layout {
		horizontal,
		vertical
	}

	private Layout layout = Layout.vertical;

	/**
	 * The bounds of the complete document, i.e. all pages laid down on a virtual floor. So we need to know the size
	 * of our floor in order to know the ranges of the scroll bars. This coordinate system starts at (0, 0) at the
	 * top left corner and the width + height is specified here.
	 */
	private Point2D.Double documentBounds;
	private PDFFile pdfFile;
	private List<Rectangle2D.Double> pageBounds;

	public PdfDocument(PDFFile pdfFile, IProgressMonitor monitor) {
		setPdfFile(pdfFile, monitor);
	}

	public PdfDocument(PDFFile pdfFile, Layout layout, IProgressMonitor monitor) {
		this.layout = layout;
		setPdfFile(pdfFile, monitor);
	}

	/**
	 * Get all PDF pages of the PDF document, create a new rectangle for each page and insert it
	 * into its place in the virtual floor starting with index one (not zero!).
	 *
	 * @param monitor a sub progress monitor showing the progress of getting all PDF pages of the given PDF file.
	 */
	private void readPdf(IProgressMonitor monitor) {
		monitor.beginTask("Reading PDF file", pdfFile.getNumPages());
		try {

			documentBounds = new Point2D.Double(0,0);
			pageBounds = new ArrayList<Rectangle2D.Double>(pdfFile == null ? 0 : pdfFile.getNumPages());

			if (pdfFile == null)
				return;

			switch (layout) {
				case vertical:
					double nextPageTop = MARGIN;

					for (int j = 0; j < pdfFile.getNumPages(); j++) {
						PDFPage pdfPage = pdfFile.getPage(j + 1);
						double pdfPageWidth = pdfPage.getBBox().getWidth();
						double pdfPageHeight = pdfPage.getBBox().getHeight();
						if (documentBounds.x < pdfPageWidth) {
							documentBounds.x = pdfPageWidth;
						}
						pageBounds.add(new Rectangle2D.Double(0, nextPageTop, pdfPageWidth, pdfPageHeight));
						nextPageTop += pdfPageHeight + MARGIN;

						monitor.worked(1);
					}

					documentBounds.y = nextPageTop;
					documentBounds.x += MARGIN * 2;

					// put all pages horizontally in the middle
					for (Rectangle2D.Double pageBound : pageBounds) {
						pageBound.x = documentBounds.x / 2 - pageBound.width / 2;
					}
					break;

				case horizontal:
					double nextPageLeft = MARGIN;

					for (int j = 0; j < pdfFile.getNumPages(); j++) {
						PDFPage pdfPage = pdfFile.getPage(j + 1);
						double pdfPageWidth = pdfPage.getBBox().getWidth();
						double pdfPageHeight = pdfPage.getBBox().getHeight();
						if (documentBounds.y < pdfPageHeight) {
							documentBounds.y = pdfPageHeight;
						}
						pageBounds.add(new Rectangle2D.Double(nextPageLeft, 0, pdfPageWidth, pdfPageHeight));
						nextPageLeft += pdfPageWidth + MARGIN;

						monitor.worked(1);
					}

					documentBounds.x = nextPageLeft;
					documentBounds.y += MARGIN * 2;

					// put all pages horizontally in the middle
					for (Rectangle2D.Double pageBound : pageBounds) {
						pageBound.y = documentBounds.y / 2 - pageBound.height / 2;
					}
					break;

				default:
					throw new IllegalStateException("Unknown layout: " + layout);
			}

			if (logger.isDebugEnabled()) {
				int pageNumber = 0;
				for (Rectangle2D.Double page : pageBounds) {
					logger.debug("readPdfDocumentProperties: page " + (++pageNumber) + ": x=" + page.getX() + " y=" + page.getY() + " w=" + page.getWidth() + " h=" + page.getHeight());
				}
			}

		} finally {
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfviewer.model.IPdfDocument#getVisiblePages(java.awt.geom.Rectangle2D)
	 */
	@Override
	public Collection<Integer> getVisiblePages(Rectangle2D bounds) {
		List<Integer> result = new ArrayList<Integer>();

		int firstVisibleIdx = -1; // the page index (0-based) of the first page (i.e. smallest page number) that is visible.

		int anyVisiblePage = findVisiblePage(bounds);
		if (logger.isDebugEnabled())
			logger.debug("getVisiblePages: anyVisiblePage=" + anyVisiblePage);

		if (anyVisiblePage < 0) {
			logger.warn("getVisiblePages: findVisiblePage(...) found none! Using expensive full scan!");
			for (int pageIdx = 0; pageIdx < pageBounds.size(); ++pageIdx) {
				if (isPageVisible(pageBounds.get(pageIdx), bounds)) {
					firstVisibleIdx = pageIdx;
					anyVisiblePage = firstVisibleIdx + 1;
					break;
				}
			}
			if (anyVisiblePage < 0) {
				logger.warn("getVisiblePages: No page is visible!");
				return result;
			}
		}
		else {
			firstVisibleIdx = anyVisiblePage - 1;
			if (firstVisibleIdx > 0) {
				Rectangle2D.Double page = pageBounds.get(firstVisibleIdx - 1);
				while (page != null && (page.contains(bounds) || bounds.contains(page) || bounds.intersects(page))) {
					--firstVisibleIdx;
					page = firstVisibleIdx - 1 < 0 ? null : pageBounds.get(firstVisibleIdx - 1);
				}
			}
		}

		int idx = firstVisibleIdx;
		Rectangle2D.Double page = pageBounds.get(idx);
		while (page != null && isPageVisible(page, bounds)) {
			++idx;
			result.add(idx);
			page = idx < pageBounds.size() ? pageBounds.get(idx) : null;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getVisiblePages: returning " + result.size() + " page numbers for bufferBounds=" + bounds);
			for (Integer pageNumber : result) {
				logger.debug("getVisiblePages: * " + pageNumber);
			}
		}

		return result;
	}

	/**
	 * Get any visible page by using an intelligent search strategy (at the moment, a nested interval
	 * algorithm is implemented, but this might change depending on how the PdfDocument lays out its
	 * pages).
	 *
	 * @param bounds the bounds within which the searched page is at least partially visible.
	 * @return a 1-based page number or -1 if no page could be found.
	 */
	private int findVisiblePage(Rectangle2D bounds)
	{
		int beginIdx = 0;
		int endIdx = pageBounds.size() - 1;

		if (isPageVisible(pageBounds.get(beginIdx), bounds))
			return beginIdx + 1;

		if (isPageVisible(pageBounds.get(endIdx), bounds))
			return endIdx + 1;

		int middleIdx = -1;
		while (endIdx - beginIdx > 1) {
			middleIdx = (beginIdx + endIdx) / 2;
			Rectangle2D.Double middlePage = pageBounds.get(middleIdx);

			if (isPageVisible(middlePage, bounds))
				return middleIdx + 1;

			switch (layout) {
				case vertical:
					if (middlePage.getMinY() > bounds.getMaxY())
						endIdx = middleIdx;
					else
						beginIdx = middleIdx;
				break;

				case horizontal:
					if (middlePage.getMinX() > bounds.getMaxX())
						endIdx = middleIdx;
					else
						beginIdx = middleIdx;
				break;

				default:
					throw new IllegalStateException("Unknown layout: " + layout);
			}
		}

		if (middleIdx != beginIdx && isPageVisible(pageBounds.get(beginIdx), bounds))
			return beginIdx + 1;

		if (middleIdx != endIdx && isPageVisible(pageBounds.get(endIdx), bounds))
			return endIdx + 1;

		return -1;
	}

	private boolean isPageVisible(Rectangle2D page, Rectangle2D bounds)
	{
		return page.contains(bounds) || bounds.contains(page) || bounds.intersects(page);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfviewer.model.IPdfDocument#getPageBounds(int)
	 */
	@Override
	public Rectangle2D getPageBounds(int pageNumber) {
		return pageBounds.get(pageNumber - 1);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfviewer.model.IPdfDocument#getDocumentBounds()
	 */
	@Override
	public Point2D getDocumentBounds() {
		return documentBounds;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfviewer.model.IPdfDocument#getPdfFile()
	 */
	@Override
	public PDFFile getPdfFile() {
		return pdfFile;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfviewer.model.IPdfDocument#setPdfFile(com.sun.pdfview.PDFFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void setPdfFile(PDFFile pdfFile, IProgressMonitor monitor) {
		this.pdfFile = pdfFile;
		readPdf(monitor);
	}

	public Layout getLayout() {
		return layout;
	}
}
