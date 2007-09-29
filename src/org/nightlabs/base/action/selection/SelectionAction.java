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

package org.nightlabs.base.action.selection;

import org.eclipse.jface.action.Action;
import org.nightlabs.base.notification.NotificationAdapterSWTThreadAsync;
import org.nightlabs.base.notification.SelectionManager;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

public abstract class SelectionAction 
extends Action
//implements IUpdateAction
{
	public SelectionAction() { }

	public void init(String zone, Class selectionClass, String text)
	{
		SelectionManager.sharedInstance().addNotificationListener(zone, selectionClass, selectionListener);
	}

	private NotificationListener selectionListener = new NotificationAdapterSWTThreadAsync() {
		public void notify(NotificationEvent notificationEvent) {
			setEnabled(calculateEnabled(notificationEvent));
		}
	};

	public abstract boolean calculateEnabled(NotificationEvent evt);	
		
//	/**
//	 * updates the UpdateAction when the selection changed
//	 *
//	 */
//	public void update() 
//	{
//		setEnabled(calculateEnabled());		
//	}
	
//	/**
//	 * Gets the current selection.
//	 * 
//	 * @return The current selection.
//	 */
//	protected ISelection getSelection() {
//		return selection;
//	}	
	
//	/**
//	 * Returns a <code>List</code> containing the currently
//	 * selected objects.
//	 * 
//	 * @return A List containing the currently selected objects.
//	 */
//	protected List getSelectedObjects() {
//		if (!(getSelection() instanceof IStructuredSelection))
//			return Collections.EMPTY_LIST;
//		return ((IStructuredSelection)getSelection()).toList();
//	}

}
