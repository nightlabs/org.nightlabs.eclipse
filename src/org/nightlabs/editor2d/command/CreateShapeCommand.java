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

package org.nightlabs.editor2d.command;

import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.resource.Messages;


public class CreateShapeCommand  
extends CreateDrawComponentCommand
{  	
  protected GeneralShape generalShape;  
  public void setGeneralShape(GeneralShape generalShape) {
    this.generalShape = generalShape;
  }
  	
	public CreateShapeCommand() 
	{
		super();		
		setLabel(Messages.getString("org.nightlabs.editor2d.command.CreateShapeCommand.label")); 	   //$NON-NLS-1$
	}
		
	public void execute() 
	{		
	  if (generalShape != null)
      getShapeDrawComponent().setGeneralShape(generalShape);	    	    
	      		
		if (drawComponent instanceof LineDrawComponent)
      getShapeDrawComponent().setFill(false);
    
    super.execute();
	}	
			
  protected ShapeDrawComponent getShapeDrawComponent() {
   return (ShapeDrawComponent) getChild();
  }
}
