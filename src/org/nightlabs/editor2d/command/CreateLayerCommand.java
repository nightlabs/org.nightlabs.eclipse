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

import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.resource.Messages;


public class CreateLayerCommand  
extends CreateDrawComponentCommand
{		 
	public CreateLayerCommand(PageDrawComponent parent, Editor2DFactory factory)
	{
		if (parent == null) {
			throw new IllegalArgumentException("Param parent (PageDrawComponent) must not be null!"); //$NON-NLS-1$
		}	  
		this.parent = parent;
	  this.factory = factory;
	  setLabel(Messages.getString("org.nightlabs.editor2d.command.CreateLayerCommand.label"));	   //$NON-NLS-1$
	}
	
	private Editor2DFactory factory = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() 
	{
	  drawComponent = factory.createLayer();	  
    getLayer().setParent(getRootDrawComponent().getCurrentPage());
		drawOrderIndex = getRootDrawComponent().getCurrentPage().getDrawComponents().indexOf(
        getRootDrawComponent().getCurrentLayer()) + 1;    
    getRootDrawComponent().getCurrentPage().addDrawComponent(getLayer(), drawOrderIndex);
    getRootDrawComponent().setCurrentLayer(getLayer());
	}	
	
	public void redo() 
	{
    super.redo();
		getRootDrawComponent().setCurrentLayer(getLayer());			  		
	}	
			
	protected RootDrawComponent getRootDrawComponent() {
	  return (RootDrawComponent) parent.getRoot();
	}
  
  protected Layer getLayer() {
    return (Layer) drawComponent;
  }
}
