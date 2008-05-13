package org.nightlabs.eclipse.ui.fckeditor.file;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ImageClippingArea extends Canvas implements PaintListener
{
	private Image sourceImage;
	private Image previewImage;
	private Rectangle clippingArea;

	/**
	 * Create a new ImageClippingArea instance.
	 * @param parent
	 * @param style
	 */
	public ImageClippingArea(Composite parent, int style)
	{
		super(parent, style);
		addPaintListener(this);
	}

	private static final int outerBorder = 1;

	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		if (sourceImage != null) {
			ImageData sourceImageData = sourceImage.getImageData();
			Point imageBounds = new Point(sourceImageData.width, sourceImageData.height);
			Point myBounds = new Point(getClientArea().width - getClientArea().x, getClientArea().height - getClientArea().y);
			float mx = (float)imageBounds.x / (float)myBounds.x;
			float my = (float)imageBounds.y / (float)myBounds.y;
			float m = Math.max(mx, my);
			if(previewImage == null) {
				previewImage = new Image(getDisplay(), sourceImage.getImageData().scaledTo(Math.round(imageBounds.x / m) - outerBorder*2, Math.round(imageBounds.y / m) - 2));
				ImageData previewImageData = previewImage.getImageData();
				clippingArea = new Rectangle(0 + 20, 0 + 30, previewImageData.width - 40, previewImageData.height - 80);
//				clippingArea = new Rectangle(0, 0, previewImageData.width, previewImageData.height);
			}
			ImageData previewImageData = previewImage.getImageData();
			int offsetX = Math.round((myBounds.x - previewImageData.width - outerBorder) / 2f);
			int offsetY = Math.round((myBounds.y - previewImageData.height - outerBorder) / 2f);
			System.out.println("offset: "+offsetX+"/"+offsetY);
			int imageX = getClientArea().x + offsetX;
			int imageY = getClientArea().y + offsetY;
			gc.drawImage(previewImage, imageX, imageY);

			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			gc.drawRectangle(
					imageX -1 + clippingArea.x,
					imageY -1 + clippingArea.y,
					clippingArea.width + 1,
					clippingArea.height + 1);

//			gc.setBackgroundPattern(new Pattern(getDisplay(), sourceImage));
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
//			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.setAlpha(50);
			gc.setFillRule(SWT.FILL_EVEN_ODD);
			gc.fillRectangle(imageX, imageY, clippingArea.x, clippingArea.y);
		}
//		if (text != null) {
//			gc.drawString(text, x, 1);
//		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose()
	{
		super.dispose();
		if(previewImage != null)
			previewImage.dispose();
	}

	/**
	 * Get the sourceImage.
	 * @return the sourceImage
	 */
	public Image getSourceImage()
	{
		return sourceImage;
	}

	/**
	 * Set the sourceImage.
	 * @param sourceImage the sourceImage to set
	 */
	public void setSourceImage(Image sourceImage)
	{
		this.sourceImage = sourceImage;
		if(previewImage != null) {
			previewImage.dispose();
			previewImage = null;
		}
	}

	public Rectangle getClippingArea()
	{
		return clippingArea;
	}
}
