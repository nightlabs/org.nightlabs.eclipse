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

import org.eclipse.draw2d.geometry.Rectangle;
import org.nightlabs.editor2d.util.J2DUtil;


public class FeedbackShapeFigure 
extends AbstractShapeFigure 
{
  public void setBounds(Rectangle newBounds) 
  {
  	Rectangle oldBounds = getBounds();
  	J2DUtil.transformGeneralShape(getGeneralShape(), oldBounds, newBounds);
  	
  	super.repaint();
  }
  
//  public void setBounds(Rectangle newBounds) 
//  {        
//    Rectangle oldBounds = getBounds();
//    AffineTransform at = J2DUtil.getAffineTransform(oldBounds, newBounds);
//    getGeneralShape().transform(at);
//    for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
//      IFigure child = (IFigure) it.next();
//      if (child instanceof ShapeFigure) {
//        ShapeFigure shapeFigure = (ShapeFigure) child;
//        shapeFigure.transform(at);
//      }
//    }    
//    super.setBounds(J2DUtil.toDraw2D(getGPBounds()));       
//  }  
   
//  public Rectangle getBounds()
//  {
//    Rectangle totalBounds = new Rectangle();
//    Rectangle gpBounds = J2DUtil.toDraw2D(getGeneralShape().getBounds());
//    for (Iterator it = getChildren().iterator(); it.hasNext(); ) 
//    {
//      IFigure child = (IFigure) it.next();
//      if (child instanceof ShapeFigure) {
//        ShapeFigure childFigure = (ShapeFigure) child;
//        Rectangle childBounds = J2DUtil.toDraw2D(childFigure.getGeneralShape().getBounds());
//        gpBounds = gpBounds.union(childBounds);
//      }
//    }
//    return gpBounds;
//  }
  
//  public void transform(AffineTransform at) 
//  {
//    getGeneralShape().transform(at);
//    outlineArea = null;
//    bounds = J2DUtil.toDraw2D(getGeneralShape().getBounds());
//    for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
//      IFigure child = (IFigure) it.next();
//      if (child instanceof ShapeFigure) {
//        ShapeFigure shapeFigure = (ShapeFigure) child;
//        shapeFigure.transform(at);
//      }
//    }
//    repaint();    
//  }   
}
