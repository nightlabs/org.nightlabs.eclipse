package org.nightlabs.eclipse.ui.pdfviewer;

import java.awt.Graphics2D;
import java.util.EventObject;

public class PaintEvent extends EventObject
{
    private static final long serialVersionUID = 1L;

	private Graphics2D graphics2D;

	public PaintEvent(PdfViewer source, Graphics2D graphics2D) {
		super(source);
		this.graphics2D = graphics2D;
	}

	public Graphics2D getGraphics2D() {
	    return graphics2D;
    }

	@Override
	public PdfViewer getSource() {
	    return (PdfViewer) super.getSource();
	}
}
