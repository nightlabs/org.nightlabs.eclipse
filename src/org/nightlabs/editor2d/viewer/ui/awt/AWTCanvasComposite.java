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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.AbstractCanvasComposite;
import org.nightlabs.editor2d.viewer.ui.IAutoScrollSupport;
import org.nightlabs.editor2d.viewer.ui.IBufferedCanvas;
import org.nightlabs.editor2d.viewer.ui.ICanvas;
import org.nightlabs.editor2d.viewer.ui.IMouseManager;
import org.nightlabs.editor2d.viewer.ui.IViewer;
import org.nightlabs.editor2d.viewer.ui.IViewport;

public class AWTCanvasComposite
extends AbstractCanvasComposite
implements IComponentViewer
{
//	public static int styleFlag = SWT.EMBEDDED | SWT.DOUBLE_BUFFERED;
//	public static int styleFlag = SWT.EMBEDDED | SWT.NO_BACKGROUND | SWT.NO_MERGE_PAINTS;
	public static int styleFlag = SWT.EMBEDDED;
	public static final Logger logger = Logger.getLogger(AWTCanvasComposite.class);
	private Frame frame = null;
	private DisplayPanel displayPanel = null;
//	private BufferedViewport displayPanel = null;	

	public AWTCanvasComposite(Composite parent, int style, DrawComponent dc)
	{
		super(parent, style | styleFlag, dc);
		addDisposeListener(disposeListener);
	}

	public AWTCanvasComposite(Composite parent, int style, DrawComponent dc, boolean autoScroll)
	{
		super(parent, style | styleFlag, dc, autoScroll);
		addDisposeListener(disposeListener);
	}

	public Component getComponent() {
		return (Component) getCanvas();
	}
			
	@Override
	protected IAutoScrollSupport initAutoScrollSupport()
	{
		IAutoScrollSupport autoScrollSupport = new AWTCanvasAutoScrollSupport(getComponent());
		autoScrollSupport.setScrollStep(10);
		autoScrollSupport.setTimerDelay(5);
		return autoScrollSupport;
	}
	  
	@Override
	public IMouseManager initMouseManager(IViewer viewer) {
		return new AWTMouseManager(viewer, getComponent());
	}
				
	public IBufferedCanvas getBufferedCanvas() {
		return (IBufferedCanvas) getCanvas();
	}
	
	public IViewport getViewport() {
		return (IViewport) getCanvas();
	}
	
	@Override
	public void updateCanvas()
	{
		if (getCanvas() != null)
			getBufferedCanvas().notifyChange();
	}

	@Override
	protected ICanvas createCanvas(Composite parent)
	{
		frame = SWT_AWT.new_Frame(parent);
		frame.setLayout(new BorderLayout());
		displayPanel = new DisplayPanel(getDrawComponent());
//		displayPanel = new BufferedViewport(getDrawComponent());
		frame.add(displayPanel, BorderLayout.CENTER);		
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				if (displayPanel != null)
					displayPanel.dispose();
			}
		});
		
		return displayPanel;
	}
			
	private DisposeListener disposeListener = new DisposeListener()
	{
		public void widgetDisposed(DisposeEvent e)
		{
			if (logger.isDebugEnabled())
				logger.debug("disposeListener()!"); //$NON-NLS-1$
			
			if (getAutoScrollSupport() != null)
				getAutoScrollSupport().dispose();
		}
	};
			
}
