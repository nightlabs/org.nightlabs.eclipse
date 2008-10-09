package org.nightlabs.eclipse.ui.pdfviewer.internal;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

/**
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class RenderThread extends Thread
{
	private static final Logger logger = Logger.getLogger(RenderThread.class);
	private static long threadId = 0;
	private static synchronized long nextThreadId() {
		return threadId++;
	}

	private Display display;
	private PdfViewerComposite pdfViewerComposite;
	private RenderBuffer renderBuffer;

	public RenderThread(PdfViewerComposite pdfViewerComposite, RenderBuffer renderBuffer) {
		super("render-" + Long.toHexString(nextThreadId())); //$NON-NLS-1$
		this.renderBuffer = renderBuffer;
		this.pdfViewerComposite = pdfViewerComposite;
		this.display = pdfViewerComposite.getDisplay();
		start();
	}

	private volatile boolean forceInterrupt = false;

	@Override
	public void interrupt() {
		forceInterrupt = true;
		super.interrupt();
	}

	@Override
	public boolean isInterrupted() {
		return forceInterrupt || super.isInterrupted();
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				if (pdfViewerComposite.isDisposed())
					return;

				// rendering necessary, if zoom has been changed
				boolean doRender = pdfViewerComposite.getZoomFactorPerMill() != (int) (renderBuffer.getZoomFactor() * 1000);

				// the new zoom factor (if unchanged, it's already the same as renderBuffer.getZoomFactor())
				double zoomFactor = doRender ? (double)pdfViewerComposite.getZoomFactorPerMill() / 1000 : renderBuffer.getZoomFactor();

				// viewRegion is the real coordinates of the visible area (i.e. the viewPanel's location and size in the real coordinate system)
				Rectangle2D viewRegion = new Rectangle2D.Double(
						pdfViewerComposite.getViewOrigin().getX(),
						pdfViewerComposite.getViewOrigin().getY(),
						pdfViewerComposite.getViewPanel().getWidth() / (zoomFactor * getZoomScreenResolutionFactorX()),
						pdfViewerComposite.getViewPanel().getHeight() / (zoomFactor * getZoomScreenResolutionFactorY())
				);

//				int bufferWidth = (int) (pdfViewerComposite.getViewPanel().getWidth() * RenderBuffer.BUFFER_WIDTH_FACTOR);
//				int bufferHeight = (int) (pdfViewerComposite.getViewPanel().getHeight() * RenderBuffer.BUFFER_HEIGHT_FACTOR);

				double bufferWidthFactor;
				double bufferHeightFactor;
				Dimension2D documentDimension = pdfViewerComposite.getPdfDocument().getDocumentDimension();
				if (documentDimension.getWidth() > documentDimension.getHeight()) {
					bufferWidthFactor = RenderBuffer.BUFFER_SIZE_FACTOR_LARGE;
					bufferHeightFactor = RenderBuffer.BUFFER_SIZE_FACTOR_SMALL;
				}
				else {
					bufferWidthFactor = RenderBuffer.BUFFER_SIZE_FACTOR_SMALL;
					bufferHeightFactor = RenderBuffer.BUFFER_SIZE_FACTOR_LARGE;
				}
				
				int bufferWidth = (int) (pdfViewerComposite.getViewPanel().getWidth() * bufferWidthFactor);
				int bufferHeight = (int) (pdfViewerComposite.getViewPanel().getHeight() * bufferHeightFactor);
				
				if (bufferWidth >= 1 && bufferHeight >= 1) {

					if (!doRender) {
						// rendering necessary, if buffer width or buffer height has been changed (induced by panel in- or decrease)
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
						renderBuffer.paintToBuffer(
								bufferWidth,
								bufferHeight,
								(int) (viewRegion.getCenterX() - bufferWidth / 2.0d / (zoomFactor * getZoomScreenResolutionFactorX())),
								(int) (viewRegion.getCenterY() - bufferHeight / 2.0d / (zoomFactor * getZoomScreenResolutionFactorY())),
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
			} catch (final Throwable t) {
				logger.error("run: " + t.getClass().getName() + ": " + t.getLocalizedMessage(), t); //$NON-NLS-1$ //$NON-NLS-2$
//				ExceptionHandlerRegistry.asyncHandleException(t);

				// We don't want a dependency on org.nightlabs.base.ui, but want our exception handler to handle it
				// (or to be more precise the exception handler of the current application). Therefore, we throw
				// it on the UI thread, which has (hopefully) the correct exception handler registered.
				display.asyncExec(new Runnable() {
					public void run() {
						throw new RuntimeException(t);
					}
				});

				try { Thread.sleep(5000); } catch (InterruptedException x) { } // prevent too quick re-spawns, if the error occurs every time
			}
		}
	}

	protected double getZoomScreenResolutionFactorX()
	{
		return pdfViewerComposite.getZoomScreenResolutionFactor().getX();
	}

	protected double getZoomScreenResolutionFactorY()
	{
		return pdfViewerComposite.getZoomScreenResolutionFactor().getY();
	}
}
