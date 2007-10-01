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
package org.nightlabs.editor2d.command;

import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class CreatePageCommand 
extends CreateDrawComponentCommand 
{
	public CreatePageCommand(RootDrawComponent root, Editor2DFactory factory)
	{
		super();
		if (root == null)
			throw new IllegalArgumentException("Param root must not be null!"); //$NON-NLS-1$
		if (factory == null)
			throw new IllegalArgumentException("Param factory must not be null!");	   //$NON-NLS-1$
		
		this.parent = root;
	  this.factory = factory;
	  setLabel(Messages.getString("org.nightlabs.editor2d.command.CreatePageCommand.label"));	   //$NON-NLS-1$
	}
	
	private Editor2DFactory factory = null;

	public void execute() 
	{
	  drawComponent = factory.createPageDrawComponent();	  
    getPage().setParent(getRootDrawComponent());
		drawOrderIndex = getRootDrawComponent().getDrawComponents().indexOf(
        getRootDrawComponent().getCurrentPage()) + 1;    
    getRootDrawComponent().addDrawComponent(getPage(), drawOrderIndex);
    getRootDrawComponent().setCurrentPage(getPage());
	}	
	
	public void redo() 
	{
    super.redo();
		getRootDrawComponent().setCurrentPage(getPage());			  		
	}	
			
	protected RootDrawComponent getRootDrawComponent() {
	  return (RootDrawComponent) parent;
	}
  
  protected PageDrawComponent getPage() {
    return (PageDrawComponent) drawComponent;
  }	
}
