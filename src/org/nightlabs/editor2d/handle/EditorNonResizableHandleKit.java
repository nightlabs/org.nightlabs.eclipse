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

import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.gef.handles.NonResizableHandleKit;
import org.eclipse.gef.handles.ResizeHandle;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.swt.graphics.Cursor;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorNonResizableHandleKit 
extends NonResizableHandleKit 
{

	public EditorNonResizableHandleKit() {
		super();
	}

	/**
	 * Fills the given List with handles at each corner of a
	 * figure.
	 * @param part the handles' GraphicalEditPart
	 * @param handles the List to add the four corner handles to
	 * @param tracker the handles' DragTracker
	 * @param cursor the handles' Cursor
	 */
	public static void addCornerHandles(GraphicalEditPart part, List handles, 
										DragTracker tracker, Cursor cursor) {
		handles.add(createHandle(part, PositionConstants.SOUTH_EAST, tracker, cursor));
		handles.add(createHandle(part, PositionConstants.SOUTH_WEST, tracker, cursor));
		handles.add(createHandle(part, PositionConstants.NORTH_WEST, tracker, cursor));
		handles.add(createHandle(part, PositionConstants.NORTH_EAST, tracker, cursor));
	}

	/**
	 * Fills the given List with handles at each corner of a
	 * figure.
	 * @param part the handles' GraphicalEditPart
	 * @param handles the List to add the four corner handles to
	 */
	public static void addCornerHandles(GraphicalEditPart part, List handles) {
		handles.add(createHandle(part, PositionConstants.SOUTH_EAST));
		handles.add(createHandle(part, PositionConstants.SOUTH_WEST));
		handles.add(createHandle(part, PositionConstants.NORTH_WEST));
		handles.add(createHandle(part, PositionConstants.NORTH_EAST));
	}

	/**
	 * Adds a single handle in the given direction to the given List.
	 * 
	 * @param part the owner GraphicalEditPart of the handle
	 * @param handles the List to add the handle to
	 * @param direction the integer constant from PositionConstants that refers to the handle
	 * direction
	 */
	public static void addHandle(GraphicalEditPart part, List handles, int direction) {
		handles.add(createHandle(part, direction));	
	}

	/**
	 * Adds a single handle in the given direction to the given List.
	 * 
	 * @param tracker the DragTracker to assign to this handle
	 * @param part the owner GraphicalEditPart of the handle
	 * @param handles the List to add the handle to
	 * @param direction the integer constant from PositionConstants that refers to the handle
	 * direction
	 * @param cursor the Cursor to use when hovering over this handle
	 */
	public static void addHandle(GraphicalEditPart part, List handles, int direction,
								 DragTracker tracker, Cursor cursor) {
		handles.add(createHandle(part, direction, tracker, cursor));	
	}

	/**
	 * Fills the given List with handles at each corner.
	 * @param part the handles' GraphicalEditPart
	 * @param handles the List to add the handles to
	 */
	public static void addHandles(GraphicalEditPart part, List handles) {
		addMoveHandle(part, handles);
		addCornerHandles(part, handles);
	}

	/**
	 * Fills the given List with handles at each corner.
	 * @param part the handles' GraphicalEditPart
	 * @param handles the List to add the handles to
	 * @param tracker the handles' DragTracker
	 * @param cursor the handles' Cursor
	 */
	public static void addHandles(GraphicalEditPart part, List handles, DragTracker tracker,
								  Cursor cursor) {
		addMoveHandle(part, handles, tracker, cursor);
		addCornerHandles(part, handles, tracker, cursor);
	}

	/**
	 * Fills the given List with move borders at each side of a
	 * figure.
	 * @param f the handles' GraphicalEditPart
	 * @param handles the List to add the handles to
	 */
	public static void addMoveHandle(GraphicalEditPart f, List handles) {
		handles.add(moveHandle(f));
	}

	/**
	 * Fills the given List with move borders at each side of a
	 * figure.
	 * 
	 * @param tracker the DragTracker to assign to this handle
	 * @param f the handles' GraphicalEditPart
	 * @param handles the List to add the handles to
	 * @param cursor the Cursor to use when hovering over this handle
	 */
	public static void addMoveHandle(GraphicalEditPart f, List handles, DragTracker tracker, 
									 Cursor cursor) {
		handles.add(moveHandle(f, tracker, cursor));
	}

	static Handle createHandle(GraphicalEditPart owner, int direction) {
		ResizeHandle handle = new ResizeHandle(owner, direction);
		handle.setCursor(SharedCursors.SIZEALL);
		handle.setDragTracker(new DragEditPartsTracker(owner));
		return handle;
	}

	static Handle createHandle(GraphicalEditPart owner, int direction, DragTracker tracker,
							   Cursor cursor) {
		ResizeHandle handle = new ResizeHandle(owner, direction);
		handle.setCursor(cursor);
		handle.setDragTracker(tracker);
		return handle;
	}

	/**
	 * Returns a new {@link MoveHandle} with the given owner.
	 * @param owner the GraphicalEditPart that is the owner of the new MoveHandle 
	 * @return the new MoveHandle
	 */
	public static Handle moveHandle(GraphicalEditPart owner) {
		return new EditorMoveHandle(owner);
	}

	/**
	 * Returns a new {@link MoveHandle} with the given owner.
	 * 
	 * @param tracker the DragTracker to assign to this handle
	 * @param owner the GraphicalEditPart that is the owner of the new MoveHandle 
	 * @param cursor the Cursor to use when hovering over this handle
	 * @return the new MoveHandle
	 */
	public static Handle moveHandle(GraphicalEditPart owner, DragTracker tracker, 
									Cursor cursor) {
		MoveHandle moveHandle = new EditorMoveHandle(owner);
		moveHandle.setDragTracker(tracker);
		moveHandle.setCursor(cursor);
		return moveHandle;
	}

}
