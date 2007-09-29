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

package org.nightlabs.base.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

public class SelectionUtil
{

	public SelectionUtil() {
		super();
	}
	
	/**
	 * 
	 * @param selection the ISelection to check
	 * @param clazz the Class which determines which entries are allowed
	 * @return a StructuredSelection which contains only Objects from the given
	 * selection of the given Class
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public static StructuredSelection checkSelection(ISelection selection, Class clazz) 
	{
		if (!selection.isEmpty() && selection instanceof StructuredSelection) 
		{
			List<Object> list = new ArrayList<Object>();
			boolean containsOther = false;
			StructuredSelection s = (StructuredSelection) selection;
			for (Iterator it = s.iterator(); it.hasNext(); ) 
			{
				Object o = it.next();
				if (clazz.isAssignableFrom(o.getClass())) {
					list.add(o);
				} else {
					containsOther = true;
				}
			}
			if (!containsOther)
				return s;
			else
				return new StructuredSelection(list);
		}
		return StructuredSelection.EMPTY;
	}

	/**
	 * 
	 * @param l the list to check
	 * @param clazz the Class which determines which entries are allowed
	 * @return a List which contains only Objects which are assignable from
	 * the given Class and were contained in the given list
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public static List checkList(List l, Class clazz) 
	{
		List<Object> list = new ArrayList<Object>();
		boolean containsOther = false;		
		for (Iterator it = list.iterator(); it.hasNext(); ) 
		{
			Object o = it.next();
			if (clazz.isAssignableFrom(o.getClass())) {
				list.add(o);
			} else {
				containsOther = true;
			}
		}
		if (!containsOther)
			return l;
		else
			return list;				
	}
	
}
