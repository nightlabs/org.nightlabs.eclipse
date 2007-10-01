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
package org.nightlabs.editor2d.handle;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Locator;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.MoveHandle;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorMoveHandle 
extends MoveHandle 
{

	/**
	 * @param owner
	 */
	public EditorMoveHandle(GraphicalEditPart owner) {
		super(owner);
	}

	/**
	 * @param owner
	 * @param loc
	 */
	public EditorMoveHandle(GraphicalEditPart owner, Locator loc) {
		super(owner, loc);
	}

	/**
	 * Initializes the handle.  Sets the {@link DragTracker} and
	 * DragCursor.
	 */
	protected void initialize() 
	{	
//		super.initialize();
//		setBorder(new LineBorder(ColorConstants.black, 1));
		
		setOpaque(false);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setCursor(Cursors.SIZEALL);		
	}	
}
