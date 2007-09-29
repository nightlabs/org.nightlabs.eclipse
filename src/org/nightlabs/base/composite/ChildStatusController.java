/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
/**
 * Created Mar 16, 2006, 8:25:52 PM by nick
 */

package org.nightlabs.base.composite;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 */
public class ChildStatusController
{

	private Map<Control, ChildStatus> childStatusByControl = new HashMap<Control, ChildStatus>();

	private static class ChildStatus
	{
		private boolean enabled = true;

		public boolean isEnabled()
		{
			return enabled;
		}
		public void setEnabled(boolean enabled)
		{
			this.enabled = enabled;
		}
	}

	private DisposeListener childDisposeListener = new DisposeListener() {
		public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
			childStatusByControl.remove(e.getSource());
		}
	};

	private ChildStatus getChildStatus(Control control, boolean create)
	{
		ChildStatus cs = childStatusByControl.get(control);
		if (cs == null && create) {
			cs = new ChildStatus();
			childStatusByControl.put(control, cs);
			control.addDisposeListener(childDisposeListener);
		}
		return cs;
	}

	public void setEnabled(Composite owner, boolean enabled)
	{
		Control[] children = owner.getChildren();
		for (int i = 0; i < children.length; ++i) {
			Control child = children[i];
			if (enabled) {
				ChildStatus childStatus = getChildStatus(child, false);
				if (childStatus != null) {
					child.setEnabled(childStatus.isEnabled());
				}
			} else {
				ChildStatus childStatus = getChildStatus(child, true);
				childStatus.setEnabled(child.isEnabled());
				child.setEnabled(false);
			}
		}
	}

}
