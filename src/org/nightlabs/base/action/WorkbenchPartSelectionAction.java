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
package org.nightlabs.base.action;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class WorkbenchPartSelectionAction 
extends WorkbenchPartAction
implements ISelectionAction
{
	public WorkbenchPartSelectionAction() {
		super();
	}

	/**
	 * @param activePart
	 */
	public WorkbenchPartSelectionAction(IWorkbenchPart activePart) {
		super(activePart);
	}

	/**
	 * @param text
	 */
	public WorkbenchPartSelectionAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public WorkbenchPartSelectionAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public WorkbenchPartSelectionAction(String text, int style) {
		super(text, style);
	}

	private ISelection selection = null;
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.action.ISelectionAction#getSelectedObjects()
	 */
	public List getSelectedObjects() 
	{
		if (!(getSelection() instanceof IStructuredSelection))
			return Collections.EMPTY_LIST;
		return ((IStructuredSelection)getSelection()).toList();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.action.ISelectionAction#getSelection()
	 */
	public ISelection getSelection() 
	{
		if (selection == null)
			selection = new StructuredSelection(Collections.EMPTY_LIST);
		return selection;
	}

	public void setSelection(ISelection selection) {
		this.selection = selection;
	}	
}
