package org.nightlabs.eclipse.ui.pdfviewer.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;


/**
 * One-dimensional implementation of {@link PdfDocument}. All pages are arranged either horizontally
 * or vertically (in a single row or a single column).
 *
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class OneDimensionalPdfDocument implements PdfDocument
{
	private static final Logger logger = Logger.getLogger(OneDimensionalPdfDocument.class);
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
	private Dimension2DDouble documentDimension;
	private PDFFile pdfFile;
	private List<Rectangle2D.Double> pageBounds;

	public OneDimensionalPdfDocument() { }

	public OneDimensionalPdfDocument(PDFFile pdfFile, IProgressMonitor monitor) {
		initPdfFile(pdfFile, monitor);
	}

	public OneDimensionalPdfDocument(PDFFile pdfFile, Layout layout, IProgressMonitor monitor) {
		this.layout = layout;
		initPdfFile(pdfFile, monitor);
	}

	/**
	 * Get all PDF pages of the PDF document, create a new rectangle for each page and insert it
	 * into its place in the virtual floor starting with index one (not zero!).
	 *
	 * @param monitor a sub progress monitor showing the progress of getting all PDF pages of the given PDF file.
	 */
	private void readPdf(IProgressMonitor monitor)
	{
		monitor.beginTask("Reading PDF file", pdfFile.getNumPages());
		try {
			documentDimension = new Dimension2DDouble(0, 0);
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
						if (documentDimension.getWidth() < pdfPageWidth) {
							documentDimension.setWidth(pdfPageWidth);
						}
						pageBounds.add(new Rectangle2D.Double(0, nextPageTop, pdfPageWidth, pdfPageHeight));
						nextPageTop += pdfPageHeight + MARGIN;

						monitor.worked(1);
					}

					documentDimension.setHeight(nextPageTop);
					documentDimension.setWidth(documentDimension.getWidth() + MARGIN * 2);

					// put all pages horizontally in the middle
					for (Rectangle2D.Double pageBound : pageBounds) {
						pageBound.x = documentDimension.getWidth() / 2 - pageBound.width / 2;
					}
					break;

				case horizontal:
					double nextPageLeft = MARGIN;

					for (int j = 0; j < pdfFile.getNumPages(); j++) {
						PDFPage pdfPage = pdfFile.getPage(j + 1);
						double pdfPageWidth = pdfPage.getBBox().getWidth();
						double pdfPageHeight = pdfPage.getBBox().getHeight();
						if (documentDimension.getHeight() < pdfPageHeight) {
							documentDimension.setHeight(pdfPageHeight);
						}
						pageBounds.add(new Rectangle2D.Double(nextPageLeft, 0, pdfPageWidth, pdfPageHeight));
						nextPageLeft += pdfPageWidth + MARGIN;

						monitor.worked(1);
					}

					documentDimension.setWidth(nextPageLeft);
					documentDimension.setHeight(documentDimension.getHeight() + MARGIN * 2);

					// put all pages horizontally in the middle
					for (Rectangle2D.Double pageBound : pageBounds) {
						pageBound.y = documentDimension.getHeight() / 2 - pageBound.height / 2;
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
	 * @see org.nightlabs.eclipse.ui.pdfviewer.model.PdfDocument#getVisiblePages(java.awt.geom.Rectangle2D)
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
	 * algorithm is implemented, but this might change depending on how the OneDimensionalPdfDocument lays out its
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
	 * @see org.nightlabs.eclipse.ui.pdfviewer.model.PdfDocument#getPageBounds(int)
	 */
	@Override
	public Rectangle2D getPageBounds(int pageNumber) {
		return pageBounds.get(pageNumber - 1);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfviewer.model.PdfDocument#getDocumentBounds()
	 */
	@Override
	public Dimension2D getDocumentDimension() {
		return documentDimension;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfviewer.model.PdfDocument#getPdfFile()
	 */
	@Override
	public PDFFile getPdfFile() {
		return pdfFile;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.pdfviewer.model.PdfDocument#setPdfFile(com.sun.pdfview.PDFFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initPdfFile(PDFFile pdfFile, IProgressMonitor monitor) {
		if (this.pdfFile != null)
			throw new IllegalStateException("A PDF file has already been assigned! This method cannot be called again!");

		this.pdfFile = pdfFile;
		readPdf(monitor);
	}

	public Layout getLayout() {
		return layout;
	}
}
