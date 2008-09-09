package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.util.Utilities;


public class RenderThread extends Thread {
	
	private PdfViewerComposite pdfViewerComposite;
	private RenderBuffer renderBuffer;
	private boolean createTwoNewBuffers = false;
	private boolean createOneNewBufferDownwards = false;
	private boolean createOneNewBufferUpwards = false;		
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

			// (A) no buffers exist (this is the case the PDF document is loaded at the beginning)
			if (renderBuffer.getBufferedImageMainBounds() == null) {	
				renderBuffer.initBuffering();
				createTwoNewBuffers = true;
			}
			else {		
				// TODO buffer width administration
				// find out whether we really have to do something by getting the desired coordinates and 
				// comparing them to the coordinates of the buffer	
				if (pdfViewerComposite.getRectangleViewOrigin().y + pdfViewerComposite.getViewPanel().getHeight() > renderBuffer.getBufferedImageSubBounds().getY() + renderBuffer.getBufferedImageSubBounds().getHeight())
					createTwoNewBuffers = true;
				else {
					if (pdfViewerComposite.getRectangleViewOrigin().y + pdfViewerComposite.getViewPanel().getHeight() + 100 > renderBuffer.getBufferedImageMainBounds().getY() + renderBuffer.getBufferedImageMainBounds().getHeight() && 
							pdfViewerComposite.getRectangleViewOrigin().y + pdfViewerComposite.getViewPanel().getHeight() <= renderBuffer.getBufferedImageSubBounds().getY() + renderBuffer.getBufferedImageSubBounds().getHeight())
						createOneNewBufferDownwards = true;
					else {
						if (pdfViewerComposite.getRectangleViewOrigin().y < renderBuffer.getBufferedImageMainBounds().getY())
							createOneNewBufferUpwards = true;	
					}
				}
			}
			if (createTwoNewBuffers == true) {	
				// (A) current view (region) does not fit in both buffers anymore => create two new buffers
				Logger.getRootLogger().info("(A) current view (region) does not fit in both main and sub buffer anymore => create two new buffers");
				int posX = 0;
				int posY = Utilities.doubleToIntRoundedDown(Math.max(0, pdfViewerComposite.getRectangleViewOrigin().y - pdfViewerComposite.getViewPanel().getHeight() / 2));  // y-position of new main buffer
				renderBuffer.createOrSetBufferDimensions(posX, posY, true);						
			}
			if (createOneNewBufferDownwards == true) {
				// (B) current view (region) does not fit in main buffer anymore => use sub buffer as main buffer instead and create new sub buffer below the old sub buffer
				Logger.getRootLogger().info("(B) current view (region) does not fit in main buffer anymore => use sub buffer as main buffer instead and create new sub buffer below old sub buffer");
				renderBuffer.createOrSetBufferDimensions(false);
			}
			if (createOneNewBufferUpwards == true) {
				// (C) current view (region) does not fit in main buffer anymore => create two new buffers
				Logger.getRootLogger().info("(C) current view (region) does not fit in main buffer anymore => create two new buffers");
				int posX = 0;
				int posY = Utilities.doubleToIntRoundedDown(Math.max(0, pdfViewerComposite.getRectangleViewOrigin().y - (renderBuffer.getBufferedImageMainBounds().getHeight() - 1.5 * pdfViewerComposite.getViewPanel().getHeight())));  // y-position of new main buffer
				renderBuffer.createOrSetBufferDimensions(posX, posY, true);		
			}		
			if (createTwoNewBuffers == true || createOneNewBufferDownwards == true || createOneNewBufferUpwards == true) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						pdfViewerComposite.getViewPanel().repaint();
					}
				});					
				createTwoNewBuffers = false;
				createOneNewBufferDownwards = false;
				createOneNewBufferUpwards = false;	
			}
			else {
				// (D) current view (region) does still fit in current buffer => nothing to do but drawing
			}
			
			synchronized (this) {
				try {
					this.wait(10); // TODO use timeout!
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


	
}




