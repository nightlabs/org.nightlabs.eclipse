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
package org.nightlabs.editor2d.ui.model;

import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.property.CheckboxPropertyDescriptor;
import org.nightlabs.editor2d.IConnectable;
import org.nightlabs.editor2d.LineDrawComponent;

/**
 * Author: Daniel Mazurek 
 */
public class LinePropertySource 
extends ShapeDrawComponentPropertySource 
{
	/**
	 * @param element
	 */
	public LinePropertySource(LineDrawComponent element) {
		super(element);
	}

	protected LineDrawComponent getLineDrawComponent() {
		return (LineDrawComponent) drawComponent;
	}
	
	@Override
	public Object getPropertyValue(Object id) 
	{
		if (IConnectable.PROP_CONNECT.equals(id)) {
			return getLineDrawComponent().isConnect();
		}
		return super.getPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (IConnectable.PROP_CONNECT.equals(id)) {
			getLineDrawComponent().setConnect((Boolean)value);
			return;
		}
		super.setPropertyValue(id, value);
	}

	@Override
	protected List<IPropertyDescriptor> createPropertyDescriptors() {
		List<IPropertyDescriptor> descriptors = super.createPropertyDescriptors();
		descriptors.add(createConnectPD());
		return descriptors;
	}

	protected IPropertyDescriptor createConnectPD() {
		PropertyDescriptor desc = new CheckboxPropertyDescriptor(IConnectable.PROP_CONNECT,
				"Connect");
		return desc;
	}
}
