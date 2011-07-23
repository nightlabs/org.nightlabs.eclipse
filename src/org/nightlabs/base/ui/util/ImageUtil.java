/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.ui.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jfire.compatibility.CompatibleGC;
import org.nightlabs.jfire.compatibility.CompatibleSWT;

/**
 * Utility class for working with AWT and SWT images.
 *
 * @author unascribed (probably Daniel Mazurek)
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ImageUtil
{
	private static final Logger logger = Logger.getLogger(ImageUtil.class);

	protected ImageUtil() { }

	/**
	 * converts a SWT Image to a SWT BufferedImage
	 * taken from http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet156.java
	 *
	 * @param data The ImageData of the SWT Image (@link org.eclipse.swt.graphics.Image.getImageData())
	 * @return a (@link java.awt.image.BufferedImage) which is identical to the original SWT Image
	 */
	public static BufferedImage convertToAWT(ImageData data)
	{
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					pixelArray[0] = rgb.red;
					pixelArray[1] = rgb.green;
					pixelArray[2] = rgb.blue;
					raster.setPixels(x, y, 1, 1, pixelArray);
				}
			}
			return bufferedImage;
		} else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte)rgb.red;
				green[i] = (byte)rgb.green;
				blue[i] = (byte)rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
			} else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
			}
			BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}

	/**
	 * converts an AWT BufferedImage to an SWT ImageData
	 * taken from http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet156.java
	 *
	 * @param bufferedImage the (@link java.awt.image.BufferedImage) to convert
	 * @return a (@link org.eclipse.swt.graphics) which can be used to construct a SWT Image
	 */
	public static ImageData convertToSWT(BufferedImage bufferedImage)
	{
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel)bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			WritableRaster raster = bufferedImage.getRaster();
			int numComponents = colorModel.getNumComponents();
			int[] pixelArray = new int[numComponents]; // was hardcoded 3 before, but I just needed 4 (incl. alpha). I hope that numComponents will always be correct. Marco.
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel)bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}

	/**
	 * Load an image from the given input stream.
	 * @param in The input stream to load the image from
	 * @return The loaded image data
	 * @throws IOException In case of an error reading from the input stream
	 * @throws UnsupportedImageException If the image format is not supported
	 */
	public static ImageData loadImage(InputStream in/*, IProgressMonitor monitor*/) throws IOException/*, UnsupportedImageException*/
	{
		// TODO: monitor handling
//		monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.loadingTaskName"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
//		monitor.subTask(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.loadingTaskName")); //$NON-NLS-1$

		ImageData result = null;
		BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
		bufferedInputStream.mark(Integer.MAX_VALUE);

		try {
			ImageLoader imageLoader = new ImageLoader();
			ImageData[] id = imageLoader.load(bufferedInputStream);
			result = id[0];
		} catch(SWTException e) {
			if(e.code == SWT.ERROR_IO)
				throw new IOException(e.getMessage(), e);
//			Activator.warn("Unable to load image using SWT image loader", e); //$NON-NLS-1$
		}

		if(result == null) {
			bufferedInputStream.reset();
			BufferedImage bufferedImage = ImageIO.read(bufferedInputStream);
			if(bufferedImage == null)
				throw new RuntimeException("Unable to load image"); //$NON-NLS-1$
			result = convertToSWT(bufferedImage);
			if(result == null)
				throw new RuntimeException("Unable to load image"); //$NON-NLS-1$
		}

//		monitor.done();

		return result;
	}

	private static final class ImageColorEntry {
		public Image image;
		public org.eclipse.swt.graphics.Color color;

		public ImageColorEntry(Image image, org.eclipse.swt.graphics.Color color) {
			if (image == null)
				throw new IllegalArgumentException("image == null"); //$NON-NLS-1$

			if (color == null)
				throw new IllegalArgumentException("color == null"); //$NON-NLS-1$

			this.image = image;
			this.color = color;
		}
	}

	private static LinkedList<ImageColorEntry> temporaryImageColors = new LinkedList<ImageColorEntry>();
	private static Timer disposeTemporaryColorsTimer = null;

	private static void disposeUnusedTemporaryColors()
	{
		int nondisposedColorCount;

		LinkedList<ImageColorEntry> imageColorsToDispose = new LinkedList<ImageColorEntry>();

		synchronized (temporaryImageColors) {
			for (Iterator<ImageColorEntry> it = temporaryImageColors.iterator(); it.hasNext();) {
				ImageColorEntry imageColorEntry = it.next();
				if (imageColorEntry.image.isDisposed()) {
					if (logger.isTraceEnabled())
						logger.trace("disposeUnusedTemporaryColors: image=" + Integer.toHexString(System.identityHashCode(imageColorEntry.image)) + " color=" + Integer.toHexString(System.identityHashCode(imageColorEntry.color))); //$NON-NLS-1$ //$NON-NLS-2$

					imageColorsToDispose.add(imageColorEntry);
					it.remove();
				}
			}
			nondisposedColorCount = temporaryImageColors.size();

			if (nondisposedColorCount == 0 && disposeTemporaryColorsTimer != null) {
				disposeTemporaryColorsTimer.cancel();
				disposeTemporaryColorsTimer = null;
			}
		} // synchronized (temporaryImageColors) {

		// Dispose outside of the synchronized block, because that otherwise causes dead-locks sometimes
		// (the Color.dispose() method does some other synchronisation).
		for (ImageColorEntry imageColorEntry : imageColorsToDispose) {
			imageColorEntry.color.dispose();
		}

		if (logger.isDebugEnabled())
			logger.debug("disposeUnusedTemporaryColors: Disposed " + imageColorsToDispose.size() + " unused temporary colors. Still non-disposed are " + nondisposedColorCount + " colors."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private static void registerTemporaryColor(Image image, org.eclipse.swt.graphics.Color color)
	{
		synchronized (temporaryImageColors) {
			temporaryImageColors.add(new ImageColorEntry(image, color));

			if (disposeTemporaryColorsTimer == null) {
				disposeTemporaryColorsTimer = new Timer("DisposeTemporaryColors", true); //$NON-NLS-1$
				disposeTemporaryColorsTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						disposeUnusedTemporaryColors();
					}
				}, 30000, 30000);
			}
		} // synchronized (temporaryImageColors) {

		if (logger.isTraceEnabled())
			logger.trace("registerTemporaryColor: image=" + Integer.toHexString(System.identityHashCode(image)) + " color=" + Integer.toHexString(System.identityHashCode(color))); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create an image with a width and a height of 16 pixel filled by the specified AWT color.
	 * <b>You must ensure that the image is disposed when not needed anymore!</b>
	 * <p>
	 * This is a convenience method delegating to {@link #createColorImage(Color, int, int)}.
	 * </p>
	 * <p>
	 * The SWT {@link org.eclipse.swt.graphics.Color} instance that is created internally on-the-fly
	 * is automatically disposed when the returned {@link Image} is disposed. Because the {@link Image}
	 * class doesn't support {@link DisposeListener}s, this is implemented with a {@link Timer} and therefore
	 * delayed. Though this should never cause a problem in real life, you might at least theoretically run into trouble,
	 * if you create and dispose many images very fast in a short time. In this unlikely case, consider using
	 * {@link #createColorImage(org.eclipse.swt.graphics.Color)} instead
	 * and manage the SWT {@link org.eclipse.swt.graphics.Color} instances yourself (pool them and dispose them
	 * immediately whenever possible).
	 * </p>
	 *
	 * @param color the AWT color to fill the image.
	 * @return the newly created image.
	 * @see #createColorImage(Color, int, int)
	 */
	public static Image createColorImage(Color color) {
		return createColorImage(color, 16, 16);
	}

	/**
	 * Create an image with a width and a height of 16 pixel filled by the specified SWT color.
	 * <b>You must ensure that the image is disposed when not needed anymore!</b>
	 * <p>
	 * This is a convenience method delegating to {@link #createColorImage(org.eclipse.swt.graphics.Color, int, int)}.
	 * </p>
	 *
	 * @param color the SWT color to fill the image.
	 * @return the newly created image.
	 * @see #createColorImage(org.eclipse.swt.graphics.Color, int, int)
	 */
	public static Image createColorImage(org.eclipse.swt.graphics.Color color) {
		return createColorImage(color, 16, 16);
	}

	/**
	 * Create an image filled by the specified AWT color.
	 * <b>You must ensure that the image is disposed when not needed anymore!</b>
	 * <p>
	 * The SWT {@link org.eclipse.swt.graphics.Color} instance that is created internally on-the-fly
	 * is automatically disposed when the returned {@link Image} is disposed. Because the {@link Image}
	 * class doesn't support {@link DisposeListener}s, this is implemented with a {@link Timer} and therefore
	 * delayed. Though this should never cause a problem in real life, you might at least theoretically run into trouble,
	 * if you create and dispose many images very fast in a short time. In this unlikely case, consider using
	 * {@link #createColorImage(org.eclipse.swt.graphics.Color, int, int)} instead
	 * and manage the SWT {@link org.eclipse.swt.graphics.Color} instances yourself (pool them and dispose them
	 * immediately whenever possible).
	 * </p>
	 *
	 * @param color the AWT color to fill the image.
	 * @return the newly created image.
	 * @see #createColorImage(Color)
	 */
	public static Image createColorImage(Color color, int width, int height)
	{
		Image image = CompatibleSWT.newImage(Display.getDefault(), width, height);
		GC gc = CompatibleGC.newGC(image);
		try {
			org.eclipse.swt.graphics.Color swtColor = ColorUtil.toSWTColor(color);
			registerTemporaryColor(image, swtColor);
			gc.setBackground(swtColor);
			gc.fillRectangle(0, 0, width, height);
			gc.drawRectangle(0,0,width-1,height-1);
		} finally {
			gc.dispose();
		}
		return image;
	}

	/**
	 * Create an image filled by the specified SWT color.
	 * <b>You must ensure that the image is disposed when not needed anymore!</b>
	 *
	 * @param color the SWT color to fill the image.
	 * @return the newly created image.
	 * @see #createColorImage(org.eclipse.swt.graphics.Color)
	 */
	public static Image createColorImage(org.eclipse.swt.graphics.Color color, int width, int height)
	{
		Image image = CompatibleSWT.newImage(Display.getDefault(), width, height);
		GC gc = CompatibleGC.newGC(image);
		try {
			gc.setBackground(color);
			gc.fillRectangle(0, 0, width, height);
			gc.drawRectangle(0,0,width-1,height-1);
		} finally {
			gc.dispose();
		}
		return image;
	}


	public static Image createLineStyleImage(int lineStyle, int width, int height)
	{
		Image image = CompatibleSWT.newImage(Display.getDefault(), width, height);
		GC gc = CompatibleGC.newGC(image);
		try {
			CompatibleGC.setLineStyle(gc, lineStyle);
			gc.setLineWidth(5);
			gc.setForeground(new org.eclipse.swt.graphics.Color(Display.getDefault(), 0, 0, 0));
			gc.drawLine(1, height/2, width-1, height/2);
		} finally {
			gc.dispose();
		}
		return image;
	}

	public static Image createLineStyleImage(int lineStyle) {
		return createLineStyleImage(lineStyle, 16, 64);
	}

	/**
	 *this method Resizes an Image SWT
	 * @param image to resize
	 * @param width, height new desired Image Size
	 * @param highQuality if the resize should be high quality thus slower
	 * @return Image of the new resized Image
	 */
	public static Image resize(Image image, int width, int height,Boolean highQuality) {
		Image scaled = CompatibleSWT.newImage(Display.getDefault(), width, height);
		GC gc = CompatibleGC.newGC(scaled);
		if(highQuality)
		{
			CompatibleGC.setAntialias(gc, SWT.ON);
			CompatibleGC.setInterpolation(gc, CompatibleSWT.HIGH);
		}
		gc.drawImage(image, 0, 0,
				image.getBounds().width, image.getBounds().height,
				0, 0, width, height);
		gc.dispose();
		return scaled;
	}

}
