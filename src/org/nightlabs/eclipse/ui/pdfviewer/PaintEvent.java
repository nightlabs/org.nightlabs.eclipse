package org.nightlabs.eclipse.ui.pdfviewer;

import java.awt.Graphics2D;
import java.util.EventObject;

/**
 * Event propagated to {@link PaintListener}s. It provides access to a {@link Graphics2D}
 * allowing listeners to draw.
 * 
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PaintEvent extends EventObject
{
    private static final long serialVersionUID = 1L;

	private Graphics2D graphics2D;

	public PaintEvent(PdfViewer source, Graphics2D graphics2D) {
		super(source);
		this.graphics2D = graphics2D;
	}

	/**
	 * Get access to the drawing area.
	 *
	 * @return the AWT API object to the drawing area.
	 */
	public Graphics2D getGraphics2D() {
	    return graphics2D;
    }

	@Override
	public PdfViewer getSource() {
	    return (PdfViewer) super.getSource();
	}
}
