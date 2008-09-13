package org.nightlabs.eclipse.ui.pdfviewer.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.sun.pdfview.PDFFile;

/**
 * Implementations of this interface define where the pages of a {@link PDFFile} are located
 * in the viewing area. Therefore, an object of this interface declares a so-called <i>real
 * coordinate system</i> of the complete viewing area including each page's position and bounds.
 * <p>
 * Implementors are free in how they layout their document. Pages can be simply horizontally
 * (or vertically) spread in one row (or column) like it is done by {@link OneDimensionalPdfDocument} or
 * more complex layouts can be implemented (e.g. a table with multiple columns and rows).
 * </p>
 * <p>
 * The real coordinate system uses a resolution of 1/72 inch per DOT as defined by the PDF standard.
 * Thus, for example a DIN A 4 page (210 mm * 297 mm) has a width of
 * 595.28 DOT = 210 mm / 25.4 (mm/inch) * 72 (DOT/inch) and a
 * height of 841.89 DOT = 297 mm / 25.4 (mm/inch) * 72 (DOT/inch).
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public interface PdfDocument {

	/**
	 * Get the page numbers (1-based) of those pages that are partially or completely visible within the
	 * given bounds. The pages are rendered in the order the resulting <code>Collection</code> iterates.
	 * This is only relevant, if pages are overlapping (then the last page is top, i.e. every page is above
	 * the previous one).
	 *
	 * @param bounds coordinates of the area of interest in the real coordinate system.
	 * @return a list of page numbers of those pages visible in the given bounds.
	 */
	Collection<Integer> getVisiblePages(Rectangle2D bounds);

	Rectangle2D getPageBounds(int pageNumber);

	Point2D getDocumentBounds();

	PDFFile getPdfFile();

	/**
	 * Read the given {@link PDFFile} and calculate the layout. This operation can be long-going
	 * and should indicate its progress via the given <code>monitor</code>. The method should
	 * return only after the complete file has been read and the document-layout is done.
	 * <p>
	 * This method can be called multiple times with different PDF files. Each time it is called,
	 * it should dispose of all its contents and replace them by the new data.
	 * </p>
	 * <p>
	 * This method is usually called on a {@link Job}'s thread. It does not need to be synchronized,
	 * because it is guaranteed that no other method of the <code>PdfDocument</code> is called
	 * while this method is running.
	 * </p>
	 *
	 * @param pdfFile the {@link PDFFile} to be displayed.
	 * @param monitor the progress monitor for progress status feedback.
	 */
	void setPdfFile(PDFFile pdfFile, IProgressMonitor monitor);
}