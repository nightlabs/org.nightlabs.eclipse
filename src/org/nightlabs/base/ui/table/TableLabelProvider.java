/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.ui.table;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Adapter for LabelProviders for Tables.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class TableLabelProvider
extends LabelProvider
implements ITableLabelProvider, IColumnComparableProvider
{

	@Override
	public String getText(Object element) {
		return getColumnText(element, 0);
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation uses the {@link #getColumnText(Object, int)} method
	 * of the label-provider to get the column text as Comparable.
	 * </p>
	 * <p>
	 * Subclasses may override this method to provide Comparable objects for those
	 * columns where sorting the String representation is not equivalent to sorting 
	 * the actual column object (like for date columns).
	 * </p>
	 */
	@Override
	public Comparable<?> getColumnComparable(Object element, int columnIndex) {
		return getColumnText(element, columnIndex);
	}

}
