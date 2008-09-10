package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;


public class RenderThread extends Thread {

	private PdfViewerComposite pdfViewerComposite;
	private RenderBuffer renderBuffer;
//	private boolean createTwoNewBuffers = false;
//	private boolean createOneNewBufferDownwards = false;
//	private boolean createOneNewBufferUpwards = false;
	private static long threadId = 0;
	private static synchronized long nextThreadId() {
		return threadId++;
	}

	public RenderThread(PdfViewerComposite pdfViewerComposite, RenderBuffer renderBuffer) {
		super("render-" + Long.toHexString(nextThreadId()));
		this.renderBuffer = renderBuffer;
		this.pdfViewerComposite = pdfViewerComposite;
		start();
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
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
						(int) (viewRegion.getCenterX() - renderBuffer.getBufferWidth() / 2.0d / zoomFactor),
						(int) (viewRegion.getCenterY() - renderBuffer.getBufferHeight() / 2.0d / zoomFactor),
						zoomFactor
				);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						pdfViewerComposite.getViewPanel().repaint();
					}
				});
			}

//			// (A) no buffers exist (this is the case the PDF document is loaded at the beginning)
//			if (renderBuffer.getBufferedImageMainBounds() == null) {
//				renderBuffer.initBuffering();
//				createTwoNewBuffers = true;
//			}
//			else {
//				if (pdfViewerComposite.getZoomFactorPerMill() != (int) (renderBuffer.getZoomFactor() * 1000)) {
//					createTwoNewBuffers = true;
//				}
//
//				// TODO buffer width administration
//				// find out whether we really have to do something by getting the desired coordinates and
//				// comparing them to the coordinates of the buffer
//				if (pdfViewerComposite.getRectangleViewOrigin().y + pdfViewerComposite.getViewPanel().getHeight() > renderBuffer.getBufferedImageSubBounds().getY() + renderBuffer.getBufferedImageSubBounds().getHeight())
//					createTwoNewBuffers = true;
//				else {
//					if (pdfViewerComposite.getRectangleViewOrigin().y + pdfViewerComposite.getViewPanel().getHeight() + 100 > renderBuffer.getBufferedImageMainBounds().getY() + renderBuffer.getBufferedImageMainBounds().getHeight() &&
//							pdfViewerComposite.getRectangleViewOrigin().y + pdfViewerComposite.getViewPanel().getHeight() <= renderBuffer.getBufferedImageSubBounds().getY() + renderBuffer.getBufferedImageSubBounds().getHeight())
//						createOneNewBufferDownwards = true;
//					else {
//						if (pdfViewerComposite.getRectangleViewOrigin().y < renderBuffer.getBufferedImageMainBounds().getY())
//							createOneNewBufferUpwards = true;
//					}
//				}
//			}
//
//
//			if (createTwoNewBuffers == true) {
//				// (A) current view (region) does not fit in both buffers anymore => create two new buffers
//				Logger.getRootLogger().info("(A) current view (region) does not fit in both main and sub buffer anymore => create two new buffers");
//				int posX = 0;
//				int posY = Utilities.doubleToIntRoundedDown(Math.max(0, pdfViewerComposite.getRectangleViewOrigin().y - pdfViewerComposite.getViewPanel().getHeight() / 2));  // y-position of new main buffer
//				renderBuffer.createOrSetBufferDimensions(posX, posY, (double)pdfViewerComposite.getZoomFactorPerMill() / 1000);
//			}
//
//			if (createOneNewBufferDownwards == true) {
//				// (B) current view (region) does not fit in main buffer anymore => use sub buffer as main buffer instead and create new sub buffer below the old sub buffer
//				Logger.getRootLogger().info("(B) current view (region) does not fit in main buffer anymore => use sub buffer as main buffer instead and create new sub buffer below old sub buffer");
//				renderBuffer.createOrSetBufferDimensions();
//			}
//
//			if (createOneNewBufferUpwards == true) {
//				// (C) current view (region) does not fit in main buffer anymore => create two new buffers
//				Logger.getRootLogger().info("(C) current view (region) does not fit in main buffer anymore => create two new buffers");
//				int posX = 0;
//				int posY = Utilities.doubleToIntRoundedDown(Math.max(0, pdfViewerComposite.getRectangleViewOrigin().y - (renderBuffer.getBufferedImageMainBounds().getHeight() - 1.5 * pdfViewerComposite.getViewPanel().getHeight())));  // y-position of new main buffer
//				renderBuffer.createOrSetBufferDimensions(posX, posY, (double)pdfViewerComposite.getZoomFactorPerMill() / 1000);
//			}
//
//			if (createTwoNewBuffers == true || createOneNewBufferDownwards == true || createOneNewBufferUpwards == true) {
//				createTwoNewBuffers = false;
//				createOneNewBufferDownwards = false;
//				createOneNewBufferUpwards = false;
//				repaint();
//			}
//			else {
//				// (D) current view (region) does still fit in current buffer => nothing to do but drawing
//			}

			synchronized (this) {
				try {
					this.wait(60000);
				} catch (InterruptedException e) {
					// ignore
				}
			}

//			while ((!bufferedImageMainIsRendered()) && (!bufferedImageSubIsRendered())) {
//				Logger.getRootLogger().info("waiting for buffer to be rendered...");
//			}

//			renderBuffer.getBufferedImageMain().flush();
//			renderBuffer.getBufferedImageSub().flush();

			// TODO find out whether the newly created imageData is still up-to-date (we're asynchronous here
			// and thus, the situation might have already changed)

		}
	}

//	private void repaint()
//	{
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				pdfViewerComposite.getViewPanel().repaint();
//			}
//		});
//	}


}




