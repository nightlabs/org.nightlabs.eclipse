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

package org.nightlabs.editor2d.viewer.tool;

import java.util.List;

import org.nightlabs.editor2d.viewer.IViewer;

public interface IToolManager 
{
	/**
	 * 
	 * @return a List with all registered Tools
	 */
	List getTools();
	
	/**
	 * 
	 * @param id the id of the Tool
	 * @return the Tool with the corresponding ID
	 */
	ITool getTool(String id);
	
	/**
	 * adds a Tool to the ToolManager
	 * @param tool the ITool to add
	 */
	void addTool(ITool tool);
	
	/**
	 * removes a previously added Tool
	 * @param tool the ITool to remove
	 */
	void removeTool(ITool tool);
	
	/**
	 * 
	 * @return the IViewer for the ToolManager 
	 */
	IViewer getViewer();
	
	/**
	 * 
	 * @param viewer the IViewer for the ToolManager
	 */
	void setViewer(IViewer viewer);
	
	/**
	 * 
	 * @param tool the active Tool
	 */
	public void setActiveTool(ITool tool) ;
	
	/**
	 * 
	 * @param id the ID of the Tool
	 */
	public void setActiveTool(int id) ;
	
	/**
	 * 
	 * @return the active Tool
	 */
	public ITool getActiveTool();
}
