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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nightlabs.editor2d.viewer.IDrawComponentConditional;
import org.nightlabs.editor2d.viewer.IViewer;
import org.nightlabs.editor2d.viewer.event.MouseEvent;
import org.nightlabs.editor2d.viewer.event.MouseListener;
import org.nightlabs.editor2d.viewer.event.MouseMoveListener;

public class ToolManager 
implements IToolManager 
{

	public ToolManager(IViewer viewer) {
		setViewer(viewer);
	}
	
	protected Map<String, ITool> id2Tool = new HashMap<String, ITool>();	
	
	protected List<ITool> tools = null;
	public List<ITool> getTools() 
	{
		if (tools == null)
			tools = new ArrayList();
		
		return tools;
	}

	public ITool getTool(String id) {
		return id2Tool.get(id);
	}
	
	public void addTool(ITool tool) 
	{
		tool.setViewer(viewer);
		id2Tool.put(tool.getID(), tool);
		getTools().add(tool);		
	}

	public void removeTool(ITool tool) {
		getTools().remove(tool);
		id2Tool.remove(tool);
	}

	protected IViewer viewer = null;
	public IViewer getViewer() {
		return viewer;
	}
	public void setViewer(IViewer viewer) 
	{
		if (viewer != null) {
			viewer.getMouseManager().removeMouseMoveListener(mouseMoveListener);
			viewer.getMouseManager().removeMouseListener(mouseListener);
		}
		
		this.viewer = viewer;
		viewer.getMouseManager().addMouseMoveListener(mouseMoveListener);
		viewer.getMouseManager().addMouseListener(mouseListener);
		for (Iterator it = getTools().iterator(); it.hasNext(); ) {
			ITool tool = (ITool) it.next();
			tool.setViewer(viewer);
		}
	}

	public void setActiveTool(int id) 
	{
		ITool tool = (ITool) id2Tool.get(new Integer(id));
		if (tool != null)
			setActiveTool(tool);
	}
	
	protected ITool activeTool = null;
	public void setActiveTool(ITool tool) 
	{
		if (activeTool != null)
			activeTool.deactivate();
		
		activeTool = tool;
		activeTool.activate();
	}
	public ITool getActiveTool() {
		return activeTool;
	}
		
	protected void doMouseMoved(MouseEvent me) 
	{	
		if (activeTool != null) {
			activeTool.mouseMoved(me);
		}		
	}
	
	protected void doMouseReleased(MouseEvent me) 
	{
		if (activeTool != null) {
			activeTool.mouseReleased(me);
		}
	}

	protected void doMousePressed(MouseEvent me) 
	{
		if (activeTool != null) {
			activeTool.mousePressed(me);
		}					
	}
	
	protected MouseMoveListener mouseMoveListener = new MouseMoveListener()
	{	
		public void mouseMoved(MouseEvent me) {
			doMouseMoved(me);
		}	
	};
	
	protected MouseListener mouseListener = new MouseListener()
	{	
		public void mouseReleased(MouseEvent me) {
			doMouseReleased(me);
		}	
		public void mousePressed(MouseEvent me) {
			doMousePressed(me);
		}	
	};
	
	protected IDrawComponentConditional conditional = null;	
	public void setConditional(IDrawComponentConditional conditional) {
		this.conditional = conditional;
		for (Iterator<ITool> it = getTools().iterator(); it.hasNext(); ) {
			ITool tool = it.next();
			tool.setConditional(conditional);
		}
	}
	public IDrawComponentConditional getConditional() {
		return conditional;
	}

}
