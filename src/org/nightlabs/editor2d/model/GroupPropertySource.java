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
package org.nightlabs.editor2d.model;

import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.property.IntPropertyDescriptor;
import org.nightlabs.editor2d.GroupDrawComponent;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class GroupPropertySource 
extends DrawComponentPropertySource 
{
	/**
	 * @param element
	 */
	public GroupPropertySource(GroupDrawComponent element) {
		super(element);
	}

	public GroupDrawComponent getGroupDrawComponent() {
		return (GroupDrawComponent) drawComponent;
	}
	
	protected List<IPropertyDescriptor> createPropertyDescriptors() 
	{
		super.createPropertyDescriptors();
		
		List<IPropertyDescriptor> descriptors = getDescriptors();
		// Group Amount
		descriptors.add(createGroupAmountPropertyDescriptor());
		
		return descriptors;
	}	
	
	private static final String GROUP_AMOUNT_ID = "groupAmount"; //$NON-NLS-1$
	protected PropertyDescriptor createGroupAmountPropertyDescriptor() 
	{
		return new IntPropertyDescriptor(GROUP_AMOUNT_ID, 
				Messages.getString("org.nightlabs.editor2d.model.GroupPropertySource.label.groupedElements"), true); //$NON-NLS-1$
	}

	@Override
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(GROUP_AMOUNT_ID))
			return getGroupDrawComponent().getDrawComponents().size();
		
		return super.getPropertyValue(id);
	}
}
