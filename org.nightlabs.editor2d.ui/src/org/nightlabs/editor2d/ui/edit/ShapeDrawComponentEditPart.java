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

package org.nightlabs.editor2d.ui.edit;

import java.beans.PropertyChangeEvent;

import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.editor2d.IFillable;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.ui.model.ShapeDrawComponentPropertySource;


public class ShapeDrawComponentEditPart
extends DrawComponentEditPart
{
	public ShapeDrawComponentEditPart(ShapeDrawComponent shapeDrawComponent) {
		super(shapeDrawComponent);
	}

	public ShapeDrawComponent getShapeDrawComponent() {
		return (ShapeDrawComponent) getModel();
	}

	public GeneralShape getGeneralShape() {
		return getShapeDrawComponent().getGeneralShape();
	}

	@Override
	public IPropertySource getPropertySource()
	{
		if (propertySource == null)
		{
			propertySource =
				new ShapeDrawComponentPropertySource(getShapeDrawComponent());
		}
		return propertySource;
	}

	@Override
	protected void propertyChanged(PropertyChangeEvent evt)
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(ShapeDrawComponent.PROP_FILL_COLOR)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(ShapeDrawComponent.PROP_LINE_COLOR)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(IFillable.PROP_FILL)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(ShapeDrawComponent.PROP_LINE_STYLE)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(ShapeDrawComponent.PROP_LINE_WIDTH)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(ShapeDrawComponent.PROP_GENERAL_SHAPE)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(ShapeDrawComponent.PROP_SHOW_STROKE)) {
			refreshVisuals();
			return;
		}
	}

}
