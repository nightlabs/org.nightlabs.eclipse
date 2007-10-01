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

package org.nightlabs.editor2d.viewer.ui.awt;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import org.nightlabs.editor2d.viewer.ui.AbstractAutoScrollSupport;

public abstract class AbstractAWTAutoScrollSupport 
extends AbstractAutoScrollSupport 
{
	public AbstractAWTAutoScrollSupport(Component comp) 
	{
		super();
		component = comp;
		init();
	}
	
	private Component component;	
	public Component getComponent() {
		return component;
	}
	
	protected void init() 
	{
		component.addComponentListener(resizeListener);
		component.addMouseListener(exitListener);
		component.addMouseMotionListener(moveListener);
		initAutoScroll(component.getBounds());
	}
	
	private ComponentListener resizeListener = new ComponentAdapter()
	{
		public void componentResized(ComponentEvent evt) 
		{
			Component c = evt.getComponent();
			initAutoScroll(c.getBounds());
		}
	};
	
	private MouseMotionListener moveListener = new MouseMotionAdapter()
	{	
		public void mouseMoved(MouseEvent evt) {
			AbstractAWTAutoScrollSupport.this.mouseMoved(evt.getX(), evt.getY());
		}	
	};
	
	private MouseListener exitListener = new MouseAdapter()
	{	
		public void mouseExited(MouseEvent arg0) {
			AbstractAWTAutoScrollSupport.this.mouseExited();
		}	
	};
	@Override
	public void dispose() 
	{
		component.removeComponentListener(resizeListener);
		component.removeMouseMotionListener(moveListener);
		component.removeMouseListener(exitListener);
		super.dispose();
	}
		
}
