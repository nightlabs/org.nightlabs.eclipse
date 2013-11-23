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
package org.nightlabs.editor2d.ui.util.feedback;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PaintDescriptor
{
  public static final Color DEFAULT_BGCOLOR = ColorConstants.darkGray;
  public static final Color DEFAULT_FGCOLOR = ColorConstants.black;
  public static final int DEFAULT_LINE_WIDTH = 1;
  public static final int DEFAULT_LINE_STYLE = 1;
  public static final boolean DEFAULT_XOR = true;
  public static final boolean DEFAULT_FILL = true;
  public static final boolean DEFAULT_OUTLINE = true;
  
	public PaintDescriptor() {
		super();
	}

	protected boolean fill = DEFAULT_FILL;
	public boolean isFill() {
		return fill;
	}
	public void setFill(boolean fill) {
		this.fill = fill;
	}
	
	protected boolean xor = DEFAULT_XOR;
	public boolean isXor() {
		return xor;
	}
	public void setXor(boolean xor) {
		this.xor = xor;
	}
	
	protected int lineWidth = DEFAULT_LINE_WIDTH;
	public int getLineWidth() {
		return lineWidth;
	}
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	protected int lineStyle = DEFAULT_LINE_STYLE;
	public int getLineStyle() {
		return lineStyle;
	}
	public void setLineStyle(int lineStyle) {
		this.lineStyle = lineStyle;
	}

	protected boolean outline = DEFAULT_OUTLINE;
	public boolean isOutline() {
		return outline;
	}
	public void setOutline(boolean outline) {
		this.outline = outline;
	}
	
  protected Color bgColor = DEFAULT_BGCOLOR;
  public Color getBackgroundColor() {
  	return bgColor;
  }
  public void setBackgroundColor(Color bgColor) {
  	this.bgColor = bgColor;
  }
  
  protected Color fgColor = DEFAULT_FGCOLOR;
  public Color getForegroundColor() {
  	return fgColor;
  }
  public void setForegroundColor(Color fgColor) {
  	this.fgColor = fgColor;
  }
}
