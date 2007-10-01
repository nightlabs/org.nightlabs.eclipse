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
package org.nightlabs.editor2d.command.shape;

import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.impl.ShapeDrawComponentImpl;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ConvertToShapeCommand 
extends Command 
{
	public ConvertToShapeCommand(ShapeDrawComponent sdc) 
	{
		super();
		setLabel(Messages.getString("org.nightlabs.editor2d.command.shape.ConvertToShapeCommand.label")); //$NON-NLS-1$
		this.sdc = sdc;
	}

	private ShapeDrawComponent sdc = null;
	private ShapeDrawComponent convertedShape = null;	
	private DrawComponentContainer parent = null;
	private int drawOrderIndex = -1;
	
	@Override
	public void execute() 
	{
		parent = sdc.getParent();
		drawOrderIndex = parent.getDrawComponents().indexOf(sdc);
		
		GeneralShape newShape = new GeneralShape(sdc.getGeneralShape());
		ShapeDrawComponent convertedShape = new ShapeDrawComponentImpl();
		convertedShape.setGeneralShape(newShape);
		convertedShape.setFill(sdc.isFill());
		convertedShape.setFillColor(sdc.getFillColor());
		convertedShape.setLineColor(sdc.getLineColor());
		convertedShape.setLineStyle(sdc.getLineStyle());
		convertedShape.setLineWidth(sdc.getLineWidth());
		convertedShape.setRotationMember(sdc.getRotation());
		convertedShape.setRenderMode(sdc.getRenderMode());
		
		parent.removeDrawComponent(sdc);
		parent.addDrawComponent(convertedShape, drawOrderIndex);
	}

	@Override
	public void redo() 
	{
		execute();
	}

	@Override
	public void undo() 
	{
		parent.removeDrawComponent(convertedShape);
		parent.addDrawComponent(sdc, drawOrderIndex);
	}
}
