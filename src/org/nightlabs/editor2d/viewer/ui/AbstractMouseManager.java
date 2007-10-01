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
	
//	protected double zoom = 1.0d;
//	protected IZoomListener zoomListener = new IZoomListener()
//	{	
//		public void zoomChanged(double zoom) {
//			AbstractMouseManager.this.zoom = zoom;
//		}	
//	};
//	public IZoomListener getZoomListener() {
//		return zoomListener;
//	}
//	public double getZoom() {
//		return zoom;
//	}
	
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
 
	protected Collection mouseChangedListeners = null;
	protected Collection getMouseChangedListeners() 
	{
		if (mouseChangedListeners == null)
			mouseChangedListeners = new ArrayList();
		
		return mouseChangedListeners;
	}

	protected void doFireMouseChanged() 
	{
		for (Iterator it = getMouseChangedListeners().iterator(); it.hasNext(); ) {
			IMouseChangedListener l = (IMouseChangedListener) it.next();
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
		
	protected Collection mouseMoveListenerer = null;
	protected Collection getMouseMoveListeners() 
	{
		if (mouseMoveListenerer == null)
			mouseMoveListenerer = new ArrayList();
		
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
		for (Iterator it = getMouseMoveListeners().iterator(); it.hasNext(); ) {
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
	
	protected Collection mouseListeners = null;
	protected Collection getMouseListeners() 
	{
		if (mouseListeners == null)
			mouseListeners = new ArrayList();
		
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
		for (Iterator it = getMouseListeners().iterator(); it.hasNext(); ) {
			MouseListener l = (MouseListener) it.next();
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
		for (Iterator it = getMouseListeners().iterator(); it.hasNext(); ) {
			MouseListener l = (MouseListener) it.next();
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
	
//	protected DrawComponent oldDC = null;	
//	protected int oldRenderMode = 0;
//	protected void drawRollOver(DrawComponent dc) 
//	{
//    if (!dc.equals(oldDC)) {            
//			if (oldDC != null) {
//				oldDC.setRenderMode(oldRenderMode);
//				getViewer().getBufferedCanvas().getTempContentManager().removeFromTempContent(oldDC);
//			}
//			oldDC = dc;
//			oldRenderMode = dc.getRenderMode();			
//			dc.setRenderMode(RenderModeManager.ROLLOVER_MODE);
//			getViewer().getBufferedCanvas().getTempContentManager().addToTempContent(dc);	
//			getViewer().getBufferedCanvas().repaint();
//    }
//	}
//	
//	protected void drawSelected(DrawComponent dc)
//	{
//		
//	}
//		
//	protected boolean debug = false;
//	protected boolean wasRepainted = false;
//	protected void checkDrawComponents(int x, int y) 
//	{
//		long startTime = 0;
//		if (debug)
//			startTime = System.currentTimeMillis(); 
//				
//		DrawComponent dc = getViewer().findObjectAt(x, y);		
//		if (dc != null) {			
//			drawRollOver(dc);
//		}
//		else {
//			if (oldDC != null) {
////				if (!wasRepainted) {
////					oldDC.setRenderMode(oldRenderMode);
////					getViewer().getBufferedCanvas().repaint();
////					wasRepainted = true;
////				}
//			}
//		}
//		
//		if (debug) {
//			long endTime = System.currentTimeMillis() - startTime;			
//			LOGGER.debug("findObject took "+endTime+" ms");			
//		}					
//	}
	 	
	
}
