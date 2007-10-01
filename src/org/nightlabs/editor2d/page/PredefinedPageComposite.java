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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.print.page.PredefinedPageEP;
import org.nightlabs.print.page.IPredefinedPage;
import org.nightlabs.print.page.PredefinedPageRegistry;

/**
 * a Composite with a combo for selecting {@link IPredefinedPage}s
 * 
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PredefinedPageComposite 
extends XComposite 
{
//	protected PageRegistry getPageRegistry() {
//		return PageRegistryEP.sharedInstance().getPageRegistry();
//	}

	protected PredefinedPageRegistry getPageRegistry() {
		return PredefinedPageEP.sharedInstance().getPageRegistry();
	}

	public PredefinedPageComposite(Composite parent, int style) {
		super(parent, style);
		this.pages = new ArrayList<IPredefinedPage>(getPageRegistry().getPages());
		createComposite(this);
	}	
	
	public PredefinedPageComposite(Composite parent, int style, Collection<IPredefinedPage> pages) {
		super(parent, style);
		this.pages = new ArrayList<IPredefinedPage>(pages);
		createComposite(this);		
	}

	public PredefinedPageComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, Collection<IPredefinedPage> pages) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.pages = new ArrayList<IPredefinedPage>(pages);
		createComposite(this);				
	}

	public PredefinedPageComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.pages = new ArrayList<IPredefinedPage>(getPageRegistry().getPages());
		createComposite(this);				
	}	
	
	private List<IPredefinedPage> pages = null;
	private Combo combo = null;
	public Combo getCombo() {
		return combo;
	}
	
	protected void createComposite(Composite parent) 
	{
		combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (IPredefinedPage page : pages) {
			combo.add(page.getPageID());
		}		
	}
	
	public IPredefinedPage getSelectedPage() {
		return pages.get(combo.getSelectionIndex());
	}
	
	public void selectPage(IPredefinedPage page) {
		int index = pages.indexOf(page);
		if (index != -1)
			combo.select(index);
	}
}
