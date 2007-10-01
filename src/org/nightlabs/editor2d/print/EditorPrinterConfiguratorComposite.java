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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.print.PageSetupComposite;
import org.nightlabs.base.ui.print.PrinterConfiguratorComposite;
import org.nightlabs.editor2d.DrawComponent;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPrinterConfiguratorComposite 
extends PrinterConfiguratorComposite 
{	
	/**
	 * @param parent
	 * @param style
	 */
	public EditorPrinterConfiguratorComposite(DrawComponent dc, Composite parent, int style) {
		super(parent, style);
		this.dc = dc;
		super.initGUI(this);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public EditorPrinterConfiguratorComposite(DrawComponent dc, Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.dc = dc;
		super.initGUI(this);
	}

	private DrawComponent dc = null;
	@Override
	protected PageSetupComposite initPageSetupComposite(Composite parent) 
	{
		return new EditorPageSetupComposite(dc, getPageFormat(), parent, SWT.NONE); 
	}
	
	protected void initGUI(Composite parent) {
		
	}
}
