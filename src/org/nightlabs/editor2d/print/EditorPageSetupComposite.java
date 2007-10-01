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
package org.nightlabs.editor2d.print;

import java.awt.print.PageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.print.PageSetupComposite;
import org.nightlabs.base.ui.print.PrintPreviewComposite;
import org.nightlabs.editor2d.DrawComponent;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPageSetupComposite 
extends PageSetupComposite 
{
	/**
	 * @param pageFormat
	 * @param parent
	 * @param style
	 */
	public EditorPageSetupComposite(DrawComponent dc, PageFormat pageFormat, Composite parent,
			int style) 
	{
		super(pageFormat, parent, style);
		if (dc == null)
			throw new IllegalArgumentException("Param dc must not be null!"); //$NON-NLS-1$
		this.dc = dc;
		super.init(pageFormat);
	}

	/**
	 * @param pageFormat
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public EditorPageSetupComposite(DrawComponent dc, PageFormat pageFormat, Composite parent,
			int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(pageFormat, parent, style, layoutMode, layoutDataMode);
		if (dc == null)
			throw new IllegalArgumentException("Param dc must not be null!"); //$NON-NLS-1$
		
		this.dc = dc;
		super.init(pageFormat);
	}
	
	private DrawComponent dc = null;

	@Override
	protected PrintPreviewComposite initPreviewComposite(Composite parent) {
		return new EditorPrintPreviewComposite(dc, getPageFormat(), parent, SWT.NONE);
	}

	@Override
	protected void init(PageFormat pageFormat) {
	}	
}
