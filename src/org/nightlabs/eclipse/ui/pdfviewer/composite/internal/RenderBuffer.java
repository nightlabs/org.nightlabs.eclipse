package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.util.Conversion;

import com.sun.pdfview.PDFPage;


public class RenderBuffer {

	private PdfViewerComposite pdfViewerComposite;
	private PdfDocument pdfDocument;
	private BufferedImage bufferedImageMain = null; 
	private BufferedImage bufferedImageSub = null;
	private Rectangle2D bufferedImageMainBounds;
	private Rectangle2D bufferedImageSubBounds;
	private int bufferWidth;
	private int bufferHeight;
	private int round;
	private double zoomFactor;
	
	
	public RenderBuffer(PdfViewerComposite pdfViewerComposite, PdfDocument pdfDocument) {
		this.pdfViewerComposite = pdfViewerComposite;
		this.pdfDocument = pdfDocument;
		this.bufferWidth = Conversion.convert(pdfDocument.getDocumentBounds().x) * 2;
		this.bufferHeight = Toolkit.getDefaultToolkit().getScreenSize().height * 2;
		this.zoomFactor = 1;
	}

	/**
	 * Creates a new main and a new sub buffer when opening a PDF file.
	 * In the case the document was already opened a new sub buffer is created if necessary
	 *
	 * @param completeRecreation true if both the main buffer and the sub buffer have to be re-created
	 */
	public void createOrSetBufferDimensions(boolean completeRecreation) {
		createOrSetBufferDimensions(0, 0, completeRecreation);
	}
		
	/**
	 * Creates main and sub buffer in the case two new buffers are needed.
	 *
	 * @param posX the x-coordinate of the upper left corner of the buffers that have to be created.
	 * @param posY the y-coordinate of the upper left corner of the main buffer that has to be created (the y-coordinate of the sub buffer depends on this value)
	 * @param completeRecreation true if both the main buffer and the sub buffer have to be (re-)created, otherwise false
	 */
	public synchronized void createOrSetBufferDimensions(int posX, int posY, boolean completeRecreation) {

		GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		
		if (bufferedImageMainBounds != null && bufferedImageMain != null && bufferedImageSubBounds != null && bufferedImageSub != null) {
			
			if (completeRecreation == false ) {  

				Logger.getRootLogger().info("re-creating only the sub buffer");
				
				bufferedImageMain = graphicsConfiguration.createCompatibleImage(bufferWidth, bufferHeight);	// perhaps to omit					
//				bufferedImageMain = bufferedImageSub.getSubimage(0, 0, bufferWidth, bufferHeight);	// to much unsteadiness in case B (see below)
				bufferedImageMain = bufferedImageSub;
//				bufferedImageMainBounds = new java.awt.Rectangle(Conversion.convert(bufferedImageSubBounds.getX()), Conversion.convert(bufferedImageSubBounds.getY()), bufferWidth, bufferHeight);
				bufferedImageMainBounds.setRect(bufferedImageSubBounds);
				
//				printToImageFile(bufferedImageMain, "bufferedImageMain" + round);				
				
				if (Conversion.convert(bufferedImageMainBounds.getY())  + (3 * bufferHeight) / 4 <= pdfDocument.getDocumentBounds().y) {
					bufferedImageSub = graphicsConfiguration.createCompatibleImage(bufferWidth, bufferHeight);
					bufferedImageSubBounds = new java.awt.Rectangle(posX, Conversion.convert(bufferedImageMainBounds.getY())  + (3 * bufferHeight) / 4, bufferWidth, bufferHeight);
					Job job = new Job("rendering buffer") {
						protected IStatus run (IProgressMonitor monitor) {
							bufferedImageSub = renderBufferedImage(bufferedImageSub, bufferedImageSubBounds);
//							printToImageFile(bufferedImageSub, "bufferedImageSub" + round);
							return org.eclipse.core.runtime.Status.OK_STATUS;
						}
					};
					job.setPriority(Job.SHORT);
					job.schedule();
				}
				else
					// we do not need a second buffer in this case
				
				round++;
			}
			
			else {	

				Logger.getRootLogger().info("re-creating both buffers");
				
				bufferedImageMain = graphicsConfiguration.createCompatibleImage(bufferWidth, bufferHeight);	// perhaps to omit
				bufferedImageMainBounds = new java.awt.Rectangle(posX, posY, bufferWidth, bufferHeight);
//				bufferedImageMainBounds.setRect(posX, posY, bufferWidth, bufferHeight);
				bufferedImageMain = renderBufferedImage(bufferedImageMain, bufferedImageMainBounds);
				
//				printToImageFile(bufferedImageMain, "bufferedImageMain" + round);
				
				if (posY + (3 * bufferHeight) / 4 <= pdfDocument.getDocumentBounds().y) {
					bufferedImageSub = graphicsConfiguration.createCompatibleImage(bufferWidth, bufferHeight);	 // perhaps to omit
					bufferedImageSubBounds = new java.awt.Rectangle(posX, posY + (3 * bufferHeight) / 4, bufferWidth, bufferHeight);
	//				bufferedImageSubBounds.setRect(posX, posY + (3 * bufferHeight) / 4, bufferWidth, bufferHeight);
					Job job = new Job("rendering buffer") {
						protected IStatus run (IProgressMonitor monitor) {
							bufferedImageSub = renderBufferedImage(bufferedImageSub, bufferedImageSubBounds);
//							printToImageFile(bufferedImageSub, "bufferedImageSub" + round);
							return org.eclipse.core.runtime.Status.OK_STATUS;
						}
					};
					job.setPriority(Job.SHORT);
					job.schedule();							
				}
				else 
					// we do not need a second buffer in this case
				
				round++;
			}
			

		}		
		else {
			
			Logger.getRootLogger().info("creating both buffers when opening the document");
			
			bufferedImageMain = graphicsConfiguration.createCompatibleImage(bufferWidth, bufferHeight);
			bufferedImageMainBounds = new java.awt.Rectangle(posX, 0, bufferWidth, bufferHeight);    // first buffer begins at position (0,0) of the document 
			setBufferedImageMainBounds(bufferedImageMainBounds);
			Logger.getRootLogger().info("rendering main buffer");
			bufferedImageMain = renderBufferedImage(bufferedImageMain, bufferedImageMainBounds);
			
//			printToImageFile(bufferedImageMain, "bufferedImageMain" + round);
						
			// do not put the following three lines into the job as it leads to a null pointer exception
			// TODO do creation of the sub buffer inside the job
			bufferedImageSub = graphicsConfiguration.createCompatibleImage(bufferWidth, bufferHeight);
			bufferedImageSubBounds = new java.awt.Rectangle(posX, 0 + (3 * bufferHeight) / 4 , bufferWidth, bufferHeight);
			setBufferedImageSubBounds(bufferedImageSubBounds);
			if (0 + (3 * bufferHeight) / 4 <= pdfDocument.getDocumentBounds().y) {
				Job job = new Job("rendering buffer") {
					protected IStatus run (IProgressMonitor monitor) {
						Logger.getRootLogger().info("rendering sub buffer beginning at point " + ((3 * bufferHeight) / 4));
						bufferedImageSub = renderBufferedImage(bufferedImageSub, bufferedImageSubBounds);
//						printToImageFile(bufferedImageSub, "bufferedImageSub" + round);
						return org.eclipse.core.runtime.Status.OK_STATUS;
					}
				};
				job.setPriority(Job.SHORT);
				job.schedule();

			}
			else
				// we do not need a second buffer at all
			
			round++;
		}
		
	}	

	/**
	 * Renders a buffered image by searching those PDF pages of the PDF document that are lying inside the 
	 * buffer bounds of this buffered image. 
	 *
	 * @param bufferedImage the buffered image that has to be rendered
	 * @param bufferedImageBufferBounds the dimensions of the buffered image that has to be rendered
	 * @return the rendered image
	 */
	protected synchronized BufferedImage renderBufferedImage(BufferedImage bufferedImage, Rectangle2D bufferedImageBufferBounds) {
		
		// clear buffer => fill grey
		Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
		graphics.setBackground(pdfViewerComposite.getViewPanel().getBackground());
		graphics.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		boolean endOfPageIsInBuffer;
		boolean beginningOfPageIsInBuffer;
		int bufferCoordinateY = 0;
//		affineTransform = new AffineTransform();
//		affineTransform.setToScale(zoomFactor, zoomFactor);
//		Logger.getRootLogger().info("zoom factor in renderBufferedImage: " + zoomFactor);
		
		List<Integer> bufferedImagePageNumbers = pdfDocument.getVisiblePages(bufferedImageBufferBounds);
		Collections.sort(bufferedImagePageNumbers);
		
		for (Integer pageNumber : bufferedImagePageNumbers) {
//			Logger.getRootLogger().info("get page " + pageNumber);
			beginningOfPageIsInBuffer = false;
			endOfPageIsInBuffer = false;

			Rectangle2D pageBounds = pdfDocument.getPageBounds(pageNumber);
			PDFPage pdfPage = pdfDocument.getPdfFile().getPage(pageNumber);		
//			pdfPage.getBBox().setRect(0, 0, pdfPage.getBBox().getWidth() * zoomFactor, pdfPage.getBBox().getHeight() * zoomFactor);
//			pdfPage.addXform(affineTransform);	
//			Logger.getRootLogger().info("PDF Page height in renderBufferedImage: " + pdfPage.getHeight());

			if (bufferedImageBufferBounds.getY() <= pageBounds.getY() && pageBounds.getY() < bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight())
				beginningOfPageIsInBuffer = true;
			if (bufferedImageBufferBounds.getY() < pageBounds.getY() + pageBounds.getHeight() && pageBounds.getY() + pageBounds.getHeight() <= bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight())
				endOfPageIsInBuffer = true;		
			
			// (A) get remaining part of this image as only its remaining part is lying in buffer 
			if (beginningOfPageIsInBuffer == false && endOfPageIsInBuffer == true) {
//				Logger.getRootLogger().info("get remaining part of this image");
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;
				Image pdfImage = pdfPage.getImage(
						Conversion.convert(pageBounds.getWidth() * zoomFactor), 
						Conversion.convert((pageBounds.getHeight() - (bufferedImageBufferBounds.getY() - pageBounds.getY())) * zoomFactor), 
						new Rectangle2D.Double(	0, 													
												0,	// consideration of PDF images begins from bottom left point upwards, not from top left point downwards (old y-coordinate of this image: bufferedImageMainBounds.getY() - pageBounds.getY())
												pageBounds.getWidth(), 
												pageBounds.getHeight() - (bufferedImageBufferBounds.getY() - pageBounds.getY())
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {								
								if (infoflags == 32) 
									bufferFinished[0] = true;
								return true;
							}					
				});
				
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					} 
					catch (InterruptedException e) {
					}					
				}
				
//				printToImageFile(pdfImage, "imageRemainingPart");
								
				// and draw it into the buffer
				graphics.drawImage(	pdfImage, 
									bufferedImage.getWidth() / 2 - Conversion.convert(zoomFactor * pageBounds.getWidth() / 2), 
									0,	// bufferCoordinateY is zero
									null
									);    
				bufferCoordinateY += pdfImage.getHeight(null);
				
				
				
			}
			// (B) get whole part of this image as it is completely lying in buffer 
			if (beginningOfPageIsInBuffer == true && endOfPageIsInBuffer == true) {
//				Logger.getRootLogger().info("get whole part of this image");
//				Logger.getRootLogger().info("page bounds width: " + pageBounds.getWidth() + "; page bounds height: " + pageBounds.getHeight());
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;				
				Image pdfImage = pdfPage.getImage(
						Conversion.convert(pageBounds.getWidth() * zoomFactor), 
						Conversion.convert(pageBounds.getHeight() * zoomFactor),
						new Rectangle2D.Double(	0,
												0,
												pageBounds.getWidth(),
												pageBounds.getHeight()
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) { 								
								if (infoflags == 32) 
									bufferFinished[0] = true;
								return true;
							}					
				});
				
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					}
					catch (InterruptedException e) {
					}					
				}	
				
				printToImageFile(pdfImage, "imageWholePart");
				
				// and draw it into the buffer
				bufferCoordinateY += 20;
//				Logger.getRootLogger().info("Drawing to " + (bufferedImageMain.getWidth() / 2 - Conversion.convert(pageBounds.getWidth()) / 2) + ", " + bufferCoordinateY);
				graphics.drawImage(	pdfImage, 
									bufferedImage.getWidth() / 2 - Conversion.convert(zoomFactor * pageBounds.getWidth() / 2), 
									bufferCoordinateY, 
									null
									);
				bufferCoordinateY += pdfImage.getHeight(null);
				
				
				
			}
			// (C) get upper part of this image as only its upper part is lying in buffer 
			if (beginningOfPageIsInBuffer == true && endOfPageIsInBuffer == false) {
//				Logger.getRootLogger().info("get upper part of this image");
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;					
				Image pdfImage = pdfPage.getImage(
						Conversion.convert(pageBounds.getWidth() * zoomFactor), 
						Conversion.convert((bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight() - pageBounds.getY()) * zoomFactor),
						new Rectangle2D.Double( 0,		
												// consideration of PDF images begins from bottom left point upwards, not from top left point downwards (old value: 0)
												pageBounds.getHeight() - (bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight() - pageBounds.getY()),	     
												pageBounds.getWidth(),
												bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight() - pageBounds.getY()
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
								if (infoflags == 32) 
									bufferFinished[0] = true;
								return true;
							}					
				});
				
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					} 
					catch (InterruptedException e) {
					}					
				}			
				
//				printToImageFile(pdfImage, "imageUpperPart");
				
				// and draw it into the buffer
				bufferCoordinateY += 20;
				graphics.drawImage(	pdfImage, 
									bufferedImage.getWidth() / 2 - Conversion.convert(zoomFactor * pageBounds.getWidth() / 2),  
									bufferCoordinateY, 
									null
									);				
			}
			
			
			
			// (D) get part of this image as beginning AND end of it are not lying in buffer
			if (beginningOfPageIsInBuffer == false && endOfPageIsInBuffer == false) {
//				Logger.getRootLogger().info("get part of this image");
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;	
				Image pdfImage = pdfPage.getImage(
						Conversion.convert(pageBounds.getWidth() * zoomFactor), 
						Conversion.convert(bufferedImageBufferBounds.getHeight() * zoomFactor),
						new Rectangle2D.Double(	0,
												bufferedImageBufferBounds.getY() - pageBounds.getY(),
												pageBounds.getWidth(), 
												bufferedImageBufferBounds.getHeight()
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
								if (infoflags == 32)
									bufferFinished[0] = true;
								return true;
							}					
				});
				
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					} 
					catch (InterruptedException e) {
					}					
				}	
				
//				printToImageFile(pdfImage, "imagePart");
				
				// and draw it into the buffer
				graphics.drawImage(	pdfImage, 
									bufferedImage.getWidth() / 2 - Conversion.convert(zoomFactor * pageBounds.getWidth() / 2),  
									0, // bufferCoordinateY is zero
									null
									);    
			}			
		}
		printToImageFile(bufferedImageMain, "buffer" + round);
//		round++;
		return bufferedImage;
	}
	


	/**
	 * Draws a certain part (the region of interest) of the main buffer onto the screen.  
	 *
	 * @param graphics2D the graphics context of the panel 
	 * @param region the region of interest of the document that shall be drawn onto the screen
	 */	
	public synchronized void drawRegion(Graphics2D graphics2D, Rectangle2D region) {
		
		boolean createTwoNewBuffers = false;
		boolean createOneNewBufferDownwards = false;
		boolean createOneNewBufferUpwards = false;		
		
		while ((!bufferedImageMainIsRendered()) && (!bufferedImageSubIsRendered())) {
			Logger.getRootLogger().info("waiting for buffer to be rendered...");
		}
		
		getBufferedImageMain().flush();
		getBufferedImageSub().flush();

		// TODO buffer width administration
		if (region.getY() + region.getHeight() > bufferedImageSubBounds.getY() + bufferedImageSubBounds.getHeight())
			createTwoNewBuffers = true;
		if (region.getY() + region.getHeight() > bufferedImageMainBounds.getY() + bufferedImageMainBounds.getHeight() && 
				region.getY() + region.getHeight() <= bufferedImageSubBounds.getY() + bufferedImageSubBounds.getHeight())
			createOneNewBufferDownwards = true;
		if (region.getY() < bufferedImageMainBounds.getY())
			createOneNewBufferUpwards = true;		
		
		if (createTwoNewBuffers == true) {	
			// (A) current view (region) does not fit in both buffers anymore => create two new buffers
			Logger.getRootLogger().info("(A) current view (region) does not fit in both buffers anymore => create two new buffers");
			int posX = 0;
			int posY = Conversion.convert(Math.max(0, region.getY() - region.getHeight() / 2));  // y-position of new main buffer
			createOrSetBufferDimensions(posX, posY, true);				
		}
		if (createOneNewBufferDownwards == true) {
			// (B) current view (region) does not fit in main buffer anymore => use sub buffer as main buffer instead and create new sub buffer below the old sub buffer
			Logger.getRootLogger().info("(B) current view (region) does not fit in main buffer anymore => use sub buffer as main buffer instead and create new sub buffer below the old sub buffer");
			createOrSetBufferDimensions(false);	
		}
		if (createOneNewBufferUpwards == true) {
			// (C) current view (region) does not fit in main buffer anymore => create two new buffers
			Logger.getRootLogger().info("(C) current view (region) does not fit in main buffer anymore => create two new buffers");
			int posX = 0;
			int posY = Conversion.convert(Math.max(0, region.getY() - (bufferedImageMainBounds.getHeight() - 3/2 * region.getHeight())));  // y-position of new buffer
			createOrSetBufferDimensions(posX, posY, true);	
		}		
		if (createTwoNewBuffers == false && createOneNewBufferDownwards == false && createOneNewBufferUpwards == false) {
			// (D) current view (region) does still fit in current buffer => nothing to do but drawing
			Logger.getRootLogger().info("distance to end of main buffer: " + ((bufferedImageMainBounds.getY() + bufferedImageMainBounds.getHeight()) - (region.getY() + region.getHeight())));
			Logger.getRootLogger().info("distance to beginning of main buffer: " + (region.getY() - bufferedImageMainBounds.getY()));					
		}		
		
		// draw current view (region) of buffer onto the screen
		
		// works, but draws images to the left side of the screen
/*		graphics2D.drawImage(getBufferedImage(), 
				// destination rectangle (screen) given as two points d1 and d2
				0, 0, 
				bufferWidth, Conversion.convert(region.getHeight()),
				// source (certain rectangle in the current buffer) given as two points s1 and s2
				Conversion.convert(region.getX() - bufferedImageMainBounds.getX()), Conversion.convert(region.getY() - bufferedImageMainBounds.getY()),		
				Conversion.convert(region.getX() - bufferedImageMainBounds.getX() + bufferWidth), Conversion.convert(region.getY() - bufferedImageMainBounds.getY() + region.getHeight()),
				null
				);*/
		
		graphics2D.drawImage(getBufferedImageMain(), 
				// destination rectangle (screen) given as TWO POINTS d1 and d2 (not width and height)
				Math.max(0, Conversion.convert(region.getWidth() / 2 - bufferWidth / 2)),  0, 
				Conversion.convert(Math.max(0 + bufferWidth, region.getWidth() / 2 - bufferWidth / 2 + bufferWidth)), Conversion.convert(region.getHeight()),
				// source (certain rectangle in the current buffer) given as TWO POINTS s1 and s2 (not width and height)
				Conversion.convert(region.getX() - bufferedImageMainBounds.getX()), Conversion.convert(region.getY() - bufferedImageMainBounds.getY()),		
				Conversion.convert(region.getX() - bufferedImageMainBounds.getX() + bufferWidth), Conversion.convert(region.getY() - bufferedImageMainBounds.getY() + region.getHeight()),
				null
				);		
		
	}
	
	/**
	 * Sets the background of a given buffered image to a certain color by setting every pixel separately (expensive). 
	 *
	 */
	public void bufferedImageDrawBackground () {
		for (int j = 0; j < Conversion.convert(pdfDocument.getDocumentBounds().x); j++) {
			for (int i = 0; i < bufferHeight; i++)
				bufferedImageMain.setRGB(j, i, - 5000000);
		}
	}
	
	/**
	 * Writes a given image to a file. 
	 *
	 * @param pdfImage the image that shall be written to a file
	 * @param filenamePrefix the prefix of the filename of the file the image is written to dependent on part and round 
	 */
	public void printToImageFile (Image pdfImage, String filenamePrefix) {
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
		try {
			writer.setOutput(new FileImageOutputStream(new File("/home/frederik/temp/" + filenamePrefix + ".jpg")));
			writer.write((RenderedImage) pdfImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	public boolean bufferedImageMainIsRendered() {
		return bufferedImageMainBounds != null;	
	}
	public boolean bufferedImageSubIsRendered() {
		return bufferedImageSubBounds != null;	
	}
	
	public synchronized BufferedImage getBufferedImageMain() {
		if (!bufferedImageMainIsRendered())
			renderBufferedImage(bufferedImageMain, bufferedImageMainBounds);
		return bufferedImageMain;
	}	
	
	public synchronized BufferedImage getBufferedImageSub() {
		if (!bufferedImageSubIsRendered())
			renderBufferedImage(bufferedImageSub, bufferedImageSubBounds);
		return bufferedImageSub;
	}

	public void setBufferedImageMain(BufferedImage bufferedImageMain) {
		this.bufferedImageMain = bufferedImageMain;
	}

	public PdfDocument getPdfDocument() {
		return pdfDocument;
	}

	public void setPdfDocument(PdfDocument pdfDocument) {
		this.pdfDocument = pdfDocument;
	}	
	
	public void setBufferedImageSub(BufferedImage bufferedImageSub) {
		this.bufferedImageSub = bufferedImageSub;
	}

	public Rectangle2D getBufferedImageMainBounds() {
		return bufferedImageMainBounds;
	}

	public void setBufferedImageMainBounds(Rectangle2D bufferedImageMainBounds) {
		this.bufferedImageMainBounds = bufferedImageMainBounds;
	}

	public Rectangle2D getBufferedImageSubBounds() {
		return bufferedImageSubBounds;
	}

	public void setBufferedImageSubBounds(Rectangle2D bufferedImageSubBounds) {
		this.bufferedImageSubBounds = bufferedImageSubBounds;
	}

	public int getBufferWidth() {
		return bufferWidth;
	}

	public void setBufferWidth(int bufferWidth) {
		this.bufferWidth = bufferWidth;
	}

	public int getBufferHeight() {
		return bufferHeight;
	}

	public void setBufferHeight(int bufferHeight) {
		this.bufferHeight = bufferHeight;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}
	
	
}
