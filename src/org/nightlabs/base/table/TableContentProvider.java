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

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * TODO: Use Generics for this table provider.
 * 
 * Adapter for ContentProviders for Tables.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class TableContentProvider implements IStructuredContentProvider {

	/**
	 * 
	 */
	public TableContentProvider() {
		super();
	}

	/**
	 * This basic implementation of IStructuredContentProvider accepts
	 * a {@link Collection} or Object[] as <code>inputElement</code> and calls
	 * and will return an array of its items. 
	 * <p>
	 * If the inputElement is neither a Collection or array, this method will
	 * simply return the inputElement within a new array.
	 * <p>
	 * If your inputElement has another type,
	 * you must override this method. 
	 *
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Collection)
			return ((Collection)inputElement).toArray();
		else if (inputElement instanceof Object[])
			return (Object[]) inputElement;
		else {			
			return new Object[] {inputElement};
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
