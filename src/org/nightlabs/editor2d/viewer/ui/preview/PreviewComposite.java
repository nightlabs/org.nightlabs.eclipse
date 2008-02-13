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
package org.nightlabs.editor2d.viewer.ui.preview;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.util.ColorUtil;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.IViewport;

/**
 * <p> Project: org.nightlabs.editor2d.viewer.ui </p>
 * <p> Creation Date: 04.01.2006 </p>
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PreviewComposite
extends XComposite
{
	/**
	 * @param parent
	 * @param style
	 */
	public PreviewComposite(DrawComponent dc, IViewport viewport, Composite parent, int style) {
		super(parent, style | SWT.EMBEDDED);
		this.dc = dc;
		this.viewport = viewport;
		init();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public PreviewComposite(DrawComponent dc, IViewport viewport, Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style | SWT.EMBEDDED, layoutMode, layoutDataMode);
		this.dc = dc;
		this.viewport = viewport;
		init();
	}

	private DrawComponent dc;
	private IViewport viewport;
	private Frame previewFrame;
	private PreviewPanel previewPanel;
	private void init()
	{
		previewFrame = SWT_AWT.new_Frame(this);
		previewFrame.setLayout(new BorderLayout());
		previewPanel = new PreviewPanel(dc, viewport, ColorUtil.toAWTColor(getBackground()));
		previewFrame.add(previewPanel, BorderLayout.CENTER);
		
		previewFrame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				previewPanel.dispose();
			}
		});
	}
}
