/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities														*
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org										*
 *																																						 *
 * This library is free software; you can redistribute it and/or							 *
 * modify it under the terms of the GNU Lesser General Public									*
 * License as published by the Free Software Foundation; either								*
 * version 2.1 of the License, or (at your option) any later version.					*
 *																																						 *
 * This library is distributed in the hope that it will be useful,						 *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of							*
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU					 *
 * Lesser General Public License for more details.														 *
 *																																						 *
 * You should have received a copy of the GNU Lesser General Public						*
 * License along with this library; if not, write to the											 *
 *		 Free Software Foundation, Inc.,																				 *
 *		 51 Franklin St, Fifth Floor,																						*
 *		 Boston, MA	02110-1301	USA																						 *
 *																																						 *
 * Or get it online :																													*
 *		 http://www.gnu.org/copyleft/lesser.html																 *
 *																																						 *
 *																																						 *
 ******************************************************************************/

package org.nightlabs.base.layout;

import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

/**
 * An instance of this class can be used for a {@link Tree} (can be wrapped by {@link TreeViewer}) or
 * for a {@link Table} (can be wrapped by {@link TableViewer}). It will set the widths of the columns
 * either to a fixed value or to a width calculated from a proportional factor (weight).
 *
 * @author Nicklas Shiffler
 * @author marco schulze - marco at nightlabs dot de
 */
public class WeightedTableLayout
extends TableLayout 
{
	private int[] weights;
	private int[] fixedWidths;

	/**
	 * This constructor calls {@link #WeightedTableLayout(int[], int[]) } with <code>fixedWidths == null</code>
	 */
	public WeightedTableLayout(int[] weights) 
	{
		this(weights, null);
	}

	/**
	 * @param weights Each weight defines the proportional width of the column (by zero-based column index). If
	 *		the column is not defined (i.e. the <code>weights</code> array is too short), a weight of 0 is assumed.
	 *		If one column has a fixed width, its weight must be -1 (which is an illegal argument for a variable-width column).
	 * @param fixedWidths Each column that is represented with a fixedWidth >= 0 is excluded from variable-width-calculation
	 *		and always has the same width (useful for image-only columns).
	 */
	public WeightedTableLayout(int[] weights, int[] fixedWidths) 
	{
		if (weights != null) 
			this.weights = weights;
		else
			this.weights = new int[0];

		if (fixedWidths != null)
			this.fixedWidths = fixedWidths;
		else
			this.fixedWidths = new int[0];

		// check the widths for consistence
		for (int i = 0; i < this.weights.length; i++) {
			int weight = this.weights[i];
			if (weight < 0) {
				if (getFixedWidth(i) < 0)
					throw new IllegalArgumentException("weight and fixedWidth for columnIndex="+i+" are both < 0!!!"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		for (int i = this.weights.length; i < this.fixedWidths.length; i++) {
			int fixedWidth = this.fixedWidths[i];
			if (fixedWidth < 0 && this.weights.length > i) {
				if (this.weights[i] < 0)
					throw new IllegalArgumentException("weight and fixedWidth for columnIndex="+i+" are both < 0!!!"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	protected boolean isFixedWidth(int columnIndex)
	{
		return getFixedWidth(columnIndex) >= 0;
	}

	protected int getFixedWidth(int columnIndex)
	{
		if (columnIndex >= fixedWidths.length)
			return -1;

		return fixedWidths[columnIndex];
	}

	protected int getWeight(int columnIndex)
	{
		if (columnIndex >= weights.length) {
			if (!isFixedWidth(columnIndex))
				return 0;

			return -1;
		}

		return weights[columnIndex];
	}

//	private boolean firstTime = true;	
	public void layout(Composite c, boolean flush) 
	{
//		if (!firstTime) {
//			return;
//		}
		
		int columnCount;
		if (c instanceof Table)
			columnCount = ((Table)c).getColumnCount();
		else if (c instanceof Tree)
			columnCount = ((Tree)c).getColumnCount();
		else
			throw new IllegalArgumentException("Composite c is neither a " + Table.class.getName() + " nor a " + Tree.class.getName()); //$NON-NLS-1$ //$NON-NLS-2$

		int width = c.getBounds().width;
		ScrollBar sb = c.getVerticalBar();
		if(sb.isEnabled() && sb.isVisible()) 
			width -= sb.getSize().x;
//			width += sb.getSize().x;
		
		int totalWeight = 0;
		int totalFixedWidth = 0;
		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
			if (isFixedWidth(columnIndex))
				totalFixedWidth += getFixedWidth(columnIndex);
			else
				totalWeight += getWeight(columnIndex);
		}
		if (totalWeight == 0)
			totalWeight = 1; // prevent division by 0

		int totalDynamicWidth = width - totalFixedWidth;
		if (totalDynamicWidth < 16)
			totalDynamicWidth = 16;

		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
			int columnWidth;
			if (isFixedWidth(columnIndex))
				columnWidth = getFixedWidth(columnIndex);
			else
				columnWidth = totalDynamicWidth * getWeight(columnIndex) / totalWeight;

			if (c instanceof Table)
				((Table)c).getColumn(columnIndex).setWidth(columnWidth);
			else if (c instanceof Tree)
				((Tree)c).getColumn(columnIndex).setWidth(columnWidth);
			else
				throw new IllegalArgumentException("Composite c is neither a " + Table.class.getName() + " nor a " + Tree.class.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
//		firstTime = false;
	}
}

