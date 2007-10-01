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
import org.nightlabs.base.ui.i18n.ResolutionUnitEP;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.i18n.unit.resolution.ResolutionUnitRegistry;

/**
 * a Composite which contains a Combo for selecting {@link IResolutionUnit}s
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ResolutionUnitComposite 
extends XComposite 
{

	public ResolutionUnitComposite(Composite parent, int style) {
		super(parent, style);
		this.units = new ArrayList<IResolutionUnit>(getResolutionUnitRegistry().getResolutionUnits());
		createComposite(this);
	}

	public ResolutionUnitComposite(Composite parent, int style, Collection<IResolutionUnit> units) {
		super(parent, style);
		this.units = new ArrayList<IResolutionUnit>(units);
		createComposite(this);		
	}
	
	public ResolutionUnitComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, Collection<IResolutionUnit> units) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.units = new ArrayList<IResolutionUnit>(units);
		createComposite(this);		
	}

	public ResolutionUnitComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.units = new ArrayList<IResolutionUnit>(getResolutionUnitRegistry().getResolutionUnits());
		createComposite(this);		
	}
	
	private List<IResolutionUnit> units = null;
	private Combo combo = null;
	public Combo getCombo() {
		return combo;
	}
	
	protected void createComposite(Composite parent) 
	{
		combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (IResolutionUnit unit : units) {
			combo.add(unit.getResolutionID());
		}
	}
	
	public IResolutionUnit getSelectedResolutionUnit() {
		return units.get(combo.getSelectionIndex());
	}
	
	public void selectResolutionUnit(IResolutionUnit unit) 
	{
		int index = units.indexOf(unit);
		if (index != -1)
			combo.select(index);
	}
	
//	protected PageRegistry getPageRegistry() 
//	{
//		return PageRegistryEP.sharedInstance().getPageRegistry();
//	}
	
	protected ResolutionUnitRegistry getResolutionUnitRegistry() 
	{
		return ResolutionUnitEP.sharedInstance().getResolutionUnitRegistry();
	}
}
