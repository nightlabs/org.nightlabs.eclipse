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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import org.nightlabs.editor2d.viewer.ui.AbstractMouseManager;
import org.nightlabs.editor2d.viewer.ui.IViewer;

public class AWTMouseManager
extends AbstractMouseManager
{
	public AWTMouseManager(IViewer viewer, Component c)
	{
		super(viewer);
		this.component = c;
		init();
	}
	
	private Component component = null;
	public Component getComponent() {
		return component;
	}
	
	protected void init()
	{
		component.addMouseListener(mouseListener);
		component.addMouseMotionListener(mouseMotionListener);
	}
	
	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter()
	{
		@Override
		public void mouseMoved(MouseEvent evt)
		{
//			System.out.println("mouseMoved called at "+System.currentTimeMillis());
			x = evt.getX();
			y = evt.getY();
//			doFireMouseMoved(x, y, evt.getButton());
			fireMouseMoved(x, y, evt.getButton());
		}
		
		@Override
		public void mouseDragged(MouseEvent evt)
		{
			x = evt.getX();
			y = evt.getY();
//			doFireMouseMoved(x, y, evt.getButton());
			fireMouseMoved(x, y, evt.getButton());
		}
	};
	
	private MouseListener mouseListener = new MouseAdapter()
	{
		@Override
		public void mousePressed(MouseEvent me) {
//			doFireMousePressed(me.getX(), me.getY(), me.getButton());
			fireMousePressed(me.getX(), me.getY(), me.getButton());
		}
		
		@Override
		public void mouseReleased(MouseEvent me) {
//			doFireMouseReleased(me.getX(), me.getY(), me.getButton());
			fireMouseReleased(me.getX(), me.getY(), me.getButton());
		}
	};
	
}
