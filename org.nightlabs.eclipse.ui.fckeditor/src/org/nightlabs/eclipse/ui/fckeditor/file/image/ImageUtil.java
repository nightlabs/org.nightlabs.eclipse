/* ************************************************************************
 * org.nightlabs.eclipse.ui.fckeditor - Eclipse RCP FCKeditor Integration *
 * Copyright (C) 2008 NightLabs - http://NightLabs.org                    *
 *                                                                        *
 * This library is free software; you can redistribute it and/or          *
 * modify it under the terms of the GNU Lesser General Public             *
 * License as published by the Free Software Foundation; either           *
 * version 2.1 of the License, or (at your option) any later version.     *
 *                                                                        *
 * This library is distributed in the hope that it will be useful,        *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU      *
 * Lesser General Public License for more details.                        *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with this library; if not, write to the                  *
 *     Free Software Foundation, Inc.,                                    *
 *     51 Franklin St, Fifth Floor,                                       *
 *     Boston, MA  02110-1301  USA                                        *
 *                                                                        *
 * Or get it online:                                                      *
 *     http://www.gnu.org/copyleft/lesser.html                            *
 **************************************************************************/
package org.nightlabs.eclipse.ui.fckeditor.file.image;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public class ImageUtil
{
	/**
	 * converts an SWT ImageData to an AWT BufferedImage
	 * taken from http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet156.java
	 *
	 * @param data The ImageData of the SWT Image
	 * @return a BufferedImage which is identical to the original SWT Image
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
	 * @param bufferedImage the image to convert
	 * @return the corresponding SWT image data object
	 * @throws UnsupportedImageException if the color model of the given bufferedImage is unknown.
	 */
	public static ImageData convertToSWT(BufferedImage bufferedImage) throws UnsupportedImageException
	{
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel)bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
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
		String colorModelName = bufferedImage.getColorModel().getClass().getName();
		throw new UnsupportedImageException(
				String.format(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.colorModelError"), colorModelName)); //$NON-NLS-1$
	}

	/**
	 * Load an image from the given input stream.
	 * @param in The input stream to load the image from
	 * @return The loaded image data
	 * @throws IOException In case of an error reading from the input stream
	 * @throws UnsupportedImageException If the image format is not supported
	 */
	public static ImageData loadImage(InputStream in, IProgressMonitor monitor) throws IOException, UnsupportedImageException
	{
		// TODO: monitor handling
		monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.loadingTaskName"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
		monitor.subTask(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.loadingTaskName")); //$NON-NLS-1$

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
			Activator.warn("Unable to load image using SWT image loader", e); //$NON-NLS-1$
		}

		if(result == null) {
			bufferedInputStream.reset();
			BufferedImage bufferedImage = ImageIO.read(bufferedInputStream);
			if(bufferedImage == null)
				throw new UnsupportedImageException(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.imageLoadingError")); //$NON-NLS-1$
			result = convertToSWT(bufferedImage);
			if(result == null)
				throw new UnsupportedImageException(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.imageLoadingError")); //$NON-NLS-1$
		}

		monitor.done();

		return result;
	}

	/**
	 * Load an image from the given file.
	 * @param file The file to load the image from
	 * @return The loaded image data
	 * @throws IOException In case of an error reading from the file
	 * @throws UnsupportedImageException If the image format is not supported
	 */
	public static ImageData loadImage(File file, IProgressMonitor monitor) throws IOException, UnsupportedImageException
	{
		// TODO: monitor handling
		monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.loadingTaskName"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
		monitor.subTask(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.loadingTaskName")); //$NON-NLS-1$

		ImageData result = null;

		try {
			ImageLoader imageLoader = new ImageLoader();
			ImageData[] id = imageLoader.load(file.getAbsolutePath());
			result = id[0];
		} catch(SWTException e) {
			if(e.code == SWT.ERROR_IO)
				throw new IOException(e.getMessage(), e);
			Activator.warn("Unable to load image using SWT image loader: "+file.getAbsolutePath(), e); //$NON-NLS-1$
		}

		if(result == null) {
			BufferedImage bufferedImage = ImageIO.read(new FileInputStream(file));
//			BufferedImage bufferedImage = ImageIO.read(file);
			if(bufferedImage == null)
				throw new UnsupportedImageException(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.imageLoadingError")); //$NON-NLS-1$
			result = convertToSWT(bufferedImage);
			if(result == null)
				throw new UnsupportedImageException(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.imageLoadingError")); //$NON-NLS-1$
		}

		monitor.done();

		return result;
	}

	private static Map<String, Integer> imageTypes;
	static {
		imageTypes = new HashMap<String, Integer>();
		imageTypes.put("image/jpeg", SWT.IMAGE_JPEG); //$NON-NLS-1$
		imageTypes.put("image/png", SWT.IMAGE_PNG); //$NON-NLS-1$
		imageTypes.put("image/gif", SWT.IMAGE_GIF); //$NON-NLS-1$
		imageTypes.put("image/tiff", SWT.IMAGE_TIFF); //$NON-NLS-1$

		imageTypes.put("image/vnd.microsoft.icon", SWT.IMAGE_ICO); //$NON-NLS-1$
		imageTypes.put("image/x-icon", SWT.IMAGE_ICO); //$NON-NLS-1$
		imageTypes.put("image/ico", SWT.IMAGE_ICO); //$NON-NLS-1$
		imageTypes.put("image/icon", SWT.IMAGE_ICO); //$NON-NLS-1$
		imageTypes.put("text/ico", SWT.IMAGE_ICO); //$NON-NLS-1$
		imageTypes.put("application/ico", SWT.IMAGE_ICO); //$NON-NLS-1$

		imageTypes.put("image/bmp", SWT.IMAGE_BMP); //$NON-NLS-1$
		imageTypes.put("image/x-bmp", SWT.IMAGE_BMP); //$NON-NLS-1$
		imageTypes.put("image/x-bitmap", SWT.IMAGE_BMP); //$NON-NLS-1$
		imageTypes.put("image/x-xbitmap", SWT.IMAGE_BMP); //$NON-NLS-1$
		imageTypes.put("image/x-win-bitmap", SWT.IMAGE_BMP); //$NON-NLS-1$
		imageTypes.put("image/x-windows-bmp", SWT.IMAGE_BMP); //$NON-NLS-1$
		imageTypes.put("image/ms-bmp", SWT.IMAGE_BMP); //$NON-NLS-1$
		imageTypes.put("image/x-ms-bmp", SWT.IMAGE_BMP); //$NON-NLS-1$
		imageTypes.put("application/bmp", SWT.IMAGE_BMP); //$NON-NLS-1$
		imageTypes.put("application/x-bmp", SWT.IMAGE_BMP); //$NON-NLS-1$
		imageTypes.put("application/x-win-bitmap", SWT.IMAGE_BMP); //$NON-NLS-1$

		imageTypes.put("application/rle", SWT.IMAGE_BMP_RLE); //$NON-NLS-1$
		imageTypes.put("application/x-rle", SWT.IMAGE_BMP_RLE); //$NON-NLS-1$
		imageTypes.put("image/rle", SWT.IMAGE_BMP_RLE); //$NON-NLS-1$
	}

	public static void saveImage(ImageData imageData, String mimeType, OutputStream out, final IProgressMonitor monitor) throws IOException, UnsupportedImageException
	{
		monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.savingTaskName"), 120); //$NON-NLS-1$
		monitor.subTask(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.savingTaskName")); //$NON-NLS-1$

		Integer swtFormat = imageTypes.get(mimeType);
		if(swtFormat != null) {
			try {
				monitor.worked(20);
				ImageLoader imageLoader = new ImageLoader();
				imageLoader.data = new ImageData[] { imageData };
//				imageLoader.addImageLoaderListener(new ImageLoaderListener() {
//					int done = 0;
//					@Override
//					public void imageDataLoaded(ImageLoaderEvent e)
//					{
//						if(done < 90) {
//							monitor.worked(1);
//							System.out.println("worked: "+1);
//							done++;
//						}
//					}
//				});
				imageLoader.save(out, swtFormat);
				monitor.worked(100);
				monitor.done();
			} catch(SWTException e) {
				Activator.warn("Unable to save image using SWT image loader", e); //$NON-NLS-1$
				throw new IOException(e.getMessage(), e);
			}
		} else {
			monitor.worked(5);
			BufferedImage im = convertToAWT(imageData);
			monitor.worked(15);
			ImageWriter writer = null;
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByMIMEType(mimeType);
			if (iter.hasNext())
				writer = iter.next();
			if (writer == null)
				throw new UnsupportedImageException(
						String.format(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ImageUtil.imageSaveError"), mimeType)); //$NON-NLS-1$

			writer.setOutput(out);
			writer.addIIOWriteProgressListener(new IIOWriteProgressListener() {
				float lastPercentageDone = 0f;
				int done;
				@Override
				public void imageComplete(ImageWriter source) {}
				@Override
				public void imageProgress(ImageWriter source, float percentageDone)
				{
					float diff = percentageDone - lastPercentageDone;
					int worked = Math.round(diff * 100f);
					if(worked + done > 100)
						worked = 100 - done;
					if(worked > 0) {
						monitor.worked(worked);
						System.out.println("worked: "+worked);
						done += worked;
					}
					lastPercentageDone = percentageDone;
				}
				@Override
				public void imageStarted(ImageWriter source, int imageIndex) {}
				@Override
				public void thumbnailComplete(ImageWriter source) {}
				@Override
				public void thumbnailProgress(ImageWriter source, float percentageDone) {}
				@Override
				public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex) {}
				@Override
				public void writeAborted(ImageWriter source) {}
				{
				}
			});
			try {
				writer.write(im);
				monitor.done();
			} finally {
				writer.dispose();
				out.flush();
			}
		}
	}

	public static void saveImage(ImageData imageData, String mimeType, File file, final IProgressMonitor monitor) throws IOException, UnsupportedImageException
	{
		FileOutputStream out = new FileOutputStream(file);
		try {
			saveImage(imageData, mimeType, out, monitor);
		} finally {
			out.close();
		}
	}
}
