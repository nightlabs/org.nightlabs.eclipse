/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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

package org.nightlabs.editor2d.viewer.ui.util;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class AWTSWTUtil
{
	public static Rectangle toAWTRectangle(org.eclipse.swt.graphics.Rectangle rectangle) {
	  return new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}
	
	public static org.eclipse.swt.graphics.Rectangle toSWTRectangle(Rectangle rect) {
		return new org.eclipse.swt.graphics.Rectangle(rect.x, rect.y, rect.width, rect.height);
	}
	 
	public static Path convertShape(Shape s, AffineTransform at, Device device)
	{
		float[] coords = new float[6];
		Path path = new Path(device);
    for (PathIterator pi = s.getPathIterator(at); !pi.isDone(); pi.next())
    {
      int segType = pi.currentSegment(coords);
      switch (segType)
      {
        case (PathIterator.SEG_MOVETO):
        	path.moveTo(coords[0], coords[1]);
          break;
        case (PathIterator.SEG_LINETO):
        	path.lineTo(coords[0], coords[1]);
          break;
        case (PathIterator.SEG_QUADTO):
        	path.quadTo(coords[0], coords[1], coords[2], coords[3]);
          break;
        case (PathIterator.SEG_CUBICTO):
        	path.cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
          break;
        case (PathIterator.SEG_CLOSE):
        	path.close();
          break;
      }
    }
		return path;
	}
	
	/**
	 * converts an AWT BufferedImage to an SWT ImageData
	 * taken from http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet156.java
	 * 
	 * @param bufferedImage the (@link java.awt.image.BufferedImage) to convert
	 * @return a (@link org.eclipse.swt.graphics) which can be used to construct a SWT Image
	 */
	public static ImageData toSWTImageData(BufferedImage bufferedImage)
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
		else
			return convertToSWTImageData(bufferedImage, Display.getDefault());
//		return null;
	}
	
	public static Image toSWTImage(BufferedImage img, Device dev)
	{
		return new Image(dev, toSWTImageData(img));
	}
	
	public static Image convertToSWTImage(java.awt.Image ai, Device display)
	{
	  int width = ai.getWidth(null);
	  int height = ai.getHeight(null);
	  BufferedImage bufferedImage =
	    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	  Graphics2D g2d = bufferedImage.createGraphics();
	  g2d.drawImage(ai, 0, 0, null);
	  g2d.dispose();
	  int[] data =
	    ((DataBufferInt)bufferedImage.getData().getDataBuffer())
	    .getData();
	  ImageData imageData =
	    new ImageData(width, height, 24,
	      new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
	  imageData.setPixels(0, 0, data.length, data, 0);
	  Image swtImage = new Image(display, imageData);
	  return swtImage;
	}
	
	public static ImageData convertToSWTImageData(java.awt.Image ai, Device display)
	{
	  int width = ai.getWidth(null);
	  int height = ai.getHeight(null);
	  BufferedImage bufferedImage =
	    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	  Graphics2D g2d = bufferedImage.createGraphics();
	  g2d.drawImage(ai, 0, 0, null);
	  g2d.dispose();
	  int[] data =
	    ((DataBufferInt)bufferedImage.getData().getDataBuffer())
	    .getData();
	  ImageData imageData =
	    new ImageData(width, height, 24,
	      new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
	  return imageData;
	}
}
