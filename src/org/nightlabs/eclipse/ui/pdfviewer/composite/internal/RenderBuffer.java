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

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.util.Conversion;

import com.sun.pdfview.PDFPage;


public class RenderBuffer {

//	private static final int BUFFER_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
//	private static final int BUFFER_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height * 3;	
	
	private PdfDocument pdfDocument;
	private PdfViewerComposite pdfViewerComposite;
	private ImageData imageData1, imageData2;
	private BufferedImage bufferedImageAWTNew1, bufferedImageAWTNew2;
	private BufferedImage bufferedImage = null; 
	private Rectangle rectangleBufferedImage;
	private ImageObserver imageObserver;
	private Rectangle2D bufferBounds;
	private int bufferWidth;
	
	
	public RenderBuffer(PdfViewerComposite pdfViewerComposite, PdfDocument pdfDocument) {
		this.pdfViewerComposite = pdfViewerComposite;
		this.pdfDocument = pdfDocument;
		bufferWidth = Conversion.convert(pdfDocument.getDocumentBounds().x);
	}

	/**
	 * Set the size of the buffer in pixels.
	 *
	 * @param width the width.
	 * @param height the height.
	 */
	public synchronized void createOrSetBufferDimensions(int posX, int posY)
	{
		final int BUFFER_WIDTH = Conversion.convert(pdfDocument.getDocumentBounds().x);
		final int BUFFER_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height * 3;
		GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		
		if (bufferBounds != null && bufferedImage != null) {
			// re-calculate bufferBounds
			System.out.println("recalculate the bufferBounds");
			bufferedImage = graphicsConfiguration.createCompatibleImage(BUFFER_WIDTH, BUFFER_HEIGHT);
//			bufferedImageDrawBackground();
//			bufferedImage.createGraphics().setColor(Color.RED);
//			bufferedImage.createGraphics().fillRect(0, 0, BUFFER_WIDTH, BUFFER_HEIGHT);
//			bufferBounds.setRect(posX, posY, BUFFER_WIDTH, BUFFER_HEIGHT);
			bufferBounds = new java.awt.Rectangle(posX, posY, BUFFER_WIDTH, BUFFER_HEIGHT);
			setBufferBounds(bufferBounds);
		}
		
		else {
			// calculate bufferBounds the first time
			bufferedImage = graphicsConfiguration.createCompatibleImage(BUFFER_WIDTH, BUFFER_HEIGHT);
//			bufferedImageDrawBackground();
//			System.out.println("pixel color: " + bufferedImage.getRGB(0, 0));
//			bufferedImage.createGraphics().setColor(Color.RED);
//			bufferedImage.createGraphics().fillRect(0, 0, BUFFER_WIDTH, BUFFER_HEIGHT);
			bufferBounds = new java.awt.Rectangle(0, 0, BUFFER_WIDTH, BUFFER_HEIGHT);    // first buffer begins at position (0,0) of the document 
			setBufferBounds(bufferBounds);
		}
		
	}

	public synchronized void setBufferBounds(Rectangle2D bufferBounds)
	{
		this.bufferBounds = bufferBounds;
		render();
	}

	protected void render()
	{
		// clear buffer => fill grey
		boolean endOfPageIsInBuffer;
		boolean beginningOfPageIsInBuffer;
		int bufferCoordinateY = 0;
		List<Integer> pageNumbers = pdfDocument.getVisiblePages(bufferBounds);
		Collections.sort(pageNumbers);
//		List<Integer> pageNumbers = new ArrayList<Integer>();
//		pageNumbers.add(1);
		
		for (Integer pageNumber : pageNumbers) {
			System.out.println("get page " + pageNumber);
			beginningOfPageIsInBuffer = false;
			endOfPageIsInBuffer = false;
			// get page bounds 
			Rectangle2D pageBounds = pdfDocument.getPageBounds(pageNumber);
//			System.out.println("page " + pageNumber + " pageBounds.x: " + pageBounds.getX());
//			System.out.println("page " + pageNumber + " pageBounds.y: " + pageBounds.getY());
//			System.out.println("page " + pageNumber + " pageBounds.height: " + pageBounds.getHeight());
//			System.out.println("page " + pageNumber + " pageBounds.width: " + pageBounds.getWidth());
			// get PDF page
			PDFPage pdfPage = pdfDocument.getPdfFile().getPage(pageNumber);			

			if (bufferBounds.getY() <= pageBounds.getY() && pageBounds.getY() < bufferBounds.getY() + bufferBounds.getHeight())
				beginningOfPageIsInBuffer = true;
			if (bufferBounds.getY() < pageBounds.getY() + pageBounds.getHeight() && pageBounds.getY() + pageBounds.getHeight() <= bufferBounds.getY() + bufferBounds.getHeight())
				endOfPageIsInBuffer = true;		
			
			// (A) get remaining part of this image as only its remaining part is lying in buffer 
			if (beginningOfPageIsInBuffer == false && endOfPageIsInBuffer == true) {
				System.out.println("get remaining part of this image");
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;
				Image pdfImage = pdfPage.getImage(Conversion.convert(pageBounds.getWidth()), Conversion.convert(pageBounds.getHeight() - (bufferBounds.getY() - pageBounds.getY())), 
						new Rectangle2D.Double(	
												0, 
												/* is this a bug? Consideration of the image begins from bottom left point upwards, not from top left point downwards */
												/* old y-coordinate of this image: bufferBounds.getY() - pageBounds.getY() */ 
												0,
												pageBounds.getWidth(), 
												pageBounds.getHeight() - (bufferBounds.getY() - pageBounds.getY())
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img,
									int infoflags, int x, int y, int width,
									int height) {
//								System.out.println("Image observer image update: " + x + ", " + y +", " + width + ", " + height);
//								System.out.println("infoflags: " + infoflags);
//								System.out.println(Thread.currentThread());
								
								if (infoflags == 32) {
									bufferFinished[0] = true;
								}
								return true;
							}
					
				});
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}					
				}
								
				// and draw it into the buffer
				bufferedImage.getGraphics().drawImage(pdfImage, bufferedImage.getWidth() / 2 - Conversion.convert(pageBounds.getWidth()) / 2, 0, null);    // bufferCoordinateY is zero
				bufferCoordinateY += pdfImage.getHeight(null);
				
				
				
			}
			// (B) get whole part of this image as it is completely lying in buffer 
			if (beginningOfPageIsInBuffer == true && endOfPageIsInBuffer == true) {
				System.out.println("get whole part of this image");
//				System.out.println(Thread.currentThread());
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;				
				Image pdfImage = pdfPage.getImage(Conversion.convert(pageBounds.getWidth()), Conversion.convert(pageBounds.getHeight()),
						new Rectangle2D.Double(
												0,
												0,
												pageBounds.getWidth(),
												pageBounds.getHeight()
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img,
									int infoflags, int x, int y, int width,
									int height) {
//								System.out.println("Image observer image update: " + x + ", " + y +", " + width + ", " + height);
//								System.out.println("infoflags: " + infoflags);
//								System.out.println(Thread.currentThread());
								
								if (infoflags == 32) {
									bufferFinished[0] = true;
								}
								return true;
							}
					
				});
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}					
				}
				
		/*		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
				try {
					writer.setOutput(new FileImageOutputStream(new File("/tmp/test-ab.jpg")));
					writer.write((RenderedImage) pdfImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				// and draw it into the buffer
				bufferCoordinateY += 20;
//				System.out.println("Drawing to " + (bufferedImage.getWidth() / 2 - Conversion.convert(pageBounds.getWidth()) / 2) + ", " + bufferCoordinateY);
				bufferedImage.getGraphics().drawImage(pdfImage, bufferedImage.getWidth() / 2 - Conversion.convert(pageBounds.getWidth()) / 2, bufferCoordinateY, null);
				bufferCoordinateY += pdfImage.getHeight(null);
				
			}
			// (C) get upper part of this image as only its upper part is lying in buffer 
			if (beginningOfPageIsInBuffer == true && endOfPageIsInBuffer == false) {
				System.out.println("get upper part of this image");
				System.out.println("bufferBounds.getY() + bufferBounds.getHeight() - pageBounds.getY(): " + (bufferBounds.getY() + bufferBounds.getHeight() - pageBounds.getY()));
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;	
				
				Image pdfImage = pdfPage.getImage(Conversion.convert(pageBounds.getWidth()), Conversion.convert(bufferBounds.getY() + bufferBounds.getHeight() - pageBounds.getY()),
						new Rectangle2D.Double(     
												0,
												/* is this a bug? Consideration of the image begins from bottom left point upwards, not from top left point downwards */
												/* old y-coordinate of this image: 0 */
												pageBounds.getHeight() - (bufferBounds.getY() + bufferBounds.getHeight() - pageBounds.getY()),      
												pageBounds.getWidth(),
												bufferBounds.getY() + bufferBounds.getHeight() - pageBounds.getY()
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img,
									int infoflags, int x, int y, int width,
									int height) {
//								System.out.println("Image observer image update: " + x + ", " + y +", " + width + ", " + height);
//								System.out.println("infoflags: " + infoflags);
//								System.out.println(Thread.currentThread());
								
								if (infoflags == 32) {
									bufferFinished[0] = true;
								}
								return true;
							}
					
				});
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}					
				}
/*				ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
				try {
					writer.setOutput(new FileImageOutputStream(new File("/home/frederik/temp/upper_part.jpg")));
					writer.write((RenderedImage) pdfImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				// and draw it into the buffer
				bufferCoordinateY += 20;
				bufferedImage.getGraphics().drawImage(pdfImage, bufferedImage.getWidth() / 2 - Conversion.convert(pageBounds.getWidth()) / 2, bufferCoordinateY, null);				
			}
			
			
			
			// (D) get part of this image as beginning AND end of it are not lying in buffer
			if (beginningOfPageIsInBuffer == false && endOfPageIsInBuffer == false) {
				System.out.println("get part of this image");
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;	
				Image pdfImage = pdfPage.getImage(Conversion.convert(pageBounds.getWidth()), Conversion.convert(bufferBounds.getHeight()),
						new Rectangle2D.Double(
												0,
												bufferBounds.getY() - pageBounds.getY(),
												pageBounds.getWidth(), 
												bufferBounds.getHeight()
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img,
									int infoflags, int x, int y, int width,
									int height) {
//								System.out.println("Image observer image update: " + x + ", " + y +", " + width + ", " + height);
//								System.out.println("infoflags: " + infoflags);
//								System.out.println(Thread.currentThread());
								
								if (infoflags == 32) {
									bufferFinished[0] = true;
								}
								return true;
							}
					
				});
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}					
				}	
				// and draw it into the buffer
				bufferedImage.getGraphics().drawImage(pdfImage, bufferedImage.getWidth() / 2 - Conversion.convert(pageBounds.getWidth()) / 2, 0, null);    // bufferCoordinateY is zero
			}			
		}
//		setBufferedImage(bufferedImage);
	}
	
	public synchronized void drawRegion(Graphics2D graphics2D, Rectangle2D region) {
		// TODO: Convert the absolutely given region to coordinates relative to the buffer origin 
		// This might require redraw, if the region is out of the buffer
		while (!isRendered()) {
			System.out.println("Waiting for buffer");
		}
		getBufferedImage().flush();
//		graphics2D.drawImage(getBufferedImage(), /*Conversion.convert((double)(pdfViewerComposite.getScreenSize().width) / 2 - bufferBounds.getWidth() / 2),*/0, 0, null);
				
		if ( /*(region.getX() + region.getWidth() > bufferBounds.getX() + bufferBounds.getWidth()) || */ (region.getY() + region.getHeight() > bufferBounds.getY() + bufferBounds.getHeight()) ||
				/*(region.getX() < bufferBounds.getX()) || */ (region.getY() < bufferBounds.getY())) {
			// (A) current view (region) doesn't fit in current buffer anymore => a new buffer is needed
			System.out.println("a new buffer must be created");
			int posX = 0, posY = 0;
			if (region.getY() + region.getHeight() > bufferBounds.getY() + bufferBounds.getHeight()) {			
				// model 1: construct new buffer in a way that region lies vertically in the center of this new buffer
//				int posY = Conversion.convert(Math.max(0, region.getY() - (bufferBounds.getHeight() / 2 - region.getHeight() / 2)));  
				// model 2: construct new buffer in a way that region lies nearly at the top of this new buffer
				posY = Conversion.convert(Math.max(0, region.getY() - region.getHeight() / 2));  
			}
			if (region.getY() < bufferBounds.getY()) {
				// model 3: construct new buffer in a way that region lies nearly at the bottom of this new buffer
				posY = Conversion.convert(Math.max(0, region.getY() - (bufferBounds.getHeight() - 3/2 * region.getHeight())));			
			}
			createOrSetBufferDimensions(posX, posY);						
		}
		else {
			// (B) current view (region) does still fit in current buffer
			// nothing to do but drawing (see below)
			System.out.println("distance to end of this buffer: " + ((bufferBounds.getY() + bufferBounds.getHeight()) - (region.getY() + region.getHeight())));
			System.out.println("distance to beginning of this buffer: " + (region.getY() - bufferBounds.getY()));
		}
		
		// draw current view (region) of buffer onto the screen
//		System.out.println("region x: " + region.getX() + "; buffer bounds x: " + bufferBounds.getX());
//		System.out.println("region y: " + region.getY() + "; buffer bounds y: " + bufferBounds.getY());
		
		// works, but draws the images to the left side of the screen
/*		graphics2D.drawImage(getBufferedImage(), 
				// destination rectangle (screen) given as two points d1 and d2
				0, 0, 
				bufferWidth, Conversion.convert(region.getHeight()),
				// source (certain rectangle in the current buffer) given as two points s1 and s2
				Conversion.convert(region.getX() - bufferBounds.getX()), Conversion.convert(region.getY() - bufferBounds.getY()),		
				Conversion.convert(region.getX() - bufferBounds.getX() + bufferWidth), Conversion.convert(region.getY() - bufferBounds.getY() + region.getHeight()),
				null
				);*/
		
		graphics2D.drawImage(getBufferedImage(), 
				// destination rectangle (screen) given as TWO POINTS d1 and d2 (not width and height)
				Math.max(0, Conversion.convert(region.getWidth() / 2 - bufferWidth / 2)),  0, 
				Conversion.convert(Math.max(0 + bufferWidth, region.getWidth() / 2 - bufferWidth / 2 + bufferWidth)), Conversion.convert(region.getHeight()),
				// source (certain rectangle in the current buffer) given as TWO POINTS s1 and s2 (not width and height)
				Conversion.convert(region.getX() - bufferBounds.getX()), Conversion.convert(region.getY() - bufferBounds.getY()),		
				Conversion.convert(region.getX() - bufferBounds.getX() + bufferWidth), Conversion.convert(region.getY() - bufferBounds.getY() + region.getHeight()),
				null
				);
			
		
	}

	public Rectangle2D getBufferBounds() {
		return bufferBounds;
	}

	public boolean isRendered() {
		return bufferBounds != null;
	}
	
	public synchronized BufferedImage getBufferedImage() {
		if (!isRendered())
			render();
		return bufferedImage;
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}

	public PdfDocument getPdfDocument() {
		return pdfDocument;
	}

	public void setPdfDocument(PdfDocument pdfDocument) {
		this.pdfDocument = pdfDocument;
	}
	
	public void bufferedImageDrawBackground () {
		for (int j = 0; j < Conversion.convert(pdfDocument.getDocumentBounds().x); j++) {
			for (int i = 0; i < Toolkit.getDefaultToolkit().getScreenSize().height * 3; i++)
				bufferedImage.setRGB(j, i, - 5000000);
		}
	}
	
	
	
	
	
}
