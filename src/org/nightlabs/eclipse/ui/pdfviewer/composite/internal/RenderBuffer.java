package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.Color;
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
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.log4j.Logger;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;

import com.sun.pdfview.PDFPage;


public class RenderBuffer
{
	private static final Logger logger = Logger.getLogger(RenderBuffer.class);

	private PdfViewerComposite pdfViewerComposite;
	private PdfDocument pdfDocument;

	private Object bufferedImageMutex = new Object();
	// BEGIN protected by bufferedImageMutex
	private BufferedImage bufferedImage = null;
	private Rectangle2D.Double bufferedImageBounds = null;
	private double zoomFactor;
	// END protected by bufferedImageMutex

	private int bufferWidth;
	private int bufferHeight;

	public RenderBuffer(PdfViewerComposite pdfViewerComposite, PdfDocument pdfDocument) {
		this.pdfViewerComposite = pdfViewerComposite;
		this.pdfDocument = pdfDocument;
		this.bufferWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 1.3);
		this.bufferHeight = Toolkit.getDefaultToolkit().getScreenSize().height * 2;
	}

	/**
	 * Renders the buffer's area.
	 *
	 * @param posX the x-coordinate in the real coordinate system of the upper left corner of the buffers that have to be created.
	 * @param posY the y-coordinate in the real coordinate system of the upper left corner of the main buffer that has to be created (the y-coordinate of the sub buffer depends on this value)
	 */
	public void render(int posX, int posY, double zoomFactor)
	{
		GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage bufferedImage = graphicsConfiguration.createCompatibleImage(bufferWidth, bufferHeight);
		Rectangle2D.Double bufferedImageBounds = new Rectangle2D.Double(
				posX,
				posY,
				bufferWidth / zoomFactor,
				bufferHeight / zoomFactor
		);

		// clear buffer => fill grey
		Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
		graphics.setColor(pdfViewerComposite.getViewPanel().getBackground());
		graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

		// get page numbers of those pages that are lying in buffer bounds of currently considered buffer
		List<Integer> bufferedImagePageNumbers = pdfDocument.getVisiblePages(bufferedImageBounds);

		for (Integer pageNumber : bufferedImagePageNumbers) {
			Rectangle2D.Double pageBounds = pdfDocument.getPageBounds(pageNumber);
			PDFPage pdfPage = pdfDocument.getPdfFile().getPage(pageNumber);

			int pdfImageWidth = (int) (pageBounds.getWidth() * zoomFactor);
			int pdfImageHeight = (int) (pageBounds.getHeight() * zoomFactor);

			// PDF coordinate system begins from bottom left point upwards, not from top left point downwards
			Rectangle2D.Double clipLeftBottom = new Rectangle2D.Double(0, 0, 0, 0);
			clipLeftBottom.width = pdfPage.getWidth();
			clipLeftBottom.height = pdfPage.getHeight();

			if (pageBounds.getMinX() < bufferedImageBounds.getMinX()) {
				double d = bufferedImageBounds.getMinX() - pageBounds.getMinX();
				pdfImageWidth -= d * zoomFactor;
				clipLeftBottom.x = d;
				clipLeftBottom.width -= d;
			}

			if (pageBounds.getMinY() < bufferedImageBounds.getMinY()) {
				double d = bufferedImageBounds.getMinY() - pageBounds.getMinY();
				pdfImageHeight -= d * zoomFactor;
				clipLeftBottom.height -= d;
			}

			if (pageBounds.getMaxX() > bufferedImageBounds.getMaxX()) {
				double d = pageBounds.getMaxX() - bufferedImageBounds.getMaxX();
				pdfImageWidth -= d * zoomFactor;
				clipLeftBottom.width -= d;
			}

			if (pageBounds.getMaxY() > bufferedImageBounds.getMaxY()) {
				double d = pageBounds.getMaxY() - bufferedImageBounds.getMaxY();
				pdfImageHeight -= d * zoomFactor;
				clipLeftBottom.height -= d;
				clipLeftBottom.y = d;
			}

			if (pdfImageWidth < 1 || pdfImageHeight < 1) // skip a 0-height/width image
				continue;

			Image pdfImage = getPdfImage(
					pdfPage,
					pdfImageWidth,
					pdfImageHeight,
					clipLeftBottom
			);

//			String renderID = Long.toString(System.currentTimeMillis(), 36);
//			printToImageFile(pdfImage, String.format("pdfImage-%s-%03d", renderID , pageNumber));

			// In contrast to clipLeftBottom the clipAbsoluteLeftTop specifies the left-top-point of the clip relative
			// to the PdfDocument's complete coordinate system.
			Rectangle2D.Double clipAbsoluteLeftTop = new Rectangle2D.Double();
			clipAbsoluteLeftTop.x = pageBounds.getX() + clipLeftBottom.x;
			clipAbsoluteLeftTop.y = pageBounds.getY() + pageBounds.getHeight() - (clipLeftBottom.y + clipLeftBottom.height);
			clipAbsoluteLeftTop.width = clipLeftBottom.width;
			clipAbsoluteLeftTop.height = clipLeftBottom.height;

			drawImage(
					graphics,
					pdfImage,
					(int) ((clipAbsoluteLeftTop.getX() - bufferedImageBounds.getX()) * zoomFactor),
					(int) ((clipAbsoluteLeftTop.getY() - bufferedImageBounds.getY()) * zoomFactor)
			);

			graphics.setColor(Color.BLACK);
			graphics.drawRect(
					(int) ((pageBounds.getX() - bufferedImageBounds.getX()) * zoomFactor),
					(int) ((pageBounds.getY() - bufferedImageBounds.getY()) * zoomFactor),
					(int) (pageBounds.getWidth() * zoomFactor),
					(int) (pageBounds.getHeight() * zoomFactor)
			);
		} // for (Integer pageNumber : bufferedImagePageNumbers) {

//		printToImageFile(bufferedImage, "bufferedImage-" + round);

		synchronized (this.bufferedImageMutex) {
			this.bufferedImage = bufferedImage;
			this.bufferedImageBounds = bufferedImageBounds;
			this.zoomFactor = zoomFactor;
		}
	}

	private static void drawImage(Graphics2D graphics2D, Image image, int x, int y)
	{
		final boolean[] bufferFinished = new boolean[1];
		bufferFinished[0] = graphics2D.drawImage(
				image,
				x,
				y,
				new ImageObserver() {
					@Override
					public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
						if (infoflags == ImageObserver.ALLBITS)
							bufferFinished[0] = true;
						return true;
					}
				}
		);
		while (!bufferFinished[0]) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	private static Image getPdfImage(PDFPage pdfPage, int pdfImageWidth, int pdfImageHeight, Rectangle2D clipLeftBottom)
	{
		final boolean[] bufferFinished = new boolean[1];
		bufferFinished[0] = false;
		Image pdfImage = pdfPage.getImage(
				pdfImageWidth,
				pdfImageHeight,
				clipLeftBottom,
				new ImageObserver() {
					@Override
					public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
						if (infoflags == ImageObserver.ALLBITS)
							bufferFinished[0] = true;
						return true;
					}
				}
		);
		while (!bufferFinished[0]) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		return pdfImage;
	}

	/**
	 * Draws a certain part (the region of interest) of the main buffer onto the screen - or more precisely the viewPanel
	 * (passed as <code>graphics2D</code>).
	 *
	 * @param graphics2D the graphics context of the panel
	 * @param region the region of interest of the document that shall be drawn onto the screen in real coordinates
	 * @return <code>true</code>, if the region could be completely copied from the buffer. <code>false</code>, if the requested region
	 *		exceeds the buffer and therefore could only copy partially or not at all. Empty pages will be drawn instead.
	 */
	public boolean drawRegion(
			Graphics2D graphics2D,
			int graphics2DWidth, int graphics2DHeight,
			double requestedZoomFactor,
			Rectangle2D region
	)
	{
		if (graphics2D == null)
			throw new IllegalArgumentException("graphics2D must not be null!");

		if (region == null)
			throw new IllegalArgumentException("region must not be null!");

		synchronized (bufferedImageMutex) {
			boolean zoomMismatch = (int)(requestedZoomFactor * 1000) != (int) (zoomFactor * 1000);
			boolean bufferSufficient = !zoomMismatch;

			int destinationX1 = 0;
			int destinationY1 = 0;
			int destinationX2 = graphics2DWidth;
			int destinationY2 = graphics2DHeight;

			int sourceX1 = 0;
			int sourceY1 = 0;
			int sourceX2 = 0;
			int sourceY2 = 0;

			if (bufferedImageBounds == null)
				bufferSufficient = false;
			else {
				// destination rectangle (screen coordinates relative to the panel! i.e. usually the complete panel)
				// given as TWO POINTS d1 and d2 (not width and height)
				sourceX1 = (int) ((region.getMinX() - bufferedImageBounds.getMinX()) * zoomFactor);
				sourceY1 = (int) ((region.getMinY() - bufferedImageBounds.getMinY()) * zoomFactor);

				if (bufferedImageBounds.getMinX() > region.getMinX()) {
					sourceX1 = 0;
					destinationX1 = (int) ((bufferedImageBounds.getMinX() - region.getMinX()) * zoomFactor);
					bufferSufficient = false;
				}

				if (bufferedImageBounds.getMinY() > region.getMinY()) {
					sourceY1 = 0;
					destinationY1 = (int) ((bufferedImageBounds.getMinY() - region.getMinY()) * zoomFactor);
					bufferSufficient = false;
				}

				if (bufferedImageBounds.getMaxX() < region.getMaxX()) {
					destinationX2 = graphics2DWidth - (int) ((region.getMaxX() - bufferedImageBounds.getMaxX()) * zoomFactor);
					bufferSufficient = false;
				}

				if (bufferedImageBounds.getMaxY() < region.getMaxY()) {
					destinationY2 = graphics2DWidth - (int) ((region.getMaxY() - bufferedImageBounds.getMaxY()) * zoomFactor);
					bufferSufficient = false;
				}

				sourceX2 = sourceX1 + destinationX2 - destinationX1;
				sourceY2 = sourceY1 + destinationY2 - destinationY1;
			}

			if (!bufferSufficient) {
				// draw background
				graphics2D.setColor(pdfViewerComposite.getViewPanel().getBackground());
				graphics2D.fillRect(0, 0, graphics2DWidth, graphics2DHeight);

				// draw empty pages
				List<Integer> visiblePages = pdfDocument.getVisiblePages(region);
				for (Integer pageNumber : visiblePages) {
					Rectangle2D page = pdfDocument.getPageBounds(pageNumber);

					int x = (int) ((page.getX() - region.getX()) * requestedZoomFactor);
					int y = (int) ((page.getY() - region.getY()) * requestedZoomFactor);
					int w = (int) (page.getWidth() * requestedZoomFactor);
					int h = (int) (page.getHeight() * requestedZoomFactor);

					graphics2D.setColor(Color.RED);
					graphics2D.drawRect(x, y, w, h);

					graphics2D.setColor(Color.YELLOW);
					graphics2D.fillRect(x + 1, y + 1, w - 1, h - 1);
				}
			}

			if (
					bufferedImage != null &&
					!zoomMismatch &&
					destinationX2 > destinationX1 &&
					destinationY2 > destinationY1 &&
					sourceX2 > sourceX1 &&
					sourceY2 > sourceY1
			)
			{
				graphics2D.drawImage(
						bufferedImage,
						destinationX1,
						destinationY1,
						destinationX2,
						destinationY2,
						sourceX1,
						sourceY1,
						sourceX2,
						sourceY2,
						null
				);
			}

			return bufferSufficient;
		} // synchronized (bufferedImageMutex) {
	}

	/**
	 * Writes a given image to a file (very time-consuming - ONLY FOR DEBUGGING!).
	 *
	 * @param pdfImage the image that shall be written to a file
	 * @param filenamePrefix the prefix of the filename of the file the image is written to dependent on part and round
	 */
	@SuppressWarnings("unused")
	private void printToImageFile(Image pdfImage, String filenamePrefix) {
		if (pdfImage != null) {
			ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
			try {
				writer.setOutput(new FileImageOutputStream(new File("/home/marco/temp/images/" + filenamePrefix + ".png")));
				writer.write((RenderedImage) pdfImage);
				writer.dispose();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public PdfDocument getPdfDocument() {
		return pdfDocument;
	}

	public void setPdfDocument(PdfDocument pdfDocument) {
		this.pdfDocument = pdfDocument;
	}

	public Rectangle2D.Double getBufferedImageBounds() {
		return bufferedImageBounds;
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

	public double getZoomFactor() {
		return zoomFactor;
	}

}
