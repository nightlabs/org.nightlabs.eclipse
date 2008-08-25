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
public class PdfDocument {
	
	private static final int MARGIN_BETWEEN_PAGES = 20;
	private PDFFile pdfFile;	
	List<Integer> result = new ArrayList<Integer>();

	/**
	 * The bounds of the complete document, i.e. all pages laid down on a virtual floor. So we need to know the size
	 * of our floor in order to know the ranges of the scroll bars. This coordinate system starts at (0, 0) at the
	 * left top corner and the width + height is specified here.
	 */
	private Point2D.Double documentBounds;
	private List<Rectangle2D.Double> pageBounds;

	public PdfDocument(PDFFile pdfFile) {
		
		setPdfFile(pdfFile);
		
		documentBounds = new Point2D.Double();
		pageBounds = new ArrayList<Rectangle2D.Double>(pdfFile.getNumPages());
		
		documentBounds.setLocation(0, 0);
		double nextPageTop = 0;

		for (int j = 0; j < pdfFile.getNumPages(); j++) {					
			// get the corresponding PDF page and its properties (height and width), beginning with index one, not zero !!!!!
			PDFPage pdfPage = pdfFile.getPage(j + 1);
			double pdfPageWidth = pdfPage.getBBox().getWidth();		
//			System.out.println("page width: " + pdfPageWidth);
			double pdfPageHeight = pdfPage.getBBox().getHeight();   
//			System.out.println("page height: " + pdfPageHeight);			
			// maintain maximum width and height of all PDF pages
			if (documentBounds.x < pdfPageWidth)
				documentBounds.x = pdfPageWidth;
			// create a new rectangle for each page and insert it into its place in the virtual floor (dependent on the y-coordinate) 
			pageBounds.add(new Rectangle2D.Double(0, nextPageTop, pdfPageWidth, pdfPageHeight));
			nextPageTop += pdfPageHeight + MARGIN_BETWEEN_PAGES;			
		}

//		documentBounds.y += Math.max(0, nextPageTop - MARGIN_BETWEEN_PAGES);
		documentBounds.y += nextPageTop;

		// put all pages horizontally in the middle
		for (Rectangle2D.Double pageBound : pageBounds) {
			pageBound.x = documentBounds.x / 2 - pageBound.width / 2;
		}
	}

	/**
	 * Get the page numbers (1-based) of those pages that are partially or completely visible within the
	 * given bounds.
	 *
	 * @param bufferBounds the coordinates of the area of interest.
	 * @return the page numbers.
	 */
	public List<Integer> getVisiblePages(Rectangle2D bufferBounds)
	{		
		Rectangle2D.Double pageInTheMiddlePageBound;
		
		// (A) Algorithm 1: naive iteration through all pages
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
		int beginningOfInterval = 1;
		int endOfInterval = pdfFile.getNumPages();
		int pageNumber = (int)Math.ceil((double)pdfFile.getNumPages() / 2);
		pageInTheMiddlePageBound = pageBounds.get(pageNumber - 1);
		
		// 	ii) special case: the page includes the whole buffer (must be a big page) => almost nothing more to do
		if (pageInTheMiddlePageBound.contains(bufferBounds)) {
			List<Integer> result = new ArrayList<Integer>();
			result.add(pageNumber);
			return result;
		}
		
		// 	iii) recursive computation of nested intervals until a page is found that is (also partly) lying inside the given buffer bounds
		result.clear();
		computeNestedIntervals(beginningOfInterval, endOfInterval, pageNumber, bufferBounds);
		return result;
		
	}
	
	public void computeNestedIntervals (int beginningOfInterval, int endOfInterval, int pageNumber, Rectangle2D bufferBounds) {
		
		Rectangle2D.Double pageInTheMiddlePageBound;
		Rectangle2D.Double pageDownPageBound;
		Rectangle2D.Double pageUpPageBound;		
		boolean pageUpIsPartOfBuffer = true;
		boolean pageDownIsPartOfBuffer = true;
		System.out.println("interval: [" + beginningOfInterval + "," + endOfInterval + "]");
		
		if (pageNumber >= 1 && pageNumber <= pdfFile.getNumPages()) {
			pageInTheMiddlePageBound = pageBounds.get(pageNumber - 1);
		
			if (bufferBounds.contains(pageInTheMiddlePageBound) || bufferBounds.intersects(pageInTheMiddlePageBound)) {
				// a) the page in the middle is already lying inside the given buffer bounds => get neighbor pages lying in buffer, too  
				result.add(pageNumber);System.out.println("adding page " + pageNumber + " to result list");
				int pageNumberDown = pageNumber;
				int pageNumberUp = pageNumber;
				while (pageUpIsPartOfBuffer == true && pageNumberDown - 1 >= 1) {
					pageNumberDown -= 1;
					pageDownPageBound = pageBounds.get(pageNumberDown - 1);
					if (bufferBounds.contains(pageDownPageBound) || bufferBounds.intersects(pageDownPageBound)) {
						result.add(pageNumberDown);System.out.println("adding page " + pageNumberDown + " to result list");}
					else
						pageUpIsPartOfBuffer = false;
				}
				while (pageDownIsPartOfBuffer == true && pageNumberUp + 1 <= pdfFile.getNumPages()) {
					pageNumberUp += 1;
					pageUpPageBound = pageBounds.get(pageNumberUp - 1);
					if (bufferBounds.contains(pageUpPageBound) || bufferBounds.intersects(pageUpPageBound)) {
						result.add(pageNumberUp);System.out.println("adding page " + pageNumberUp + " to result list");}
					else
						pageDownIsPartOfBuffer = false;
				}
			}
			else {
				// b) the page in the middle is not lying inside the given buffer bounds => divide interval (down- or upwards)
				if (pageInTheMiddlePageBound.y + pageInTheMiddlePageBound.height < bufferBounds.getY()) {
					// go upwards (page numbers are getting higher)
					beginningOfInterval = pageNumber;
					pageNumber = pageNumber + (int)Math.ceil(((double)(endOfInterval - beginningOfInterval)) / 2);				
					// start again with smaller interval
					computeNestedIntervals(beginningOfInterval, endOfInterval, pageNumber, bufferBounds);
				}
				else {
					// bufferBounds.getY() + bufferBounds.getHeight() < pageInTheMiddlePageBound.y // it is not necessary to check this 
					// go downwards (page numbers are getting smaller)
					endOfInterval = pageNumber;
					pageNumber = pageNumber - (int)Math.ceil(((double)(endOfInterval - beginningOfInterval)) / 2);
					// start again with smaller interval
					computeNestedIntervals(beginningOfInterval, endOfInterval, pageNumber, bufferBounds);
				}
			}		
		}		
	}
	

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
	}
}
