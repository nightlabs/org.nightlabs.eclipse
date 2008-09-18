package org.nightlabs.eclipse.ui.pdfviewer;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

public abstract class AbstractPdfDocument implements PdfDocument {

	@Override
    public int getMostVisiblePage(Rectangle2D bounds) {
		Collection<Integer> visiblePages = getVisiblePages(bounds);

		// TODO implement correctly!

		if (visiblePages.isEmpty())
			return -1;
		else
			return visiblePages.iterator().next();
    }

}
