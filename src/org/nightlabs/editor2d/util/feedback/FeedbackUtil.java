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

package org.nightlabs.editor2d.util.feedback;

import org.eclipse.swt.graphics.Color;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.figures.FeedbackShapeFigure;
import org.nightlabs.editor2d.figures.ShapeFigure;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.j2d.GeneralShapeFactory;


public class FeedbackUtil 
{  
  public FeedbackUtil() 
  {
    super(); 
  }
    
  public static final PaintDescriptor DEFAULT_PAINT_DESCRIPTOR = new PaintDescriptor(); 
  
  public static ShapeFigure getCustomFeedbackFigure(Object modelPart) 
	//protected IFigure getCustomFeedbackFigure(Object modelPart)
	{
  	return getCustomFeedbackFigure(modelPart, DEFAULT_PAINT_DESCRIPTOR);
	}    
       
  public static ShapeFigure getCustomFeedbackFigure(Object modelPart, PaintDescriptor pd) 
	//protected IFigure getCustomFeedbackFigure(Object modelPart)
	{
	  GeneralShape gs = null;
	  ShapeFigure shapeFigure = new FeedbackShapeFigure();	  
	  if (modelPart instanceof ShapeDrawComponent) {
	    ShapeDrawComponent sdc = (ShapeDrawComponent) modelPart;
	    gs = (GeneralShape)sdc.getGeneralShape().clone();
	  }
	  else if (modelPart instanceof ImageDrawComponent) {
	    ImageDrawComponent idc = (ImageDrawComponent) modelPart;
	    gs = (GeneralShape) idc.getImageShape().clone();
	  }
	  else if (modelPart instanceof DrawComponentContainer) 
	  {
	    DrawComponentContainer container = (DrawComponentContainer) modelPart;
	    gs = GeneralShapeFactory.createRectangle(1,1,1,1);
	//    for (Iterator it = container.getDrawComponents().iterator(); it.hasNext(); ) {
	//      DrawComponent dc = (DrawComponent) it.next(); 
	//      ShapeFigure figure = getCustomFeedbackFigure(dc);
	//      shapeFigure.add(figure);
	//    }	          
	  }    
	  else {
	    gs = GeneralShapeFactory.createRectangle(0, 0, 10, 10);      
	  }
	  shapeFigure.setGeneralShape(gs);
	  shapeFigure.setXOR(pd.isXor());
	  shapeFigure.setFill(pd.isFill());
	  shapeFigure.setBackgroundColor(pd.getBackgroundColor());       
	  shapeFigure.setForegroundColor(pd.getForegroundColor());
	  return shapeFigure;    
	}
  
  protected static PaintDescriptor pd = new PaintDescriptor();
  
  public static ShapeFigure getCustomFeedbackFigure(Object modelPart, Color bgColor, Color fgColor,
  		boolean fill, boolean outline, boolean xor, int lineStyle, int lineWidth)
  {
  	pd.setFill(fill);
  	pd.setOutline(outline);
  	pd.setXor(xor);
  	pd.setLineStyle(lineStyle);
  	pd.setLineWidth(lineWidth);
  	pd.setBackgroundColor(bgColor);
  	pd.setForegroundColor(fgColor);
  	return getCustomFeedbackFigure(modelPart, pd);
  }
  
  public static ShapeFigure getCustomFeedbackFigure(Object modelPart, Color bgColor, Color fgColor)
  {
  	return getCustomFeedbackFigure(modelPart, bgColor, fgColor, PaintDescriptor.DEFAULT_FILL, 
  			PaintDescriptor.DEFAULT_OUTLINE, PaintDescriptor.DEFAULT_XOR, PaintDescriptor.DEFAULT_LINE_STYLE,
  			PaintDescriptor.DEFAULT_LINE_WIDTH);
  }
}
