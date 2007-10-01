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
package org.nightlabs.editor2d.actions;

import org.eclipse.jface.action.Action;
import org.nightlabs.editor2d.properties.UnitManager;
import org.nightlabs.i18n.unit.IUnit;

/**
 * An Action which sets the corresponding {@link IUnit} as the the current Unit
 * in the {@link UnitManager}
 * 
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class UnitAction 
extends Action 
{
	public UnitAction(UnitManager unitManager, IUnit unit) 
	{
		super();
		this.unitManager = unitManager;
		this.unit = unit;
		setId(unit.getUnitID());
		setText(unit.getUnitSymbol());		
	}

	private UnitManager unitManager = null;
	private IUnit unit = null;
	
	@Override
	public void run() 
	{
		unitManager.setCurrentUnit(unit);
	}
		
}
