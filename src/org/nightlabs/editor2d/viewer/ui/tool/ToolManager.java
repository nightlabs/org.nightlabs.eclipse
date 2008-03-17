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

package org.nightlabs.editor2d.viewer.ui.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nightlabs.editor2d.viewer.ui.IDrawComponentConditional;
import org.nightlabs.editor2d.viewer.ui.IViewer;
import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;
import org.nightlabs.editor2d.viewer.ui.event.MouseListener;
import org.nightlabs.editor2d.viewer.ui.event.MouseMoveListener;

public class ToolManager
implements IToolManager
{
//	private static final Logger logger = Logger.getLogger(ToolManager.class);
	
//	public ToolManager(IViewer viewer) {
//		setViewer(viewer);
//	}
	public ToolManager(ToolEntryManager toolEntryManager) {
		this.toolEntryManager = toolEntryManager;
		setViewer(toolEntryManager.getViewer());
	}

	private ToolEntryManager toolEntryManager;
	private Map<String, ITool> id2Tool = new HashMap<String, ITool>();	
	private List<ITool> tools = null;
	private IViewer viewer = null;
	private ITool activeTool = null;
	private ITool defaultTool = null;
	private IDrawComponentConditional conditional = null;
	
	private MouseMoveListener mouseMoveListener = new MouseMoveListener()
	{
		public void mouseMoved(MouseEvent me) {
			doMouseMoved(me);
		}
	};
	
	private MouseListener mouseListener = new MouseListener()
	{
		public void mouseReleased(MouseEvent me) {
			doMouseReleased(me);
		}
		public void mousePressed(MouseEvent me) {
			doMousePressed(me);
		}
	};

	public List<ITool> getTools()
	{
		if (tools == null)
			tools = new ArrayList<ITool>();
		
		return tools;
	}

	public ITool getTool(String id) {
		return id2Tool.get(id);
	}
	
	public void addTool(ITool tool)
	{
		tool.setViewer(viewer);
		tool.setToolManager(this);
		id2Tool.put(tool.getID(), tool);
		getTools().add(tool);
	}

	public void removeTool(ITool tool) {
		getTools().remove(tool);
		id2Tool.remove(tool);
	}

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
		for (Iterator<ITool> it = getTools().iterator(); it.hasNext(); ) {
			ITool tool = it.next();
			tool.setViewer(viewer);
		}
	}

	public void setActiveTool(int id)
	{
		ITool tool = id2Tool.get(new Integer(id));
		if (tool != null)
			setActiveTool(tool);
	}
	
	public void setActiveTool(ITool tool)
	{
		if (this.activeTool != tool) {
			if (activeTool != null)
				activeTool.deactivate();
			
			activeTool = tool;
			activeTool.activate();
			
			IToolEntry toolEntry = getToolEntryManager().getToolEntry(activeTool);
			getToolEntryManager().setActiveToolEntry(toolEntry);
		}
	}
	
	public ITool getActiveTool() {
		return activeTool;
	}
		
	protected void doMouseMoved(MouseEvent me)
	{
		if (activeTool != null) {
			activeTool.mouseMoved(me);
//			System.out.println("Tool MouseMoved called at "+System.currentTimeMillis());
			if (activeTool.isRepaintNeeded()) {
				getViewer().getBufferedCanvas().repaint();
//				if (logger.isDebugEnabled()) {
//					logger.debug("Repaint requested at "+System.currentTimeMillis());
//				}
				activeTool.setRepaintNeeded(false);
			}
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

	/**
	 * Return the defaultTool.
	 * @return the defaultTool
	 */
	public ITool getDefaultTool() 
	{
		if (defaultTool == null && !getTools().isEmpty()) {
			defaultTool = getTools().get(0);
		}
		return defaultTool;
	}

	/**
	 * Sets the defaultTool.
	 * @param defaultTool the defaultTool to set
	 */
	public void setDefaultTool(ITool defaultTool) {
		if (this.defaultTool != defaultTool) {
			this.defaultTool = defaultTool;
			IToolEntry toolEntry = getToolEntryManager().getToolEntry(defaultTool);
			getToolEntryManager().setDefaultToolEntry(toolEntry);			
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.tool.IToolManager#getToolEntryManager()
	 */
	@Override
	public ToolEntryManager getToolEntryManager() {
		return toolEntryManager;
	}

}
