package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;

/**
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class RenderThread extends Thread
{
	private static long threadId = 0;
	private static synchronized long nextThreadId() {
		return threadId++;
	}

	private PdfViewerComposite pdfViewerComposite;
	private RenderBuffer renderBuffer;

	public RenderThread(PdfViewerComposite pdfViewerComposite, RenderBuffer renderBuffer) {
		super("render-" + Long.toHexString(nextThreadId()));
		this.renderBuffer = renderBuffer;
		this.pdfViewerComposite = pdfViewerComposite;
		start();
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				if (pdfViewerComposite.isDisposed())
					return;

				// rendering necessary, if the zoom changed
				boolean doRender = pdfViewerComposite.getZoomFactorPerMill() != (int) (renderBuffer.getZoomFactor() * 1000);

				// the new zoom factor (if unchanged, it's already the same as renderBuffer.getZoomFactor())
				double zoomFactor = doRender ? (double)pdfViewerComposite.getZoomFactorPerMill() / 1000 : renderBuffer.getZoomFactor();

				// viewRegion is the real coordinates of the visible area (i.e. the viewPanel's location and size in the real coordinate system)
				Rectangle2D viewRegion = new Rectangle2D.Double(
						pdfViewerComposite.getRectangleViewOrigin().x,
						pdfViewerComposite.getRectangleViewOrigin().y,
						pdfViewerComposite.getViewPanel().getWidth() / zoomFactor,
						pdfViewerComposite.getViewPanel().getHeight() / zoomFactor
				);

				int bufferWidth = (int) (pdfViewerComposite.getViewPanel().getWidth() * RenderBuffer.BUFFER_WIDTH_FACTOR);
				int bufferHeight = (int) (pdfViewerComposite.getViewPanel().getHeight() * RenderBuffer.BUFFER_HEIGHT_FACTOR);

				if (bufferWidth >= 1 && bufferHeight >= 1) {

					if (!doRender) {
						if (bufferWidth != renderBuffer.getBufferWidth())
							doRender = true;
						else if (bufferHeight != renderBuffer.getBufferHeight())
							doRender = true;
					}

					if (!doRender) {
						// rendering necessary, if the view area (= pdfViewerComposite.viewPanel) is (at least partially) outside of the buffer
						Rectangle2D bufferedImageBounds = renderBuffer.getBufferedImageBounds();
						if (bufferedImageBounds == null)
							doRender = true;
						else {
							if (bufferedImageBounds.getMinX() > viewRegion.getMinX())
								doRender = true;
							else if (bufferedImageBounds.getMinY() > viewRegion.getMinY())
								doRender = true;
							else if (bufferedImageBounds.getMaxX() < viewRegion.getMaxX())
								doRender = true;
							else if (bufferedImageBounds.getMaxY() < viewRegion.getMaxY())
								doRender = true;
						}
					}

					if (doRender) {
						renderBuffer.render(
								bufferWidth,
								bufferHeight,
								(int) (viewRegion.getCenterX() - bufferWidth / 2.0d / zoomFactor),
								(int) (viewRegion.getCenterY() - bufferHeight / 2.0d / zoomFactor),
								zoomFactor
						);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								pdfViewerComposite.getViewPanel().repaint();
							}
						});
					}

				}

				synchronized (this) {
					try {
						this.wait(1000); // Unfortunately, we sometimes don't get notified when zooming, so sleeping only one second to ensure early redraws. Marco.
					} catch (InterruptedException e) {
						// ignore
					}
				}
			} catch (Throwable t) {
				ExceptionHandlerRegistry.asyncHandleException(t);
				try { Thread.sleep(5000); } catch (InterruptedException x) { } // prevent too quick re-spawns, if the error occurs every time
			}
		}
	}

}




