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
import org.eclipse.swt.SWT;

/**
 * @author Christian Soltenborn - http://www.eclipsezone.com/eclipse/forums/t59401.html
 */
public abstract class AbstractInvertableTableSorter<T> extends InvertableSorter<T> {
	private final InvertableSorter inverse = new InvertableSorter<T>() {

		@Override
		public int _compare(Viewer viewer, T e1, T e2) {
			return (-1) * AbstractInvertableTableSorter.this.compare(viewer, e1, e2);
		}

		@Override
		public InvertableSorter getInverseSorter() {
			return AbstractInvertableTableSorter.this;
		}

		@Override
		public int getSortDirection() {
			return SWT.DOWN;
		}
	};

	@Override
	public InvertableSorter getInverseSorter() {
		return inverse;
	}

	@Override
	public int getSortDirection() {
		return SWT.UP;
	}
}
