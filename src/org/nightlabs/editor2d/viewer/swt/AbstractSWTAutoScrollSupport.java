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

package org.nightlabs.editor2d.viewer.swt;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.util.GeomUtil;
import org.nightlabs.editor2d.viewer.AbstractAutoScrollSupport;

public abstract class AbstractSWTAutoScrollSupport
extends AbstractAutoScrollSupport
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractSWTAutoScrollSupport.class.getName());
	
	protected Control control;
	public Control getControl() {
		return control;
	}
	
	public AbstractSWTAutoScrollSupport(Control control) 
	{
		super();
		this.control = control;
		init();
	}
	
	protected void init() 
	{
		control.addControlListener(controlSizeListener);
		control.addMouseMoveListener(autoScrollListener);	
		control.addMouseTrackListener(mouseTrackListener);
		control.addDisposeListener(disposeListener);
		initAutoScroll(GeomUtil.toAWTRectangle(control.getBounds()));
	}
	
	protected DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) 
		{
			control.removeControlListener(controlSizeListener);
			control.removeMouseMoveListener(autoScrollListener);
			control.removeMouseTrackListener(mouseTrackListener);
			stopTimers();
		}	
	};
	
	protected ControlListener controlSizeListener = new ControlListener()
	{	
		public void controlResized(ControlEvent evt) 
		{
			logger.debug("Control Resized!"); //$NON-NLS-1$
			Control c = (Control) evt.getSource();
			initAutoScroll(GeomUtil.toAWTRectangle(c.getBounds()));
		}	
		public void controlMoved(ControlEvent arg0) {
			
		}	
	};	
		
	protected MouseMoveListener autoScrollListener = new MouseMoveListener()
	{	
		public void mouseMove(MouseEvent e) 
		{
			mouseMoved(e.x, e.y);
		}	
	};
		
	protected MouseTrackListener mouseTrackListener = new MouseTrackAdapter()
	{	
		public void mouseExit(MouseEvent e) 
		{
			mouseExited();
		}
		
		public void mouseEnter(MouseEvent e) {
			super.mouseEnter(e);
		}	
	};
	
	protected void doScrollDown(final int scrollStep) 
	{
		Display.getDefault().asyncExec(new Runnable()
		{		
			public void run() {
				scrollDown(scrollStep);
			}		
		}); 		
	}
	
	protected void doScrollUp(final int scrollStep) 
	{
		Display.getDefault().asyncExec(new Runnable()
		{		
			public void run() {
				scrollUp(scrollStep);
			}		
		}); 		
	}

	protected void doScrollLeft(final int scrollStep) 
	{
		Display.getDefault().asyncExec(new Runnable()
		{		
			public void run() {
				scrollLeft(scrollStep);
			}		
		}); 		
	}

	protected void doScrollRight(final int scrollStep) 
	{
		Display.getDefault().asyncExec(new Runnable()
		{		
			public void run() {
				scrollRight(scrollStep);
			}		
		}); 		
	}
			
	protected abstract void scrollDown(int scrollStep);
	protected abstract void scrollUp(int scrollStep);
	protected abstract void scrollLeft(int scrollStep);
	protected abstract void scrollRight(int scrollStep); 
}
