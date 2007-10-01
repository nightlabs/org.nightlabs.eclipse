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
import org.nightlabs.base.ui.i18n.UnitRegistryEP;
import org.nightlabs.editor2d.unit.UnitConstants;
import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.i18n.unit.UnitRegistry;

/**
 * a Composite which has a Combo for selecting {@link IUnit}s
 * 
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class UnitComposite 
extends XComposite 
{
	public UnitComposite(Composite parent, int style) {
		super(parent, style);
		this.units = new ArrayList<IUnit>(getUnitRegistry().getUnits(UnitConstants.UNIT_CONTEXT_EDITOR2D, true));
		createComposite(this);
	}
	
	public UnitComposite(Composite parent, int style, Collection<IUnit> units) {
		super(parent, style);
		this.units = new ArrayList<IUnit>(units);
		createComposite(this);
	}
	
	public UnitComposite(Composite parent, int style, LayoutMode layoutMode,
			LayoutDataMode layoutDataMode, Collection<IUnit> units) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.units = new ArrayList<IUnit>(units);
		createComposite(this);
	}

	public UnitComposite(Composite parent, int style, LayoutMode layoutMode,
			LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.units = new ArrayList<IUnit>(getUnitRegistry().getUnits(UnitConstants.UNIT_CONTEXT_EDITOR2D, true));
		createComposite(this);
	}	
	
	private List<IUnit> units = null;
	private Combo combo = null;
	public Combo getCombo() {
		return combo;
	}
	
	protected void createComposite(Composite parent) 
	{
		combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (IUnit unit : units) {
			combo.add(unit.getUnitSymbol());
		}
	}
	
	public IUnit getSelectedUnit() {
		return units.get(combo.getSelectionIndex());
	}
	
	public void selectUnit(IUnit unit) 
	{
		int index = units.indexOf(unit);
		if (index != -1)
			combo.select(index);
	}
		
	protected UnitRegistry getUnitRegistry() {
		return UnitRegistryEP.sharedInstance().getUnitRegistry();
	}	
}
