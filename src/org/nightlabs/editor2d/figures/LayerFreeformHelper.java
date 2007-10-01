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

import java.util.List;

import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.FreeformListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;


public class LayerFreeformHelper 
implements FreeformListener
{
  class ChildTracker implements FigureListener {
  	public void figureMoved(IFigure source) {
  		invalidate();
  	}
  }

  private FreeformFigure host;
  private Rectangle freeformExtent;
  private FigureListener figureListener = new ChildTracker();

  LayerFreeformHelper(FreeformFigure host) {
  	this.host = host;
  }

  public Rectangle getFreeformExtent() 
  {
  	if (freeformExtent != null)
  		return freeformExtent;
  	Rectangle r;
  	List children = host.getChildren();
  	for (int i = 0; i < children.size(); i++) {
  		IFigure child = (IFigure)children.get(i);
//  		if (child instanceof OversizedBufferFreeformLayer)
//  		  r = child.getBounds();
//  		else if (child instanceof FreeformFigure)
//  			r = ((FreeformFigure) child).getFreeformExtent();
  		if (child instanceof FreeformFigure)
  			r = ((FreeformFigure) child).getFreeformExtent();
  		else
  			r = child.getBounds();
  		if (freeformExtent == null)
  			freeformExtent = r.getCopy();
  		else
  			freeformExtent.union(r);
  	}
  	Insets insets = host.getInsets();
  	if (freeformExtent == null)
  		freeformExtent = new Rectangle(0, 0, insets.getWidth(), insets.getHeight());
  	else {
  		host.translateToParent(freeformExtent);
  		freeformExtent.expand(insets);
  	}
//  	System.out.println("New extent calculated for " + host + " = " + freeformExtent);
  	return freeformExtent;
  }

  public void hookChild(IFigure child) 
  {
//    if (child instanceof OversizedBufferFreeformLayer) {
//      return;
//    }      
  	invalidate();  	
  	if (child instanceof FreeformFigure)
  		((FreeformFigure)child).addFreeformListener(this);
  	else
  		child.addFigureListener(figureListener);
  }

  void invalidate() 
  {
  	freeformExtent = null;
  	host.fireExtentChanged();
  	if (host.getParent() != null)
  	  if (host.getParent() instanceof RootDrawComponentFreeformLayer) {
  	    // Do nothing
//  	    LOGGER.debug("host.getParent() instanceof RootDrawComponentFreeformLayer!");
  	  } else {
    		host.getParent().revalidate();
  	  }
  	else
  	  if (host instanceof OversizedBufferFreeformLayer) {
  	    // Do nothing
//  	    LOGGER.debug("host.getParent() instanceof OversizedBufferFreeformLayer!");
  	  }
  	  else {
    		host.revalidate();  	    
  	  }
    
//  	freeformExtent = null;
//  	host.fireExtentChanged();
//  	if (host.getParent() != null)  	  
//  		host.getParent().revalidate();
//  	else
//  		host.revalidate();
  }

  public void notifyFreeformExtentChanged() {
  	//A childs freeform extent has changed, therefore this extent must be recalculated
  	invalidate();
  }

  public void setFreeformBounds(Rectangle bounds) {
  	host.setBounds(bounds);
  	bounds = bounds.getCopy();
  	host.translateFromParent(bounds);
  	List children = host.getChildren();
  	for (int i = 0; i < children.size(); i++) {
  		IFigure child = (IFigure)children.get(i);
  		if (child instanceof FreeformFigure)
  			((FreeformFigure) child).setFreeformBounds(bounds);
  	}
  }

  public void unhookChild(IFigure child) {
  	invalidate();
  	if (child instanceof FreeformFigure)
  		((FreeformFigure)child).removeFreeformListener(this);
  	else
  		child.removeFigureListener(figureListener);
  }
}
