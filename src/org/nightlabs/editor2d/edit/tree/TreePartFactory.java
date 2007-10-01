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

package org.nightlabs.editor2d.edit.tree;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.GroupDrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RectangleDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.outline.filter.FilterManager;


public class TreePartFactory 
implements EditPartFactory 
{
	public TreePartFactory(FilterManager filterMan) 
	{
		if (filterMan == null)
			throw new IllegalArgumentException("Param filterMan must not be null!"); //$NON-NLS-1$
		
		this.filterMan = filterMan;
	}
	protected FilterManager filterMan;
	
  public EditPart createEditPart(EditPart context, Object model) 
  {
  	if (model instanceof RootDrawComponent)
  		return new RootDrawComponentTreeEditPart((RootDrawComponent)model, filterMan);

  	else if (model instanceof Layer)
  		return new LayerTreeEditPart((Layer)model);
  	  	
  	else if (model instanceof EllipseDrawComponent)
  		return new EllipseTreeEditPart((EllipseDrawComponent)model);

  	else if (model instanceof RectangleDrawComponent)
  		return new RectangleTreeEditPart((RectangleDrawComponent)model);

  	else if (model instanceof LineDrawComponent)
  		return new LineTreeEditPart((LineDrawComponent)model);

    else if (model instanceof TextDrawComponent)
      return new TextTreeEditPart((TextDrawComponent)model);
    
    else if (model instanceof ImageDrawComponent)
      return new ImageTreeEditPart((ImageDrawComponent)model);

    else if (model instanceof PageDrawComponent)
      return new PageTreeEditPart((PageDrawComponent)model);

    else if (model instanceof GroupDrawComponent)
      return new GroupTreeEditPart((GroupDrawComponent)model);

    else if (model instanceof ShapeDrawComponent)
      return new ShapeTreeEditPart((ShapeDrawComponent)model);
  	
    return null;
  }

}
