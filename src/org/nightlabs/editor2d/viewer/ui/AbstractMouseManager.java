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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.swt.widgets.Display;
import org.nightlabs.editor2d.viewer.ui.event.IMouseChangedListener;
import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;
import org.nightlabs.editor2d.viewer.ui.event.MouseListener;
import org.nightlabs.editor2d.viewer.ui.event.MouseMoveListener;

public abstract class AbstractMouseManager
implements IMouseManager
{
	public AbstractMouseManager(IViewer viewer)
	{
		super();
		this.viewer = viewer;
	}
	
	protected IViewer viewer;
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
		
	protected int x = 0;
	protected int y = 0;
	
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
			
	protected Point relativePoint = new Point(x,y);
	public Point getRelativePoint()
	{
		relativePoint.x = getRelativeX();
		relativePoint.y = getRelativeY();
		return relativePoint;
	}
	
	protected Point absolutePoint = new Point(x,y);
	public Point getAbsolutePoint()
	{
		absolutePoint.x = getAbsoluteX();
		absolutePoint.y = getAbsoluteY();
		return absolutePoint;
	}
 
	protected Collection<IMouseChangedListener> mouseChangedListeners = null;
	protected Collection<IMouseChangedListener> getMouseChangedListeners()
	{
		if (mouseChangedListeners == null)
			mouseChangedListeners = new ArrayList<IMouseChangedListener>();
		
		return mouseChangedListeners;
	}

	protected void doFireMouseChanged()
	{
		for (Iterator<IMouseChangedListener> it = getMouseChangedListeners().iterator(); it.hasNext(); ) {
			IMouseChangedListener l = it.next();
			if (l != null)
				l.mouseChanged(getRelativePoint(), getAbsolutePoint());
		}
	}
		
	protected void fireMouseChanged()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run() {
				doFireMouseChanged();
			}
		});
	}
	
	public void addMouseChangedListener(IMouseChangedListener l) {
		getMouseChangedListeners().add(l);
	}
	
	public void removeMouseChangedListener(IMouseChangedListener l) {
		getMouseChangedListeners().remove(l);
	}
		
	protected Collection<MouseMoveListener> mouseMoveListenerer = null;
	protected Collection<MouseMoveListener> getMouseMoveListeners()
	{
		if (mouseMoveListenerer == null)
			mouseMoveListenerer = new ArrayList<MouseMoveListener>();
		
		return mouseMoveListenerer;
	}

	public void addMouseMoveListener(MouseMoveListener l) {
		getMouseMoveListeners().add(l);
	}
	
	public void removeMouseMoveListener(MouseMoveListener l) {
		getMouseMoveListeners().remove(l);
	}

	protected MouseEvent mouseEvent = new MouseEvent();
	protected void doFireMouseMoved(int x, int y, int mouseButton)
	{
		mouseEvent.setX(x);
		mouseEvent.setY(y);
		mouseEvent.setButton(mouseButton);
		for (Iterator<MouseMoveListener> it = getMouseMoveListeners().iterator(); it.hasNext(); ) {
			MouseMoveListener l = (MouseMoveListener) it.next();
			l.mouseMoved(mouseEvent);
		}
	}
	
	protected void fireMouseMoved(final int x, final int y, final int mouseButton)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run() {
				doFireMouseMoved(x, y, mouseButton);
			}
		});
	}
	
	protected Collection<MouseListener> mouseListeners = null;
	protected Collection<MouseListener> getMouseListeners()
	{
		if (mouseListeners == null)
			mouseListeners = new ArrayList<MouseListener>();
		
		return mouseListeners;
	}

	public void addMouseListener(MouseListener l) {
		getMouseListeners().add(l);
	}
	
	public void removeMouseListener(MouseListener l) {
		getMouseListeners().remove(l);
	}

	protected void doFireMousePressed(int x, int y, int mouseButton)
	{
		mouseEvent.setX(x);
		mouseEvent.setY(y);
		mouseEvent.setButton(mouseButton);
		for (Iterator<MouseListener> it = getMouseListeners().iterator(); it.hasNext(); ) {
			MouseListener l = it.next();
			l.mousePressed(mouseEvent);
		}
	}
	
	protected void fireMousePressed(final int x, final int y, final int mouseButton)
	{
		Display.getDefault().asyncExec(new Runnable()
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
		for (Iterator<MouseListener> it = getMouseListeners().iterator(); it.hasNext(); ) {
			MouseListener l = it.next();
			l.mouseReleased(mouseEvent);
		}
	}
	
	protected void fireMouseReleased(final int x, final int y, final int mouseButton)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run() {
				doFireMouseReleased(x, y, mouseButton);
			}
		});
	}
	
}
