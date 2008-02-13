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

import java.awt.Point;
import java.util.Collection;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.IDrawComponentConditional;
import org.nightlabs.editor2d.viewer.ui.IViewer;
import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;
import org.nightlabs.editor2d.viewer.ui.event.MouseListener;
import org.nightlabs.editor2d.viewer.ui.event.MouseMoveListener;

public abstract class AbstractTool
implements ITool, MouseListener, MouseMoveListener
{
	public static final String ID_DEFAULT = "DefaultToolID";  //$NON-NLS-1$
	
	private String id = ID_DEFAULT;
	public String getID() {
		return id;
	}
	public void setID(String id) {
		this.id = id;
	}
	
	public void activate()
	{
		leftPressed = false;
		leftReleased = false;
		rightPressed = false;
		rightReleased = false;
		
		startPoint = new Point();
		currentPoint = new Point();
		deltaPoint = new Point();
	}

	public void deactivate()
	{
		leftPressed = false;
		leftReleased = false;
		rightPressed = false;
		rightReleased = false;

		startPoint = null;
		currentPoint = null;
		deltaPoint = null;
	}

	private IViewer viewer = null;
	public void setViewer(IViewer viewer) {
		this.viewer = viewer;
	}
	public IViewer getViewer() {
		return viewer;
	}
 	
	protected void addToTempContent(Object o) {
		getViewer().getBufferedCanvas().getTempContentManager().addToTempContent(o);
	}

	protected void removeTempContent(Object o) {
		getViewer().getBufferedCanvas().getTempContentManager().removeFromTempContent(o);
	}
		
	protected void repaint() {
		getViewer().getBufferedCanvas().repaint();
	}
	
	protected Point startPoint = null;
	protected Point currentPoint = null;
	protected Point deltaPoint = null;
		
	protected boolean leftPressed = false;
	protected boolean leftReleased = false;
	protected boolean rightPressed = false;
	protected boolean rightReleased = false;
	
	public void mouseMoved(MouseEvent me)
	{
		currentPoint.setLocation(me.getX(), me.getY());
		if (leftPressed) {
			deltaPoint.setLocation(me.getX() - startPoint.x, me.getY() - startPoint.y);
		}
	}
			
	public void mousePressed(MouseEvent me)
	{
		if (me.getButton() == MouseEvent.BUTTON1 ){
			leftPressed = true;
			leftReleased = false;
		}
		if (me.getButton() == MouseEvent.BUTTON3) {
			rightPressed = true;
			rightReleased = false;
		}
		
		startPoint.setLocation(me.getX(), me.getY());
	}

	public void mouseReleased(MouseEvent me)
	{
		if (me.getButton() == MouseEvent.BUTTON1) {
			leftReleased = true;
		}
		if (me.getButton() == MouseEvent.BUTTON2) {
			rightReleased = true;
		}
	}
		
	protected double getZoom() {
		return getViewer().getZoom();
	}
	
	protected int getAbsoluteScrollOffsetX() {
		return getViewer().getViewport().getOffsetX();
	}
	
	protected int getAbsoluteScrollOffsetY() {
		return getViewer().getViewport().getOffsetY();
	}
	
	protected int getRelativeScrollOffsetX() {
		return (int) Math.rint(getAbsoluteScrollOffsetX() / getZoom());
	}

	protected int getRelativeScrollOffsetY() {
		return (int) Math.rint(getAbsoluteScrollOffsetY() / getZoom());
	}
	
	public int getRelativeX(int x) {
		return ((int) Math.rint(x / getZoom())) + getRelativeScrollOffsetX();
	}
	
	public int getRelativeY(int y) {
		return ((int) Math.rint(y / getZoom())) + getRelativeScrollOffsetY();
	}
	
	private IDrawComponentConditional conditional = null;
	public void setConditional(IDrawComponentConditional conditional) {
		this.conditional = conditional;
	}
	public IDrawComponentConditional getConditional() {
		return conditional;
	}
	
	public DrawComponent getDrawComponent(int x, int y, IDrawComponentConditional conditional, Collection excludeList)
	{
		return getViewer().getHitTestManager().findObjectAt(getViewer().getDrawComponent(),
				x, y, conditional, excludeList);
	}

	public DrawComponent getDrawComponent(int x, int y)
	{
		return getViewer().getHitTestManager().findObjectAt(getViewer().getDrawComponent(),
				x, y, conditional, null);
	}
	 
}
