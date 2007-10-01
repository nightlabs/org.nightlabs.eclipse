/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.request;

import java.awt.image.ColorConvertOp;

import org.eclipse.gef.requests.CreateRequest;

public class ImageCreateRequest 
extends CreateRequest 
{

  public ImageCreateRequest() {
    super();
  }

  public ImageCreateRequest(Object type) {
    super(type);
  }

  protected String fileName = null;
  public String getFileName() {
    return fileName;
  }
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
    
  protected ColorConvertOp colorConvertOp = null;
	/**
	 * @return Returns the colorConvertOp.
	 */
	public ColorConvertOp getColorConvertOp() {
		return colorConvertOp;
	}
	/**
	 * @param colorConvertOp The colorConvertOp to set.
	 */
	public void setColorConvertOp(ColorConvertOp colorConvertOp) {
		this.colorConvertOp = colorConvertOp;
	}
  
}
