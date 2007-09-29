/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.selection;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public interface ISelectionSupport
extends ISelectionProvider
{
//	/**
//	 * fires a selectionEvent to all registered SelectionListeners
//	 *
//	 */
//	void fireSelectionChanged();
	
	/**
	 * clears the selection
	 */
	void clearSelection();

	/**
	 * @return the Selection as StructuredSelection
	 * @deprecated Use {@link #getStructuredSelection()} instead!
	 */
	StructuredSelection getSelectedObjects();

	IStructuredSelection getStructuredSelection();

	/**
	 * @param selectedObjects A List of Objects which are selected
	 */
	void setSelection(Collection selectedObjects);

	/**
	 * @param o the Object to add to the selection
	 */
	void addSelectedObject(Object o);	
	
	/**
	 * @param objects a Collection of Objects to add to the selection
	 */
	void addSelectedObjects(Collection objects);
	
	/**
	 * @param o the Object to remove from the selection
	 */
	void removeSelectedObject(Object o);
	
	/**
	 * @param objects a Collection of Objects to remove from the selection
	 */
	void removeSelectedObjects(Collection objects);
	
	/**
	 * @param o element whose presence in this collection is to be tested
	 * @return true if the Object is contained, false if not
	 */
	boolean contains(Object o);

	/**
	 * Normally, all methods that modify the selection (e.g. {@link #addSelectedObject(Object)}
	 * or {@link #removeSelectedObject(Object)}) cause a {@link SelectionChangedEvent} to be fired.
	 * If you have to do many changes, you may want to defer this event. Once you called this method,
	 * no event will be fired, until you call the {@link #endSelectionChange()}.
	 * <p>
	 * The methods <code>beginSelectionChange()</code> and {@link #endSelectionChange()} are reentrant
	 * and track by means of a counter, when to fire the event. That means, you <b>must</b> use a try...finally
	 * block and call {@link #endSelectionChange()} within the finally block.
	 * </p>
	 * <p>
	 * If you call {@link #endSelectionChange()} without performing any modifications, no event will be triggered.
	 * </p>
	 */
	void beginSelectionChange();
	/**
	 * If, after a previous call to {@link #beginSelectionChange()}, the selection has been modified, this
	 * method will cause a {@link SelectionChangedEvent} to be fired.
	 *
	 * @see #beginSelectionChange()
	 */
	void endSelectionChange();
}
