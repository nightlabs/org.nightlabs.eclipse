package org.nightlabs.eclipse.ui.pdfviewer;

/**
 * Abstract base class for the convenient implementation of {@link PaintListener}s
 * (especially, if only one of the methods shall be overridden).
 *
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public abstract class PaintAdapter implements PaintListener
{
	@Override
	public void postPaint(PaintEvent event) {
		// override to do sth.!
	}

	@Override
	public void prePaint(PaintEvent event) {
		// override to do sth.!
	}
}
