package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;


public class RenderThread extends Thread {
	
	/**
	 * The <b>real</b> coordinates of the view area - i.e. the part that is visible within the composite.
	 */
	private Rectangle2D rectangleView;
	private PdfViewerComposite pdfViewerComposite;
	private RenderBuffer renderBuffer;
	private PdfDocument pdfDocument;
	private static long threadId = 0;
	private static synchronized long nextThreadId() {
		return threadId++;
	}

	public RenderThread(PdfViewerComposite pdfViewerComposite, PdfDocument pdfDocument, RenderBuffer renderBuffer) {
		super("render-" + Long.toHexString(nextThreadId()));
		this.renderBuffer = renderBuffer;
		this.pdfViewerComposite = pdfViewerComposite;
		this.pdfDocument = pdfDocument;
		start();
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			if (pdfViewerComposite.isDisposed())
				return;

			// (A) no buffers exist (this is the case the PDF document is loaded at the beginning)
			if (renderBuffer.getBufferedImageMainBounds() == null) {				
				renderBuffer.createOrSetBufferDimensions(true); 
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						pdfViewerComposite.getViewPanel().repaint();
					}
				});					
			}			
			
			synchronized (this) {
				try {
					this.wait(); // TODO use timeout!
				} catch (InterruptedException e) {
					// ignore
				}
			}
			
			// TODO find out whether we really have to do something by getting the desired coordinates and 
			// comparing them to the coordinates of the buffer	
			
			// TODO find out whether the newly created imageData is still up-to-date (we're asynchronous here 
			// and thus, the situation might have already changed)
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					pdfViewerComposite.getViewPanel().repaint();
				}
			});
			
			
			
			
			


			
		
			
		}
	}

	public synchronized void needRendering(Rectangle2D rectangleView) {
		this.rectangleView = rectangleView;
		this.notifyAll();
	}

	
}




