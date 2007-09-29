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

package org.nightlabs.base.composite;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite for wrapping other composites and no insets.
 * 
 * @see org.nightlabs.base.composite.OrdinaryWrapperComposite
 * @see TightWrapperComposite#TightWrapperComposite(Composite, int, boolean)
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 * @deprecated Use {@link org.nightlabs.base.composite.XComposite} directly!
 */
public class TightWrapperComposite extends XComposite
{
	/**
	 * This constructor calls {@link #TightWrapperComposite(Composite, int, boolean)}
	 * with <code>setLayoutData = true</code>.
	 */
	public TightWrapperComposite(Composite parent, int style) {
		this(parent, style, true);
	}

	/**
	 * Creates a new Composite with a {@link GridLayout} which wastes a minimum of space.
	 * If <code>setLayoutData == true</code>, the newly created Composite
	 * will fit itself into another {@link GridLayout} by setting its layoutData.
	 *
	 * @param parent The Composite into which this newly created one will be embedded as child.
	 * @param style A combination of the SWT style flags.
	 * @param setLayoutData If <code>true</code>, a {@link GridData} will be created and assigned. 
	 */
	public TightWrapperComposite(Composite parent, int style, boolean setLayoutData) {
		super(
				parent, style,
				LayoutMode.TIGHT_WRAPPER, 
				setLayoutData ? LayoutDataMode.GRID_DATA : LayoutDataMode.NONE);	}

}
