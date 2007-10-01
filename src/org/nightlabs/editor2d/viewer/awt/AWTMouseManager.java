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

package org.nightlabs.editor2d.viewer.awt;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import org.nightlabs.editor2d.viewer.AbstractMouseManager;
import org.nightlabs.editor2d.viewer.IViewer;

public class AWTMouseManager 
extends AbstractMouseManager 
{
	public AWTMouseManager(IViewer viewer, Component c) 
	{
		super(viewer);
		this.component = c;
		init();
	}
	
	protected Component component = null;	
	public Component getComponent() {
		return component;
	}
	
	protected void init() 
	{
		component.addMouseListener(mouseListener);
		component.addMouseMotionListener(mouseMotionListener);
	}
	
	protected MouseMotionListener mouseMotionListener = new MouseMotionAdapter()
	{	
		public void mouseMoved(MouseEvent evt) 
		{
			x = evt.getX();
			y = evt.getY();			
			fireMouseChanged();
			fireMouseMoved(x, y, evt.getButton());
		}
		
		public void mouseDragged(MouseEvent evt)
		{
			x = evt.getX();
			y = evt.getY();			
			fireMouseChanged();
			fireMouseMoved(x, y, evt.getButton());			
		}
	};
	
	protected MouseListener mouseListener = new MouseAdapter()
	{
		public void mousePressed(MouseEvent me) {
			fireMousePressed(me.getX(), me.getY(), me.getButton());
		}
		
		public void mouseReleased(MouseEvent me) {
			fireMouseReleased(me.getX(), me.getY(), me.getButton());
		}		
	};
	
}
