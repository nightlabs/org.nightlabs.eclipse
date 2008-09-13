package org.nightlabs.eclipse.ui.pdfviewer.model;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
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
public interface PdfDocument
{
	/**
	 * Get the page numbers (1-based) of those pages that are partially or completely visible within the
	 * given bounds. The pages are rendered in the order the resulting <code>Collection</code> iterates.
	 * This is only relevant, if pages are overlapping (then the last page is top, i.e. every page is
	 * rendered above the previous one).
	 * <p>
	 * This method is called very often and should be optimized to be fast! It should therefore use an index
	 * or a fast search algorithm like nested intervals or similar. When using an index, build it already
	 * in the {@link #setPdfFile(PDFFile, IProgressMonitor)} method - not lazily here!
	 * </p>
	 *
	 * @param bounds coordinates of the area of interest in the real coordinate system.
	 * @return a list of page numbers of those pages visible in the given bounds.
	 */
	Collection<Integer> getVisiblePages(Rectangle2D bounds);

	/**
	 * Get the page bounds (i.e. location and size) in real coordinates (absolute in the coordinate
	 * system of the <code>PdfDocument</code> implementation) of the specified page.
	 *
	 * @param pageNumber the 1-based page number.
	 * @return the bounds of the specified page. {@link Rectangle2D#getX()} and {@link Rectangle2D#getY()}
	 * specify the top left corner of the page; {@link Rectangle2D#getWidth()} and {@link Rectangle2D#getHeight()}
	 * specify the page's size.
	 */
	Rectangle2D getPageBounds(int pageNumber);

	/**
	 * Get the size of the complete document. Since the document always starts with (0, 0) being
	 * the top left corner in the coordinate system, there's no location but only a size.
	 * <p>
	 * It's recommended to use {@link Dimension2DDouble} if you need <code>double</code> values.
	 * If <code>int</code> values are sufficient, you could alternatively use {@link Dimension}.
	 * </p>
	 *
	 * @return the size of the document in DOT (1 DOT = 1/72 inch).
	 */
	Dimension2D getDocumentDimension();

	/**
	 * Get the {@link PDFFile} that was previously set by {@link #setPdfFile(PDFFile, IProgressMonitor)}
	 * or <code>null</code> if that method was not yet called (and a {@link PDFFile} hasn't been passed
	 * to the constructor, either).
	 *
	 * @return the {@link PDFFile} of the current document.
	 */
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
	 * <p>
	 * When you need a second <code>PdfDocument</code> instance (e.g. for a small thumbnail-navigator),
	 * you should pass the same {@link PDFFile} instance to a new instance of {@link PdfDocument}.
	 * Re-iterating a {@link PDFFile} a second time is fast, because it seems to cache its data (I tried it
	 * and had durations like 2793 msec for the first and 2 msec for the 2nd iteration).
	 * </p>
	 *
	 * @param pdfFile the {@link PDFFile} to be displayed.
	 * @param monitor the progress monitor for progress status feedback.
	 */
	void setPdfFile(PDFFile pdfFile, IProgressMonitor monitor);
}