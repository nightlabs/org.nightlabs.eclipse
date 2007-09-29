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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.base.table.InvertableSorter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class TreeSortSelectionListener 
implements SelectionListener 
{
	private final TreeViewer viewer;
	private final TreeColumn column;
	private final InvertableSorter sorter;
	private final boolean keepDirection;
	private InvertableSorter currentSorter;
	
	/**
	 * The constructor of this listener.
	 *
	 * @param viewer
	 *						the treeviewer this listener belongs to
	 * @param column
	 *						the column this listener is responsible for
	 * @param sorter
	 *						the sorter this listener uses
	 * @param defaultDirection
	 *						the default sorting direction of this Listener. Possible
	 *						values are {@link SWT#UP} and {@link SWT#DOWN}
	 */
	public TreeSortSelectionListener(TreeViewer viewer, TreeColumn column,
			InvertableSorter sorter, int defaultDirection) {
		this(viewer, column, sorter, defaultDirection, false);
	}	
	
	/**
	 * The constructor of this listener.
	 *
	 * @param viewer
	 *						the treeviewer this listener belongs to
	 * @param column
	 *						the column this listener is responsible for
	 * @param sorter
	 *						the sorter this listener uses
	 * @param defaultDirection
	 *						the default sorting direction of this Listener. Possible
	 *						values are {@link SWT#UP} and {@link SWT#DOWN}
	 * @param keepDirection
	 *						if true, the listener will remember the last sorting direction
	 *						of the associated column and restore it when the column is
	 *						reselected. If false, the listener will use the default soting
	 *						direction
	 *
	 * @deprecated This constructor is deprecated, because the behaviour "keepDirection"
	 *		should be global (=> configuration) and not differ from view to view. (Marco)
	 */	
	public TreeSortSelectionListener(TreeViewer viewer, TreeColumn column,
			InvertableSorter sorter, int defaultDirection,
			boolean keepDirection) {
		this.viewer = viewer;
		this.column = column;
		this.keepDirection = keepDirection;
		this.sorter = (defaultDirection == SWT.UP) ?
			sorter : sorter.getInverseSorter();
		this.currentSorter = this.sorter;

		this.column.addSelectionListener(this);
	}	
	
	/**
	 * Chooses the colum of this listener for sorting of the table. Mainly used
	 * when first initialising the table.
	 */
	public void chooseColumnForSorting() 
	{
		viewer.getTree().setSortColumn(column);
		viewer.getTree().setSortDirection(currentSorter.getSortDirection());
		viewer.setSorter(currentSorter);
		
		System.out.println("sort Column = "+column.getText()); //$NON-NLS-1$
	}	
	
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		InvertableSorter newSorter;
		if (viewer.getTree().getSortColumn() == column) {
			newSorter = ((InvertableSorter) viewer.getSorter()).getInverseSorter();
		} else {
			if (keepDirection) {
				newSorter = currentSorter;
			} else {
				newSorter = sorter;
			}
		}
		currentSorter = newSorter;
		chooseColumnForSorting();
	}
	
}
