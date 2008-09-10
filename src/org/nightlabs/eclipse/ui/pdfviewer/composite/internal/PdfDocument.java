package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;


/**
 * Wrapper around a {@link PDFFile} with additional meta-data. An instance of this class knows the
 * real coordinate system of the complete viewing area including each page's position and bounds and
 * some other meta-data.
 *
 * @author frederik löser - frederik at nightlabs dot de
 */
public class PdfDocument
{
	private static final Logger logger = Logger.getLogger(PdfDocument.class);
	private static final int MARGIN_BETWEEN_PAGES = 20; // DOT = 1/72 inch
	/**
	 * The bounds of the complete document, i.e. all pages laid down on a virtual floor. So we need to know the size
	 * of our floor in order to know the ranges of the scroll bars. This coordinate system starts at (0, 0) at the
	 * top left corner and the width + height is specified here.
	 */
	private Point2D.Double documentBounds;
	private PDFFile pdfFile;
//	private List<Integer> result;
	private List<Rectangle2D.Double> pageBounds;
	private double nextPageTop;

	public PdfDocument(PDFFile pdfFile) {
		setPdfFile(pdfFile);
	}

	/**
	 * Get all PDF pages of the PDF document, create a new rectangle for each page and insert it
	 * into its place in the virtual floor starting with index one (not zero!)
	 */
	private void readPdfDocumentProperties() {
		nextPageTop = MARGIN_BETWEEN_PAGES;
		documentBounds = new Point2D.Double(0,0);
		pageBounds = new ArrayList<Rectangle2D.Double>(pdfFile.getNumPages());

		for (int j = 0; j < pdfFile.getNumPages(); j++) {
			PDFPage pdfPage = pdfFile.getPage(j + 1);
			double pdfPageWidth = pdfPage.getBBox().getWidth();
			double pdfPageHeight = pdfPage.getBBox().getHeight();
			if (documentBounds.x < pdfPageWidth) {
				documentBounds.x = pdfPageWidth;
			}
			pageBounds.add(new Rectangle2D.Double(0, nextPageTop, pdfPageWidth, pdfPageHeight));
			nextPageTop += pdfPageHeight + MARGIN_BETWEEN_PAGES;
		}

		documentBounds.y += nextPageTop;
//		documentBounds.y += Math.max(0, nextPageTop - MARGIN_BETWEEN_PAGES);

		// put all pages horizontally in the middle
		for (Rectangle2D.Double pageBound : pageBounds) {
			pageBound.x = documentBounds.x / 2 - pageBound.width / 2;
		}

		if (logger.isDebugEnabled()) {
			int pageNumber = 0;
			for (Rectangle2D.Double page : pageBounds) {
				logger.debug("readPdfDocumentProperties: page " + (++pageNumber) + ": x=" + page.getX() + " y=" + page.getY() + " w=" + page.getWidth() + " h=" + page.getHeight());
			}
		}


//		findAnyVisiblePage(new Rectangle2D.Double(0.0d, 16724.599999999995d, 3840.0d, 2048.0d));
	}

	/**
	 * Get a sorted list of page numbers (one-based) of those pages that are partially or completely visible within the
	 * given bounds.
	 *
	 * @param bufferBounds coordinates of the area of interest.
	 * @return a sorted list (smallest page number first) of those page numbers visible in the given buffer bounds.
	 */
	public List<Integer> getVisiblePages(Rectangle2D bufferBounds) {
//		Rectangle2D.Double pageInTheMiddlePageBound;
//		Rectangle2D pageInTheMiddlePageBoundZoomed = new java.awt.Rectangle(0, 0, 0, 0);
		List<Integer> result = new ArrayList<Integer>();
//		Rectangle2D bufferBoundsZoomed = null;

		// (A) Algorithm 1: iteration through all pages (not used anymore)
/* 		{
 			List<Integer> result = new ArrayList<Integer>();
			int pageNumber = 0;
			for (Rectangle2D.Double pageBound : pageBounds) {
				++pageNumber;
				if (pageBound.contains(bufferBounds) || bufferBounds.contains(pageBound) || bufferBounds.intersects(pageBound))
					result.add(pageNumber);
			}
		}*/

		// (B) Algorithm 2: use nested intervals (Intervallschachtelung)
		// 	i) initialization
//		int beginningOfInterval = 1;
//		int endOfInterval = pdfFile.getNumPages();
//		int intervalCenterPageNumber = (int)Math.ceil((double)pdfFile.getNumPages() / 2);

		// 	ii) special case: the page itself includes the whole buffer => almost nothing more to do
//		double direction = 0;
//		if (zoomFactor >= 0.2 && zoomFactor <= 0.8)
//			direction = 1;
//		else
//			if (zoomFactor >= 1.2 && zoomFactor <= 2)
//				direction = - 1;
//		pageInTheMiddlePageBound = pageBounds.get(intervalCenterPageNumber - 1);
//		pageInTheMiddlePageBoundZoomed.setRect(	pageInTheMiddlePageBound.getX() + direction * Math.abs(((zoomFactor - 1) * pageInTheMiddlePageBound.width)) / 2,
//				pageInTheMiddlePageBound.getY() * zoomFactor,
//				pageInTheMiddlePageBound.getWidth() * zoomFactor,
//				pageInTheMiddlePageBound.getHeight() * zoomFactor
//				);
//		if (pageInTheMiddlePageBoundZoomed.contains(bufferBounds)) {
//			result.add(intervalCenterPageNumber);
//			return result;
//		}
//
//		// 	iii) recursive computation of nested intervals until a page is found that is (also partly) lying inside the given buffer bounds.
//		// 		Neighbor pages of this page are then checked for the same condition
//		result.clear();
//		computeNestedIntervals(beginningOfInterval, endOfInterval, intervalCenterPageNumber, bufferBounds, zoomFactor);
//		return result;

		int anyVisiblePage = findAnyVisiblePage(bufferBounds);
		if (logger.isDebugEnabled()) {
			logger.debug("getVisiblePages: anyVisiblePage=" + anyVisiblePage);
		}

		int firstVisibleIdx = anyVisiblePage - 1;
		if (firstVisibleIdx > 0) {
			Rectangle2D.Double page = pageBounds.get(firstVisibleIdx - 1);
			while (page != null && (page.contains(bufferBounds) || bufferBounds.contains(page) || bufferBounds.intersects(page))) {
				--firstVisibleIdx;
				page = firstVisibleIdx - 1 < 0 ? null : pageBounds.get(firstVisibleIdx - 1);
			}
		}

		int idx = firstVisibleIdx;
		Rectangle2D.Double page = pageBounds.get(idx);
		while (page != null && (page.contains(bufferBounds) || bufferBounds.contains(page) || bufferBounds.intersects(page))) {
			++idx;
			result.add(idx);
			page = idx < pageBounds.size() ? pageBounds.get(idx) : null;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getVisiblePages: returning " + result.size() + " page numbers for bufferBounds=" + bufferBounds);
			for (Integer pageNumber : result) {
				logger.debug("getVisiblePages: * " + pageNumber);
			}
		}

		return result;
	}

	private int findAnyVisiblePage(Rectangle2D bufferBounds)
	{
		int beginIdx = 0;
		int endIdx = pageBounds.size() - 1;

		while (endIdx - beginIdx > 1) {
			int middleIdx = (beginIdx + endIdx) / 2;
			Rectangle2D.Double middlePage = pageBounds.get(middleIdx);

			if (middlePage.contains(bufferBounds) || bufferBounds.contains(middlePage) || bufferBounds.intersects(middlePage))
				return middleIdx + 1;

			if (middlePage.getMinY() > bufferBounds.getMaxY())
				endIdx = middleIdx;
			else
				beginIdx = middleIdx;
		}

		throw new IllegalStateException("No page found!");
//		return beginIdx + 1;
	}


//	/**
//	 * Implementation of nested intervals.
//	 *
//	 * @param beginningOfInterval starting point of the currently considered interval
//	 * @param endOfInterval end point of the currently considered interval
//	 * @param pageNumber page in the middle of the currently considered interval
//	 * @param bufferBounds coordinates of the area of interest
//	 * @param zoomFactor
//	 */
//	private void computeNestedIntervals(
//			int beginningOfInterval,
//			int endOfInterval,
//			int intervalCenterPageNumber,
//			Rectangle2D bufferBounds)
//	{
//		Rectangle2D.Double pageInTheMiddlePageBound;
//		Rectangle2D.Double pageDownPageBound;
//		Rectangle2D.Double pageUpPageBound;
//		Rectangle2D pageInTheMiddlePageBoundZoomed = new java.awt.Rectangle(0, 0, 0, 0);
//		Rectangle2D pageDownPageBoundZoomed = new java.awt.Rectangle(0, 0, 0, 0);
//		Rectangle2D pageUpPageBoundZoomed = new java.awt.Rectangle(0, 0, 0, 0);
//		boolean pageAboveIsPartOfBuffer = true;
//		boolean pageBelowIsPartOfBuffer = true;
//		double direction = 0;
//
////		Logger.getRootLogger().info("interval: [" + beginningOfInterval + "," + endOfInterval + "]");
//
//		if (intervalCenterPageNumber >= 1 && intervalCenterPageNumber <= pdfFile.getNumPages()) {
//			pageInTheMiddlePageBound = pageBounds.get(intervalCenterPageNumber - 1);
//
//			if (zoomFactor >= 0.2 && zoomFactor <= 0.8)
//				direction = 1;
//			else
//				if (zoomFactor >= 1.2 && zoomFactor <= 2)
//					direction = - 1;
//
//			pageInTheMiddlePageBoundZoomed.setRect(	pageInTheMiddlePageBound.getX() + direction * Math.abs(((zoomFactor - 1) * pageInTheMiddlePageBound.width)) / 2,
//													pageInTheMiddlePageBound.getY() * zoomFactor,
//													pageInTheMiddlePageBound.getWidth() * zoomFactor,
//													pageInTheMiddlePageBound.getHeight() * zoomFactor
//													);
//
//
//			if (bufferBounds.contains(pageInTheMiddlePageBoundZoomed) || bufferBounds.intersects(pageInTheMiddlePageBoundZoomed)) {
//				// a) the page in the middle is already lying inside the given buffer bounds => get neighbor pages lying in buffer, too
//				result.add(intervalCenterPageNumber);
//				Logger.getRootLogger().info("adding page " + intervalCenterPageNumber + " to result list");
//				int pageNumberDown = intervalCenterPageNumber;
//				int pageNumberUp = intervalCenterPageNumber;
//				while (pageAboveIsPartOfBuffer == true && pageNumberDown - 1 >= 1) {
//					pageNumberDown -= 1;
//					pageDownPageBound = pageBounds.get(pageNumberDown - 1);
//
//					pageDownPageBoundZoomed.setRect(	pageDownPageBound.getX() + direction * Math.abs(((zoomFactor - 1) * pageDownPageBound.width)) / 2,
//														pageDownPageBound.getY() * zoomFactor,
//														pageDownPageBound.getWidth() * zoomFactor,
//														pageDownPageBound.getHeight() * zoomFactor
//													);
//
//					if (bufferBounds.contains(pageDownPageBoundZoomed) || bufferBounds.intersects(pageDownPageBoundZoomed)) {
//						result.add(pageNumberDown);
//						Logger.getRootLogger().info("adding page " + pageNumberDown + " to result list");
//					}
//					else
//						pageAboveIsPartOfBuffer = false;
//				}
//				while (pageBelowIsPartOfBuffer == true && pageNumberUp + 1 <= pdfFile.getNumPages()) {
//					pageNumberUp += 1;
//					pageUpPageBound = pageBounds.get(pageNumberUp - 1);
//
//					pageUpPageBoundZoomed.setRect(	pageUpPageBound.getX() + direction * Math.abs(((zoomFactor - 1) * pageUpPageBound.width)) / 2,
//													pageUpPageBound.getY() * zoomFactor,
//													pageUpPageBound.getWidth() * zoomFactor,
//													pageUpPageBound.getHeight() * zoomFactor
//													);
//
//					if (bufferBounds.contains(pageUpPageBoundZoomed) || bufferBounds.intersects(pageUpPageBoundZoomed)) {
//						result.add(pageNumberUp);
//						Logger.getRootLogger().info("adding page " + pageNumberUp + " to result list");
//					}
//					else
//						pageBelowIsPartOfBuffer = false;
//				}
//			}
//			else {
//				if (intervalCenterPageNumber + 1 > pdfFile.getNumPages())
//					return;
//				// b) the page in the middle is not lying inside the given buffer bounds => divide interval again (down- or upwards)
//				if (pageInTheMiddlePageBoundZoomed.getY() + pageInTheMiddlePageBoundZoomed.getHeight() < bufferBounds.getY()) {
//					// go upwards (page numbers are getting higher)
//					beginningOfInterval = intervalCenterPageNumber;
//					intervalCenterPageNumber = intervalCenterPageNumber + (int)Math.ceil(((double)(endOfInterval - beginningOfInterval)) / 2);
//					// start again with smaller interval
//					computeNestedIntervals(beginningOfInterval, endOfInterval, intervalCenterPageNumber, bufferBounds, zoomFactor);
//				}
//				else {
//					// bufferBounds.getY() + bufferBounds.getHeight() < pageInTheMiddlePageBound.y // it is not necessary to check this
//					// go downwards (page numbers are getting smaller)
//					endOfInterval = intervalCenterPageNumber;
//					intervalCenterPageNumber = intervalCenterPageNumber - (int)Math.ceil(((double)(endOfInterval - beginningOfInterval)) / 2);
//					// start again with smaller interval
//					computeNestedIntervals(beginningOfInterval, endOfInterval, intervalCenterPageNumber, bufferBounds, zoomFactor);
//				}
//			}
//		}
//	}



	public Rectangle2D.Double getPageBounds(int pageNumber) {
		return pageBounds.get(pageNumber - 1);
	}

	public void setPageBounds(List<Rectangle2D.Double> pageBounds) {
		this.pageBounds = pageBounds;
	}

	public Point2D.Double getDocumentBounds() {
		return documentBounds;
	}

	public void setDocumentBounds(Point2D.Double documentBounds) {
		this.documentBounds = documentBounds;
	}

	public PDFFile getPdfFile() {
		return pdfFile;
	}

	public void setPdfFile(PDFFile pdfFile) {
		this.pdfFile = pdfFile;
		readPdfDocumentProperties();
	}

//	public List<Integer> getResult() {
//		return result;
//	}
//
//	public void setResult(List<Integer> result) {
//		this.result = result;
//	}

	public static int getMARGIN_BETWEEN_PAGES() {
		return MARGIN_BETWEEN_PAGES;
	}

	public List<Rectangle2D.Double> getPageBounds() {
		return pageBounds;
	}


}
