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
package org.nightlabs.editor2d.viewer.ui.descriptor;

import java.util.HashMap;
import java.util.Map;

import org.nightlabs.editor2d.DrawComponent;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class DescriptorManager
//implements IDrawComponentDescriptor
{

	public DescriptorManager()
	{
		super();
		addDescriptor(defaultDescriptor, DrawComponent.class);
	}

	protected IDrawComponentDescriptor defaultDescriptor = new DrawComponentDescriptor(null);
	protected IDrawComponentDescriptor currentDescriptor = defaultDescriptor;
		
	protected Map<Class, IDrawComponentDescriptor> class2Descriptor = new HashMap<Class, IDrawComponentDescriptor>();
	public void addDescriptor(IDrawComponentDescriptor desc, Class dcClass) {
		class2Descriptor.put(dcClass, desc);
	}
		
	protected DrawComponent drawComponent = null;
	public void setDrawComponent(DrawComponent dc)
	{
		this.drawComponent = dc;
		if (class2Descriptor.keySet().contains(dc.getClass())) {
			IDrawComponentDescriptor desc = class2Descriptor.get(dc.getClass());
			currentDescriptor = desc;
		}
		else {
			currentDescriptor = defaultDescriptor;
		}
		currentDescriptor.setDrawComponent(dc);
	}
	public DrawComponent getDrawComponent() {
		return drawComponent;
	}
	
	public String getEntriesAsString(boolean lineWrap) {
		return currentDescriptor.getEntriesAsString(lineWrap);
	}
	
	public Map<String, String> getProperties() {
		return currentDescriptor.getProperties();
	}
	  
}
