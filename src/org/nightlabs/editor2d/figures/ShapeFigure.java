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

package org.nightlabs.editor2d.figures;

import java.awt.geom.AffineTransform;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Translatable;

import org.nightlabs.editor2d.handle.HandleShape;
import org.nightlabs.editor2d.j2d.GeneralShape;


public interface ShapeFigure 
extends IFigure,
				Translatable,
				HandleShape
{
  public GeneralShape getGeneralShape();
  public void setGeneralShape(GeneralShape generalShape);  
//  public double getRotation();
//  public java.awt.Rectangle setRotation(double rot, boolean init);
  public void transform(AffineTransform at); 
  public void setXOR(boolean b);  
  public void setFill(boolean b);
  public int getLineWidth();
  public void setLineWidth(int lineWidth);
  public int getLineStyle();
  public void setLineStyle(int lineStyle);  
}
