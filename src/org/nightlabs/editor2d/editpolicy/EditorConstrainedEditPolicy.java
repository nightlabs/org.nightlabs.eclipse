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
package org.nightlabs.editor2d.editpolicy;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.nightlabs.editor2d.request.EditorRequestConstants;
import org.nightlabs.editor2d.util.EditorUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorConstrainedEditPolicy 
extends GraphicalEditPolicy
implements EditorRequestConstants
{

	public EditorConstrainedEditPolicy() {
	}

  public Point getConstraintFor(Point point){
    return EditorUtil.toAbsolute((GraphicalEditPart)getHost(), point);
  } 

  protected Point getScrollOffset() {
    return EditorUtil.getScrollOffset(getHost());
  }
  
  public Rectangle getConstraintFor(Rectangle rectangle) {
    return EditorUtil.oldToAbsolute((GraphicalEditPart)getHost(), rectangle);
  }

//	@Override
//	public EditPart getTargetEditPart(Request request) {
//		return super.getTargetEditPart(request);		
//	}  
    
}
