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

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.viewer.ui.tool.IToolManager;

public abstract class AbstractCanvasComposite
extends Composite
implements IViewer
{
	public AbstractCanvasComposite(Composite parent, int style, DrawComponent dc) {
		this(parent, style, dc, true);
	}

	public AbstractCanvasComposite(Composite parent, int style, DrawComponent dc,
			boolean autoScroll)
	{
		super(parent, style);
		this.autoScroll = autoScroll;
		this.drawComponent = dc;
		GridLayout layout = new GridLayout(1, true);
		setLayout(layout);
		init(this);
		addDisposeListener(disposeListener);
	}
	
	private boolean autoScroll = true;
	private IAutoScrollSupport autoScrollSupport = null;
	public IAutoScrollSupport getAutoScrollSupport() {
		return autoScrollSupport;
	}
	
	protected void init(Composite parent)
	{
		// TODO: uncomment if used as plugin, renderMode Registration now done by TestDialog
//		RenderModeManager renderMan = RendererRegistry.sharedInstance().getRenderModeManager();
//		drawComponent.setRenderModeManager(renderMan);
		
		getHitTestManager();
		canvas = createCanvas(this);
		mouseManager = initMouseManager(this);
		getZoomSupport().addZoomListener(zoomListener);
		updateCanvas();
		
		if (autoScroll) {
			autoScrollSupport = initAutoScrollSupport();
		}
	}
		
	private DrawComponent drawComponent;
		
	/**
	 * 
	 * @return the DrawComponent to draw
	 */
	public DrawComponent getDrawComponent() {
		return drawComponent;
	}
	
	/**
	 * 
	 * @param drawComponent the DrawComponent to draw
	 */
	public void setDrawComponent(DrawComponent drawComponent) {
		this.drawComponent = drawComponent;
		hitTestManager = new HitTestManager(drawComponent);
	}
				
	/**
	 * updates the Canvas
	 *
	 */
	public void updateCanvas()
	{
		if (canvas != null)
			canvas.repaint();
	}
	
	/**
	 * zooms the viewer to the given zoomFactor
	 * @param zoomFactor the zoomFactor to zoom (1.0 = 100%)
	 */
	public void setZoom(double zoomFactor) {
		setZoom(zoomFactor, false);
	}

	private ICanvas canvas;
	public ICanvas getCanvas() {
		return canvas;
	}
	
	protected void setZoom(double zoomFactor, boolean internal)
	{
		if (internal) {
			getZoomSupport().setZoom(zoomFactor);
		}
		double zoom = getZoom();
		if (canvas != null) {
			canvas.setScale(zoom);
			canvas.repaint();
		}
	}
		
	/**
	 * 
	 * @return the current zoomFactor as double (100% = 1.0)
	 */
	public double getZoom() {
		return getZoomSupport().getZoom();
	}
	 		
	public static Color defaultBgColor = new Color(null, 255, 255, 255);
	private Color bgColor = defaultBgColor;
	
	/**
	 * 
	 * @return the Background Color of the Viewer
	 */
	public Color getBgColor() {
		return bgColor;
	}
	/**
	 * 
	 * @param bgColor the Background Color of the Viewer
	 */
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
		canvas.setBackground(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
	}

	private SelectionManager selectionManager = null;
	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IViewer#getSelectionManager()
	 */
	public SelectionManager getSelectionManager()
	{
		if (selectionManager == null)
			selectionManager = new SelectionManager(this);
		
		return selectionManager;
	}

	private IZoomSupport zoomSupport = null;
	public IZoomSupport getZoomSupport()
	{
		if (zoomSupport == null)
			zoomSupport = new ZoomSupport(getViewport());
		
		return zoomSupport;
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IViewer#getRenderModeManager()
	 */
	public RenderModeManager getRenderModeManager() {
		return getDrawComponent().getRenderModeManager();
	}
	
	private IZoomListener zoomListener = new IZoomListener()
	{
		public void zoomChanged(double zoom) {
			setZoom(zoom, false);
		}
	};
	
	private IMouseManager mouseManager = null;
	public IMouseManager getMouseManager() {
		return mouseManager;
	}
	
	private HitTestManager hitTestManager = null;
	public HitTestManager getHitTestManager()
	{
		if (hitTestManager == null)
			hitTestManager = new HitTestManager(getDrawComponent());
		
		return hitTestManager;
	}
	
	protected abstract IMouseManager initMouseManager(IViewer viewer);
	protected abstract ICanvas createCanvas(Composite parent);
	protected abstract IAutoScrollSupport initAutoScrollSupport();

	private DisposeListener disposeListener = new DisposeListener()
	{
		public void widgetDisposed(DisposeEvent e)
		{
			getZoomSupport().removeZoomListener(zoomListener);
			getCanvas().dispose();
			hitTestManager = null;
			mouseManager = null;
			selectionManager = null;
			zoomSupport = null;
		}
	};
	
	private IToolManager toolManager;
	/**
	 * Return the toolManager.
	 * @return the toolManager
	 */
	public IToolManager getToolManager() {
		return toolManager;
	}

	/**
	 * Sets the toolManager.
	 * @param toolManager the toolManager to set
	 */
	public void setToolManager(IToolManager toolManager) {
		this.toolManager = toolManager;
	}
	
}
