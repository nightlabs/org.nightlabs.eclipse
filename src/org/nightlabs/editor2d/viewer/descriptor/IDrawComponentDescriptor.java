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
package org.nightlabs.editor2d.viewer.descriptor;

import java.util.Map;

import org.nightlabs.editor2d.DrawComponent;

/**
 * Describes an DrawComponent by returning Properties as Map for a DrawComponent
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface IDrawComponentDescriptor 
{
	/**
	 * sets the DrawComponent to get descriptions from
	 * @param dc the DrawComponent to set
	 */
	void setDrawComponent(DrawComponent dc);
	
	/**
	 * returns the described drawComponent
	 * @return the DrawComponent to get descriptions from 
	 */
	DrawComponent getDrawComponent();
	
	/**
	 * returns a single String which contains all properties (key+value) separated by commas (,)
	 * in the same order as they have been added
	 *   
	 * @param linewrap determines if the entries should be returned with a linewrap or not
	 * @see IDrawComponentDescriptor#getProperties()
	 * @see IDrawComponentDescriptor#addEntry(String, String)
	 * @return a single String contained all properties 
	 */
	String getEntriesAsString(boolean linewrap);
	
	/**
	 * key: property name
	 * value: property value
	 * 
	 * @see IDrawComponentDescriptor#addEntry(String, String)
	 * @return a Map containing all properties for the DrawComponent 
	 */
	Map<String, String> getProperties();
	
	/**
	 * adds a Property Entry to the Descriptor, every time the drawComponent
	 * is newly set, all values must be adapted
	 *   
	 * @param name the property name
	 * @param value the property value 
	 */
	void addEntry(String name, String value);
}
