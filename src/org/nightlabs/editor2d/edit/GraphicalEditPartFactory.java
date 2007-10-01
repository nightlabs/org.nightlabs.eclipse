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

package org.nightlabs.editor2d.edit;

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

public class GraphicalEditPartFactory 
implements EditPartFactory
{

	public GraphicalEditPartFactory()
	{
		super();
	}

	/**
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model)
	{
    if(model instanceof RootDrawComponent)
      return new RootDrawComponentEditPart((RootDrawComponent)model);
    
    else if (model instanceof Layer)
      return new LayerEditPart((Layer)model);
        
    else if (model instanceof EllipseDrawComponent)
    	return new EllipseEditPart((EllipseDrawComponent)model);

    else if (model instanceof RectangleDrawComponent)
    	return new RectangleEditPart((RectangleDrawComponent)model);

    else if (model instanceof LineDrawComponent)
      return new LineEditPart((LineDrawComponent)model);

    else if (model instanceof TextDrawComponent)
      return new TextEditPart((TextDrawComponent)model);

    else if (model instanceof ImageDrawComponent)
      return new ImageEditPart((ImageDrawComponent)model);

    else if (model instanceof PageDrawComponent)
      return new PageEditPart((PageDrawComponent)model);

    else if (model instanceof GroupDrawComponent)
      return new GroupEditPart((GroupDrawComponent)model);

    else if (model instanceof ShapeDrawComponent)
      return new ShapeDrawComponentEditPart((ShapeDrawComponent)model);
    
    return null;
	}

}
