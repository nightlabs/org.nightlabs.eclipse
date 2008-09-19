package org.nightlabs.eclipse.ui.pdfviewer;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;


public abstract class AbstractPdfDocument implements PdfDocument {

	private static final Logger logger = Logger.getLogger(AbstractPdfDocument.class);
	private int visiblePageArea;
	private int visiblePageAreaMax;
	private int mostVisiblePage;


	@Override
    public int getMostVisiblePage(Rectangle2D viewBounds) {

		Collection<Integer> visiblePages = getVisiblePages(viewBounds);
		visiblePageAreaMax = 0;

		if (logger.isDebugEnabled()) {
			for (Iterator<Integer> pageIterator = visiblePages.iterator(); pageIterator.hasNext();) {
				int pageNumber = pageIterator.next();
				logger.debug(pageNumber);
			}
		}
		// only return the first number in the collection (not used anymore)
/*		if (visiblePages.isEmpty())
			return -1;
		else
			return visiblePages.iterator().next();*/


		// Get the page number (1-based) of that page that fills out the largest part of the given bounds (in percentage)
		// in comparison to all other pages that are also partly or wholly visible within the given bounds.

		if (visiblePages.isEmpty())
			return -1;

		for (Iterator<Integer> pageIterator = visiblePages.iterator(); pageIterator.hasNext();) {
			int pageNumber = pageIterator.next();

			if (visiblePages.size() == 1)
				return pageNumber;

			Rectangle2D pageBounds = getPageBounds(pageNumber);

			for (int situation = 0; situation < 2; ++situation)
			{
				switch (situation) {
	                case 0:
	                	if (viewBounds.contains(pageBounds)) {
	                		visiblePageArea = (int) (pageBounds.getWidth() * pageBounds.getHeight());
	                		if (logger.isDebugEnabled())
	                			logger.debug("Page number: " + pageNumber + "; visible page area: " + visiblePageArea);
	                	}
		            break;
	                case 1:
	                	if (viewBounds.intersects(pageBounds)) {

	                		double x = Math.max(pageBounds.getX(), viewBounds.getX());
	                		double y = Math.max(pageBounds.getY(), viewBounds.getY());
	                		double width = Math.min(pageBounds.getMaxX() - x, viewBounds.getMaxX() - x);
	                		double height = Math.min(pageBounds.getMaxY() - y, viewBounds.getMaxY() - y);

	                		visiblePageArea = (int) (width * height);
	                		if (logger.isDebugEnabled())
	                			logger.debug("Page number: " + pageNumber + "; visible page area: " + visiblePageArea);
	                	}
		            break;
	                default:
		                throw new IllegalStateException("situation unknown: " + situation);

	            }
			}

			if (visiblePageArea >= visiblePageAreaMax) {

				if ((visiblePageArea == visiblePageAreaMax && pageNumber < mostVisiblePage) || (visiblePageArea > visiblePageAreaMax)) {
					mostVisiblePage = pageNumber;
					visiblePageAreaMax = visiblePageArea;
				}
			}
		}

		return mostVisiblePage;

    }

}
