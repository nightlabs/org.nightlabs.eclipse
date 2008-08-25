package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.SwingUtilities;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerCompositeOld;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.util.Conversion;

import com.sun.pdfview.PDFPage;


public class RenderThread extends Thread {
	
	private static long threadId = 0;
	private static synchronized long nextThreadId() {
		return threadId++;
	}

	private PdfViewerCompositeOld pdfViewerCompositeOld;
	private PdfViewerComposite pdfViewerComposite;
	private RenderBuffer renderBuffer;
	private PdfDocument pdfDocument;
	private List<PDFPage> pdfPages;
	private List<Double> pdfPageHeights;
	private List<Double> pdfPageWidths;
	private double pdfPageWidthMax;
	private List<java.awt.Image> pdfImages;
//	private Point coordinates;
	private int currentPageIndex;
	private int alreadyDrawn;
	private int scrollingStepsDownwards;
	private int scrollingStepsUpwards;
	private ImageData imageData;
	private BufferedImage bufferedImageAWTOld;
	private Point rectangleViewOrigin;
	


	/**
	 * The <b>real</b> coordinates of the view area - i.e. the part that is visible within the composite.
	 */
	private Rectangle2D rectangleView;

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

			// (A) no buffer exists (this is the case the document is loaded the first time)
			if (renderBuffer.getBufferBounds() == null) {
				renderBuffer.setPdfDocument(pdfDocument);
//				renderBuffer.setBufferSize(pdfViewerComposite.getScreenSize().width, pdfViewerComposite.getScreenSize().height * 2);				
				renderBuffer.createOrSetBufferDimensions(0, 0); 
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						pdfViewerComposite.getViewPanel().repaint();
					}
				});					
			}			
			
			Point workingcopy;
			synchronized (this) {
				try {
					this.wait(); // TODO use timeout!
				} catch (InterruptedException e) {
					// ignore
				}
//				workingcopy = coordinates;
			}
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					pdfViewerComposite.getViewPanel().repaint();
				}
			});					
			
			
			

			// TODO find out whether we really have to do sth.
			// get desired coordinates and compare them to the coordinates of the buffer		
			

			
			
			

			// TODO find out whether the newly created imageData is still up-to-date (we're asynchronous here and thus, the situation might have already changed)
//			pdfViewerComposite.setImageData(imageData);

			
		
			
		}
	}

	public synchronized void needRendering(Rectangle2D rectangleView) {
		this.rectangleView = rectangleView;
		this.notifyAll();
	}

	public ImageData getImageData() {
		return imageData;
	}

	public void setImageData(ImageData imageData) {
		this.imageData = imageData;
	}
	
}




