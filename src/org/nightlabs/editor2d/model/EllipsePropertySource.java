/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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
import org.nightlabs.base.ui.property.IntPropertyDescriptor;
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.resource.Messages;

public class EllipsePropertySource 
extends ShapeDrawComponentPropertySource 
{
	public EllipsePropertySource(EllipseDrawComponent ellipse) {
		super(ellipse);
	}
	
	protected EllipseDrawComponent getEllipseDrawComponent() {
		return (EllipseDrawComponent) drawComponent;
	}
	
	protected List<IPropertyDescriptor> createPropertyDescriptors() 
	{
		List<IPropertyDescriptor> descriptors = super.createPropertyDescriptors();
		// Start Angle
		descriptors.add(new IntPropertyDescriptor(EllipseDrawComponent.PROP_START_ANGLE,
				Messages.getString("org.nightlabs.editor2d.model.EllipsePropertySource.startAngle"))); //$NON-NLS-1$
		// End Angle
		descriptors.add(new IntPropertyDescriptor(EllipseDrawComponent.PROP_END_ANGLE,
				Messages.getString("org.nightlabs.editor2d.model.EllipsePropertySource.endAngle"))); //$NON-NLS-1$
		
		return descriptors;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) 
	{
		super.setPropertyValue(id, value);
		
		if (id.equals(EllipseDrawComponent.PROP_START_ANGLE)) {
			getEllipseDrawComponent().setStartAngle(((Integer)value).intValue());
		}
		else if (id.equals(EllipseDrawComponent.PROP_END_ANGLE)) {
			getEllipseDrawComponent().setEndAngle(((Integer)value).intValue());
		}
	}		
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) 
	{
		Object o = super.getPropertyValue(id);
		if (o != null) 
			return o;
		else 
		{
			if (id.equals(EllipseDrawComponent.PROP_START_ANGLE)) {
				return new Integer(getEllipseDrawComponent().getStartAngle());
			}
			else if (id.equals(EllipseDrawComponent.PROP_END_ANGLE)) {
				return new Integer(getEllipseDrawComponent().getEndAngle());
			}
			
			return null;			
		}
	}		
}
