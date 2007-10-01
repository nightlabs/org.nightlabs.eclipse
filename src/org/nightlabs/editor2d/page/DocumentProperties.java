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

import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.print.page.IPredefinedPage;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class DocumentProperties 
{
	private IPredefinedPage predefinedPage;
	private int orientation;
	private IResolutionUnit resolutionUnit;
	private double resolution;
//	private IUnit unit;
	
	public DocumentProperties(IPredefinedPage predefinedPage, int orientation, 
			IResolutionUnit resolutionUnit, double resolution) 
	{
		super();
		this.predefinedPage = predefinedPage;
		this.orientation = orientation;
		this.resolutionUnit = resolutionUnit;
		this.resolution = resolution;
//		this.unit = unit;
	}
	
	/**
	 * @return Returns the orientation.
	 */
	public int getOrientation() {
		return orientation;
	}
	/**
	 * @param orientation The orientation to set.
	 */
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	/**
	 * @return Returns the predefinedPage.
	 */
	public IPredefinedPage getPredefinedPage() {
		return predefinedPage;
	}
	/**
	 * @param predefinedPage The predefinedPage to set.
	 */
	public void setPredefinedPage(IPredefinedPage predefinedPage) {
		this.predefinedPage = predefinedPage;
	}
	/**
	 * @return Returns the resolution.
	 */
	public double getResolution() {
		return resolution;
	}
	/**
	 * @param resolution The resolution to set.
	 */
	public void setResolution(double resolution) {
		this.resolution = resolution;
	}
	/**
	 * @return Returns the resolutionUnit.
	 */
	public IResolutionUnit getResolutionUnit() {
		return resolutionUnit;
	}
	/**
	 * @param resolutionUnit The resolutionUnit to set.
	 */
	public void setResolutionUnit(IResolutionUnit resolutionUnit) {
		this.resolutionUnit = resolutionUnit;
	}
//	/**
//	 * @return Returns the unit.
//	 */
//	public IUnit getUnit() {
//		return unit;
//	}
//	/**
//	 * @param unit The unit to set.
//	 */
//	public void setUnit(IUnit unit) {
//		this.unit = unit;
//	}
	
}
