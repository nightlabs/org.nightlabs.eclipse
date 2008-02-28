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

package org.nightlabs.editor2d.viewer.ui;

import java.awt.Point;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;
import org.nightlabs.editor2d.viewer.ui.event.MouseListener;
import org.nightlabs.editor2d.viewer.ui.event.MouseMoveListener;

public abstract class AbstractMouseManager
implements IMouseManager
{
	private static Logger logger = Logger.getLogger(AbstractMouseManager.class);	
	private IViewer viewer;
	protected int x = 0;
	protected int y = 0;
	private Point relativePoint = new Point(x,y);
	private Point absolutePoint = new Point(x,y);
	private MouseEvent mouseEvent = new MouseEvent();
	private ListenerList mouseMoveListeners = new ListenerList();
	private ListenerList mouseListeners = new ListenerList();

	public AbstractMouseManager(IViewer viewer)
	{
		super();
		this.viewer = viewer;
	}
	
	public IViewer getViewer() {
		return viewer;
	}
		
	public double getZoom() {
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
			
	public int getRelativeX() {
		return ((int) Math.rint(x / getZoom())) + getRelativeScrollOffsetX();
	}
	
	public int getRelativeY() {
		return ((int) Math.rint(y / getZoom())) + getRelativeScrollOffsetY();
	}
	
	public int getAbsoluteX() {
		return x + getAbsoluteScrollOffsetX();
	}
	
	public int getAbsoluteY() {
		return y + getAbsoluteScrollOffsetY();
	}
			
	public Point getRelativePoint()
	{
		relativePoint.x = getRelativeX();
		relativePoint.y = getRelativeY();
		return relativePoint;
	}
	
	public Point getAbsolutePoint()
	{
		absolutePoint.x = getAbsoluteX();
		absolutePoint.y = getAbsoluteY();
		return absolutePoint;
	}
 
	public void addMouseMoveListener(MouseMoveListener l) {
		if (l != null)
			mouseMoveListeners.add(l);
	}
	
	public void removeMouseMoveListener(MouseMoveListener l) {
		if (l != null)
			mouseMoveListeners.remove(l);
	}

	protected void doFireMouseMoved(int x, int y, int mouseButton)
	{
		mouseEvent.setX(x);
		mouseEvent.setY(y);
		mouseEvent.setButton(mouseButton);
		for (int i=0; i<mouseMoveListeners.size(); i++) {
			MouseMoveListener l = (MouseMoveListener) mouseMoveListeners.getListeners()[i];
//			l.mouseMoved(new MouseEvent(x, y, mouseButton));
			l.mouseMoved(mouseEvent);
		}
	}
	
	protected void fireMouseMoved(final int x, final int y, final int mouseButton)
	{
		Display.getDefault().asyncExec(new Runnable()
//		Display.getDefault().syncExec(new Runnable()
		{			
			public void run() {
				doFireMouseMoved(x, y, mouseButton);
			}
		});
	}
	
	public void addMouseListener(MouseListener l) {
		mouseListeners.add(l);
	}
	
	public void removeMouseListener(MouseListener l) {
		mouseListeners.remove(l);
	}

	protected void doFireMousePressed(int x, int y, int mouseButton)
	{
		mouseEvent.setX(x);
		mouseEvent.setY(y);
		mouseEvent.setButton(mouseButton);
		for (int i=0; i<mouseListeners.size(); i++) {
			MouseListener l = (MouseListener) mouseListeners.getListeners()[i];
//			l.mousePressed(new MouseEvent(x, y, mouseButton));
			l.mousePressed(mouseEvent);
		}
	}
	
	protected void fireMousePressed(final int x, final int y, final int mouseButton)
	{
		Display.getDefault().asyncExec(new Runnable()
//		Display.getDefault().syncExec(new Runnable()
		{
			public void run() {
				doFireMousePressed(x, y, mouseButton);
			}
		});
	}
	
	protected void doFireMouseReleased(int x, int y, int mouseButton)
	{
		mouseEvent.setX(x);
		mouseEvent.setY(y);
		mouseEvent.setButton(mouseButton);
		for (int i=0; i<mouseListeners.size(); i++) {
			MouseListener l = (MouseListener) mouseListeners.getListeners()[i];
//			l.mouseReleased(new MouseEvent(x, y, mouseButton));
			l.mouseReleased(mouseEvent);
		}
	}
	
	protected void fireMouseReleased(final int x, final int y, final int mouseButton)
	{
		Display.getDefault().asyncExec(new Runnable()
//		Display.getDefault().syncExec(new Runnable()
		{
			public void run() {
				doFireMouseReleased(x, y, mouseButton);
			}
		});
	}
	
}
