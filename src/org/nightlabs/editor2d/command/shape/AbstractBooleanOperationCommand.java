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

import java.awt.geom.Area;

import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.impl.ShapeDrawComponentImpl;
import org.nightlabs.editor2d.j2d.GeneralShape;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class AbstractBooleanOperationCommand 
extends Command 
{

	public AbstractBooleanOperationCommand(ShapeDrawComponent primary, ShapeDrawComponent secondary) 
	{
		super();
		this.primary = primary;
		this.secondary = secondary;
	}

	protected ShapeDrawComponent primary = null;	
	protected ShapeDrawComponent secondary = null;
	protected ShapeDrawComponent booleanOperation = null;	
	
	protected int drawOrderIndex1 = -1;
	protected int drawOrderIndex2 = -1;
	
	protected DrawComponentContainer parent1 = null;
	protected DrawComponentContainer parent2 = null;	
	
	protected Area area1 = null;
	protected Area area2 = null;
	
	protected void copyProperties(ShapeDrawComponent src, ShapeDrawComponent target) 
	{
		target.setParent(src.getParent());		
		target.setFill(src.isFill());
		target.setFillColor(src.getFillColor());
//		target.setLanguageId(src.getLanguageId());
		target.setLineColor(src.getLineColor());
		target.setLineStyle(src.getLineStyle());
		target.setLineWidth(src.getLineWidth());
		target.setRenderMode(src.getRenderMode());
		target.setRotationMember(src.getRotation());
	}
		
	@Override
	public void execute() 
	{
		parent1 = primary.getParent();
		drawOrderIndex1 = parent1.getDrawComponents().indexOf(primary);
		
		parent2 = secondary.getParent();
		drawOrderIndex2 = parent2.getDrawComponents().indexOf(secondary);
		
		area1 = new Area(primary.getGeneralShape());
		area2 = new Area(secondary.getGeneralShape());
				
		GeneralShape operationShape = performOperation(area1, area2);
		booleanOperation = new ShapeDrawComponentImpl();
		booleanOperation.setGeneralShape(operationShape);
		copyProperties(primary, booleanOperation);
		parent1.removeDrawComponent(primary);
		parent2.removeDrawComponent(secondary);
		
		parent1.addDrawComponent(booleanOperation, drawOrderIndex1);
	}

	@Override
	public void undo() 
	{
		parent1.removeDrawComponent(booleanOperation);
		parent1.addDrawComponent(primary, drawOrderIndex1);
		parent2.addDrawComponent(secondary, drawOrderIndex2);
	}	
	
	@Override
	public void redo() 
	{
		execute();
	}	
	
	protected abstract GeneralShape performOperation(Area area1, Area area2);
}
