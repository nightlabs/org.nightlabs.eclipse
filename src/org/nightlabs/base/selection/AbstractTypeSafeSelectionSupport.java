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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.nightlabs.base.util.SelectionUtil;

public abstract class AbstractTypeSafeSelectionSupport
extends SelectionSupport
implements ISelectionSupport
{
	public AbstractTypeSafeSelectionSupport() {
		super();
	}
	
	/**
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection arg0) {
		setSelection(SelectionUtil.checkSelection(arg0, getSelectionClass()));
	}
	
	/**
	 * 
	 * @return the Base Class which determines which objects are allowed
	 * in the selection 
	 */
	public abstract Class<? extends Object> getSelectionClass();
	
	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#addSelectedObject(java.lang.Object)
	 */
	public void addSelectedObject(Object o) 
	{
		if (getSelectionClass().isAssignableFrom(o.getClass())) {
			super.addSelectedObject(o);
		}
	}
		
	/**
	 * @see org.nightlabs.base.selection.ISelectionSupport#addSelectedObjects(java.util.Collection)
	 */
	public void addSelectedObjects(Collection objects) 
	{
		beginSelectionChange();
		try {
			for (Iterator it = objects.iterator(); it.hasNext(); ) {
				Object o = it.next();
				if (getSelectionClass().isAssignableFrom(o.getClass()))
					super.addSelectedObject(o);
			}			
		} finally {
			endSelectionChange();
		}
//		if (!objects.isEmpty()) 
//		{
//			List l = new ArrayList(objects.size());
//			for (Iterator it = objects.iterator(); it.hasNext(); ) {
//				Object o = it.next();
//				if (getSelectionClass().isAssignableFrom(o.getClass())) {
//					l.add(o);
//				}
//			}
//			List oldSelectedObjects = getSelectedObjects().toList();
//			oldSelectedObjects.addAll(l);
//			setSelection(oldSelectedObjects);
//			fireSelectionChanged();
//		}
	}

	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#setSelection(java.util.List)
	 */
	public void setSelection(List selectedObjects) 
	{
		super.setSelection(SelectionUtil.checkList(selectedObjects, getSelectionClass()));
	}
	
}
