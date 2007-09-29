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

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class GenericInvertViewerSorter 
extends InvertableSorter<Object>
{	
	public GenericInvertViewerSorter(int columnIndex) {
		super();
		this.columnIndex = columnIndex;
	}

	private int columnIndex = 0;

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
//		return superCompare(viewer, e1, e2);		
		return compare(viewer, e1, e2,columnIndex);
	}

	@Override
	public int _compare(Viewer viewer, Object e1, Object e2) {
		return 0;
	}

	@Override
	public int getSortDirection() {
		return SWT.UP;
	}

	@Override
	public InvertableSorter getInverseSorter() {
		return inverse;
	}

	private InvertableSorter inverse = new InvertableSorter<Object>(){	
		@Override
		public int getSortDirection() {
			return SWT.DOWN;
		}

		@Override
		public InvertableSorter getInverseSorter() {
			return GenericInvertViewerSorter.this;
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
//			return (-1) * superCompare(viewer, e1, e2);
			return (-1) * GenericInvertViewerSorter.this.compare(viewer, e1, e2, columnIndex);
		}

		@Override
		protected int _compare(Viewer viewer, Object e1, Object e2) {
			return 0;
		}	
	};

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public int compare(Viewer viewer, Object e1, Object e2, int columnIndex) 
	{
		int cat1 = category(e1);
		int cat2 = category(e2);

		if (cat1 != cat2) {
			return cat1 - cat2;
		}

		String name1;
		String name2;

		if (viewer == null || !(viewer instanceof ContentViewer)) {
			name1 = e1.toString();
			name2 = e2.toString();
		} else {
			IBaseLabelProvider prov = ((ContentViewer) viewer)
			.getLabelProvider();
			if (prov instanceof ITableLabelProvider) {
				ITableLabelProvider lprov = (ITableLabelProvider) prov;
				name1 = lprov.getColumnText(e1, columnIndex);
				name2 = lprov.getColumnText(e2, columnIndex);
			}         
			else if (prov instanceof ILabelProvider) {
				ILabelProvider lprov = (ILabelProvider) prov;
				name1 = lprov.getText(e1);
				name2 = lprov.getText(e2);
			} 
			else {
				name1 = e1.toString();
				name2 = e2.toString();
			}
		}
		if (name1 == null) {
			name1 = "";//$NON-NLS-1$
		}
		if (name2 == null) {
			name2 = "";//$NON-NLS-1$
		}

		// use the comparator to compare the strings
		return getComparator().compare(name1, name2);		
	}

}
