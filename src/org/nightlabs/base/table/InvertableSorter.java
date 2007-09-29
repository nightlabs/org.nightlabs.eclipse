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

package org.nightlabs.base.table;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author Christian Soltenborn - http://www.eclipsezone.com/eclipse/forums/t59401.html
 */
public abstract class InvertableSorter<T>
extends ViewerSorter
{
	@Override
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public int compare(Viewer viewer, Object e1, Object e2)
	{
		return _compare(viewer, (T)e1, (T)e2);
	}

	protected int superCompare(Viewer viewer, Object e1, Object e2) 
	{
		return super.compare(viewer, e1, e2);
	}
	
	protected abstract int _compare(Viewer viewer, T e1, T e2);

	public abstract InvertableSorter getInverseSorter();

	public abstract int getSortDirection();
}
