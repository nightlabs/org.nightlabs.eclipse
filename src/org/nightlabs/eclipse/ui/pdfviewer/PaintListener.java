package org.nightlabs.eclipse.ui.pdfviewer;

import java.awt.Graphics2D;

/**
 * Implementations of this interface can draw into the PDF viewer's drawing area or into the buffer,
 * depending on which way they were registered. See these methods:
 * <ul>
 * <li>{@link PdfViewer#addPaintToBufferListener(PaintListener)}</li>
 * <li>{@link PdfViewer#removePaintToBufferListener(PaintListener)}</li>
 * <li>{@link PdfViewer#addPaintToViewListener(PaintListener)}</li>
 * <li>{@link PdfViewer#removePaintToViewListener(PaintListener)}</li>
 * </ul>
 *
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public interface PaintListener {
	/**
	 * Paint before the {@link PdfViewer} paints.
	 *
	 * @param event provides information about the paint situation - especially access to a {@link Graphics2D}.
	 */
	void prePaint(PaintEvent event);
	/**
	 * Paint after the {@link PdfViewer} painted.
	 *
	 * @param event provides information about the paint situation - especially access to a {@link Graphics2D}.
	 */
	void postPaint(PaintEvent event);
}
