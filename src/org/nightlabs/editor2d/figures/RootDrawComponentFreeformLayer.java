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

import java.util.Map;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Rectangle;

import org.nightlabs.editor2d.edit.RootDrawComponentEditPart;
import org.nightlabs.editor2d.util.EditorUtil;


public class RootDrawComponentFreeformLayer 
extends FreeformLayer 
implements UpdateListener
{
  public RootDrawComponentFreeformLayer() {
    super();    
  }
    
  public void registerOnDeferredUpdateManager(UpdateManager updateManager) {
    if (updateManager == null)
      return;
    updateManager.removeUpdateListener(this);
    updateManager.addUpdateListener(this);
  }
    
  private RootDrawComponentEditPart rootEditPart;    
	public void setMldcEditPart(RootDrawComponentEditPart rootEditPart) {
	  this.rootEditPart = rootEditPart;
	}
	
  private Rectangle notifiedDamage;
  
  public void notifyPainting(Rectangle damage, Map dirtyRegions) {
  	if (rootEditPart == null)
  		return;
    notifiedDamage = damage;
    notifiedDamage = EditorUtil.toAbsolute(rootEditPart, damage);
//    System.out.println("MLDC notify Painting called with "+damage);
  }
  
  public void notifyValidating() {
  }
      
//  public void paint(Graphics graphics) {
//  	super.paint(graphics);
//    System.out.println("MLDC paint called with ");
//    if (notifiedDamage == null) {
//      super.paint(graphics);
//      return;
//    }
//    
////    System.out.println("MLDC notifiedDamage != null");
//    for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
//      IFigure child = (IFigure) iter.next();
//      if (child.intersects(notifiedDamage)) {
//        graphics.pushState();
//        try {
//          graphics.setClip(child.getBounds());
////          System.out.println("MLDC paint child: "+child+" child.getBounds() "+child.getBounds());
//          child.paint(graphics);
//          graphics.restoreState();
//        } finally
//        {
//          graphics.popState();
//        }
//      }
//    }
//    notifiedDamage = null;
//  }
 }
