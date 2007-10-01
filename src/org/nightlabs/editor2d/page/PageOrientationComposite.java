/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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
package org.nightlabs.editor2d.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.model.PagePropertySource;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PageOrientationComposite 
extends XComposite 
{

	public PageOrientationComposite(Composite parent, int style) 
	{
		super(parent, style);
		createContents(this);
	}

	public PageOrientationComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		createContents(this);		
	}

	private Combo combo = null;
	protected void createContents(Composite parent) 
	{
		combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.add(PagePropertySource.getOrientationString(PageDrawComponent.ORIENTATION_VERTICAL), 0);
		combo.add(PagePropertySource.getOrientationString(PageDrawComponent.ORIENTATION_HORIZONTAL), 1);		
	}
	
	public int getOrientation() 
	{
		if (combo.getItem(combo.getSelectionIndex()).equals(
				PagePropertySource.getOrientationString(PageDrawComponent.ORIENTATION_VERTICAL)))
		{
			return PageDrawComponent.ORIENTATION_VERTICAL;
		}
		else {
			return PageDrawComponent.ORIENTATION_HORIZONTAL;
		}
	}
	
	public void selectOrientation(int orientation) 
	{
		if (orientation == PageDrawComponent.ORIENTATION_VERTICAL)
			combo.select(0);
		else if (orientation == PageDrawComponent.ORIENTATION_HORIZONTAL)
			combo.select(1);
	}
	
}
