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

package org.nightlabs.base.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Simple abstract ContentProvider for Trees.
 *  
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class TreeContentProvider implements ITreeContentProvider {

	/**
	 * Default constructor.
	 */
	public TreeContentProvider() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation does nothing. 
	 * Subclassers may extend.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation does nothing and returns <code>null</code>.
	 * Subclassers may extend.
	 */
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation does nothing and returns <code>null</code>.
	 * Subclassers may extend.
	 */
	public Object getParent(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation does nothing and returns <code>false</code>.
	 * Subclassers may extend.
	 */
	public boolean hasChildren(Object element) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation does nothing.
	 * Subclassers may extend.
	 */
	public void dispose()
	{
	}
}
