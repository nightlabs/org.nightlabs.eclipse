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

import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class DeletePageCommand 
extends DeleteDrawComponentCommand 
{
	public DeletePageCommand(RootDrawComponent root, PageDrawComponent page) {
		super(root, page);
	}
	
	@Override
	public void execute() 
	{
		super.execute();
		setCurrentPage();
	}	
	
	@Override
	public void redo() 
	{
		super.redo();
		setCurrentPage();	  
	}
	
	@Override
	public void undo() 
	{
		super.undo();
	  getRootDrawComponent().setCurrentPage((PageDrawComponent)child);	  
	}	
	
	protected void setCurrentPage() 
	{
    if (index != 0)
    	getRootDrawComponent().setCurrentPage((PageDrawComponent) parent.getDrawComponents().get(index-1));
    else if (parent.getDrawComponents().size() == 1)
    	getRootDrawComponent().setCurrentPage((PageDrawComponent) parent.getDrawComponents().get(0));
    else if (index == 0) {
    	getRootDrawComponent().setCurrentPage((PageDrawComponent) parent.getDrawComponents().get(index+1));
    }			  	  
	}	
	
	private RootDrawComponent getRootDrawComponent() {
		return (RootDrawComponent) parent;
	}
}
