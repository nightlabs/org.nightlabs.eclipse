/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ColorUtil extends org.nightlabs.util.ColorUtil
{

	protected ColorUtil()
	{
	}

	public static String rgbToString(RGB rgb)
	{
		return "RGBA{" //$NON-NLS-1$
				+ rgb.red + ','
				+ rgb.green + ','
				+ rgb.blue + ",255}"; // SWT RGB doesn't know alpha. We set it to 255 even though we could leave it out.  //$NON-NLS-1$
	}

	public static RGB stringToRGB(String color)
	{
		RGBA rgba = parseRGBA(color);
		return new RGB(rgba.r, rgba.g, rgba.b);
	}

	public static String swtColorToString(Color color)
	{
		return "RGBA{" //$NON-NLS-1$
		+ color.getRed() + ','
		+ color.getGreen() + ','
		+ color.getBlue() + ",255}"; // SWT Color doesn't know alpha. We set it to 255 even though we could leave it out. //$NON-NLS-1$
	}

	/**
	 * <b>Important:</b> You must dispose the returned color when you don't need it
	 * anymore!
	 */
	public static Color stringToSWTColor(String color)
	{
		RGBA rgba = parseRGBA(color);
		return new Color(Display.getDefault(), rgba.r, rgba.g, rgba.b);
	}

	/**
	 * <b>Important:</b> You must dispose the returned color when you don't need it
	 * anymore!
	 */
	public static Color stringToSWTColor(Device device, String color)
	{
		RGBA rgba = parseRGBA(color);
		if (device == null)
			device = Display.getDefault();
		return new Color(device, rgba.r, rgba.g, rgba.b);
	}
	
	/**
	 * A utility method to convert an SWT Color to an AWT one.
	 * 
	 * @param c
	 *            The SWT Color
	 * @return An equivalent AWT Color
	 */
	public static java.awt.Color toAWTColor(Color c) {
		return new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue());
	}
	
	/**
	 * A utility method to convert an AWT Color to an SWT one.
	 * The default display is used.
	 * Resource disposal should be performed by the caller.
	 * 
	 * @param c
	 *            The AWT Color
	 * @return An equivalent SWT Color
	 */
	public static Color toSWTColor(java.awt.Color c) {
		return new Color(Display.getDefault(),c.getRed(), c.getGreen(), c.getBlue());
	}
	
	public static java.awt.Color toAWTColor(RGB rgb) 
	{
	  if (rgb == null)
      throw new IllegalArgumentException("Param rgb must not be null!"); //$NON-NLS-1$
	  return new java.awt.Color(rgb.red, rgb.green, rgb.blue);
	}
	
	public static RGB toRGB(java.awt.Color c) 
	{
	  if(c == null)
	    throw new IllegalArgumentException("Param c must not be null!"); //$NON-NLS-1$
	    
	  return new RGB(c.getRed(), c.getGreen(), c.getBlue());
	}	
}
