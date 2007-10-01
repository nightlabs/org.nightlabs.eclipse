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

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.editor2d.viewer.AbstractMouseManager;
import org.nightlabs.editor2d.viewer.IViewer;

public class SWTMouseManager 
extends AbstractMouseManager 
{

	public SWTMouseManager(IViewer viewer, Control c) 
	{
		super(viewer);
		this.control = c;
	}

	protected Control control = null;
	public Control getControl() {
		return control;
	}
	
	protected void init() 
	{
		control.addMouseMoveListener(mouseMoveListener);
		control.addDisposeListener(disposeListener);
	}
	
	protected MouseMoveListener mouseMoveListener = new MouseMoveListener()
	{	
		public void mouseMove(MouseEvent e) 
		{
			x = e.x;
			y = e.y;
			
			fireMouseChanged();
			fireMouseMoved(x, y, e.button);
		}	
	};
	
	protected MouseListener mouseListener = new MouseAdapter()
	{	
		public void mouseUp(MouseEvent e) {
			fireMouseReleased(e.x, e.y, e.button);
		}
	
		public void mouseDown(MouseEvent e) {
			fireMousePressed(e.x, e.y, e.button);
		}	
	};
		
	protected DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent evt) {
			control.removeMouseMoveListener(mouseMoveListener);
		}
	};
	 
}
