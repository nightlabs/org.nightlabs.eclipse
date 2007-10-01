/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.figures.smartupdate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.J2DGraphics;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Control;

import org.nightlabs.editor2d.figures.BufferedFreeformLayer;
import org.nightlabs.editor2d.util.EditorUtil;

/**
 * @author Alexander Bieber <alex[AT]nightalbs[DOT]de>
 *
 */
public class MinimalBufferFreeformLayer 
	extends FreeformLayer
	implements BufferedFreeformLayer
{
	public static final int TYPE_SMART_UPDATE_ONLY = 1;
	public static final int TYPE_USE_OFFSCREEN_BUFFER = 2;
	
	private Rectangle notifiedDamage;
	
	private EditPart editPart;
	private UpdateManager updateManager;
	/**
	 * The viewer control
	 */
	private Control viewerControl;
	/**
	 * The region currently viewed in
	 * screen coordinates. Width and 
	 * height correspont with the buffers
	 * width and height.
	 */
	private Rectangle viewerRegion = new Rectangle(0,0,0,0);	
	/**
	 * The current region that is drawn
	 * on the buffer in absolute coordinates.
	 */	
	private Rectangle bufferRegion;	

	/**
	 * The buffer
	 */
	private BufferedImage bufferedImage;
	/**
	 * The buffers graphics
	 */
	private Graphics2D bufferedGraphics;
	
	/**
	 */
	public MinimalBufferFreeformLayer() {
		super();
	}
	
	public void init(EditPart editPart) {
		this.editPart = editPart;
		EditPartViewer viewer = editPart.getRoot().getViewer();
		if (viewer instanceof ScrollingGraphicalViewer) {		  	
			ScrollingGraphicalViewer graphicalViewer = (ScrollingGraphicalViewer) viewer;
			Control control = graphicalViewer.getControl();
			this.viewerControl = control;
			// TODO: add controlListener
		}		
	}
	
	/**
	 * Creates the buffer according to the size of
	 * the Control.
	 */
	protected void createBuffer() {
		if (bufferedImage != null){
			bufferedGraphics.dispose();
			bufferedImage.flush();
			bufferedImage = null;
			bufferedGraphics = null;			
		}
		viewerRegion.setLocation(0,0);
		viewerRegion.setSize(viewerControl.getSize().x, viewerControl.getSize().y);
		bufferedImage = new BufferedImage(viewerRegion.width, viewerRegion.height, BufferedImage.TYPE_INT_ARGB);
		bufferedGraphics = (Graphics2D)bufferedImage.getGraphics();
	}
	
	protected void drawChildrenRegionOnBuffer(Rectangle region) {
		// TODO: implement
	}
	
	/** 
	 * merges the buffer's content with newly painted
	 * figures to hold the correct view of the given
	 * region to paint afterwards
	 * 
	 * @param regionToPaint The region to paint
	 */
	protected void mergeBuffer(Rectangle regionToPaint) {
		int absDX = bufferRegion.x - regionToPaint.x;
		int absDY = bufferRegion.x - regionToPaint.x;
		int relDX = EditorUtil.toRelative(editPart, absDX);
		int relDY = EditorUtil.toRelative(editPart, absDY);
		if (absDX == 0 && absDY == 0)
			return;
		
		
		if (Math.abs(relDX)>bufferRegion.width || Math.abs(relDY)>bufferRegion.height) {
			// buffer needs complete redraw
			drawChildrenRegionOnBuffer(regionToPaint);
			// set the new buffer region to the region to paint
			bufferRegion.setBounds(regionToPaint);
			return;
		}
		// copy the region already drawn to the right position
		bufferedGraphics.copyArea(
				0 , 0, 
				viewerRegion.width, viewerRegion.height, 
				relDX, relDY
			);
		// now draw the region to paint on the buffer
		Rectangle region1;
		Rectangle region2;
		if (absDX >= 0) {
			// dx pos copy to right 
			region1 = new Rectangle(0, 0, absDX, bufferRegion.height);
			if (absDY >= 0)
				// to bottom
				region2 = new Rectangle(absDX, 0, bufferRegion.width-absDX, absDY);
			else
				// over the top
				region2 = new Rectangle(absDX, bufferRegion.height+absDY, bufferRegion.width-absDX, -absDY);
		}
		else {
			// dx pos copy to left 
			region1 = new Rectangle(bufferRegion.width + absDX, 0, -absDX, bufferRegion.height);
			if (absDY >= 0)
				// to bottom
				region2 = new Rectangle(0, 0, bufferRegion.width+absDX, absDY);
			else
				// over the top
				region2 = new Rectangle(0, bufferRegion.height+absDY, bufferRegion.width+absDX, -absDY);
		}
		drawChildrenRegionOnBuffer(region1);
		drawChildrenRegionOnBuffer(region2);
		// set the new buffer region to the region to paint
		bufferRegion.setBounds(regionToPaint);
	}
	
	/**
	 */
	public void paint(Graphics graphics) {
		if (!(graphics instanceof J2DGraphics)) {
			super.paint(graphics);
			return;
		}
		J2DGraphics j2dGraphics = (J2DGraphics)graphics;
		// create the Graphics where the buffer is drawn on
		Graphics2D g2d = j2dGraphics.createGraphics2D();
		try {
			// TODO: implement Minimal ... paint 
			// check if bufferRegion changed
			Rectangle regionToPaint = EditorUtil.toAbsolute(editPart, viewerRegion);
			if (!bufferRegion.equals(regionToPaint)) {
				// if so, merge actual buffer with the new bufferRegion copyArea ... Smart ...
			}
			
			// do drawImage(bufferedImage);
			double currentZoom = EditorUtil.getZoom(editPart);
			// scale it invers of the current zoom ...
			g2d.scale(1/currentZoom, 1/currentZoom);			
			// and translate it with the current scroll offset
			// so 0,0 will be drawn on top left of the control			
			Point scrollOffset = EditorUtil.getScrollOffset(editPart);
			g2d.translate(scrollOffset.x, scrollOffset.y);
			// now copy the buffer region
			g2d.setPaint(Color.WHITE);
			g2d.fillRect(-2, -2, viewerRegion.width+2, viewerRegion.height+2);
			g2d.drawImage(
					bufferedImage,
					0, 0, viewerRegion.width, viewerRegion.height,
					0, 0, viewerRegion.width, viewerRegion.height,
					null
				);
		} finally {
			g2d.dispose();
		}
		
	}

	/**
	 * @see org.nightlabs.editor2d.figures.BufferedFreeformLayer#refresh()
	 */
	public void refresh() {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure child = (Figure) iter.next();
			if (child instanceof ISmartUpdateFigure) {
				((ISmartUpdateFigure)child).refresh();
			}
		}
	}

	/**
	 * @see BufferedFreeformLayer#refresh(IFigure)
	 */
	public void refresh(IFigure figure) {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure child = (Figure) iter.next();
			if (child instanceof ISmartUpdateFigure) {
				((ISmartUpdateFigure)child).refresh(figure);
			}
		}
	}

	public void dispose() {
		bufferedGraphics.dispose();
		bufferedImage.flush();
		bufferedImage = null;
		bufferedGraphics = null;			
	}
		
}
