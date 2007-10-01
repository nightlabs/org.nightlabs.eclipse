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

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

public class ZoomSupport 
implements IZoomSupport
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ZoomSupport.class);
	
	public ZoomSupport(IViewport viewport) {
		super();
		this.viewport = viewport;
		viewport.addPropertyChangeListener(viewportListener);
	}

	private IViewport viewport = null;
	public IViewport getViewport() {
		return viewport;
	}
	public void setViewport(IViewport viewport) {
		this.viewport = viewport;
	}
	
	private Collection<IZoomListener> zoomListener;		
	public Collection<IZoomListener> getZoomListener() 
	{
		if (zoomListener == null)
			zoomListener = new ArrayList<IZoomListener>();
		
		return zoomListener;
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#addZoomListener(org.nightlabs.editor2d.viewer.ui.IZoomListener)
	 */
	public void addZoomListener(IZoomListener zoomListener) {
		getZoomListener().add(zoomListener);
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#removeZoomListener(org.nightlabs.editor2d.viewer.ui.IZoomListener)
	 */
	public void removeZoomListener(IZoomListener zoomListener) {
		getZoomListener().remove(zoomListener);
	}

	protected void fireZoomChanged() 
	{
		for (Iterator<IZoomListener> it = getZoomListener().iterator(); it.hasNext(); ) {
			IZoomListener listener = it.next();
			listener.zoomChanged(zoom);
		}
	}
	
	private double zoom = 1.0;
	
	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#getZoom()
	 */
	public double getZoom() {		
		return zoom;
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#setZoom(double)
	 */
	public void setZoom(double zoomFactor) 
	{
		double oldZoom = zoom;
		zoom = zoomFactor;
		
		if (zoomFactor > getMaxZoom())
			zoom = getMaxZoom();
		
		if (zoomFactor < getMinZoom())
			zoom = getMinZoom();
		
		if (oldZoom != zoom)
			fireZoomChanged();
		
		doZoomAll();
	}

	private double maxZoom = 10.0d;
	
	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#getMaxZoom()
	 */
	public double getMaxZoom() {
		return maxZoom;
	}

	private double minZoom = 0.01d;
	
	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#getMinZoom()
	 */
	public double getMinZoom() {
		return 0;
	}

	private double zoomStep = 0.25d;
	
	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#getZoomStep()
	 */
	public double getZoomStep() {
		return zoomStep;
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#setMaxZoom(double)
	 */
	public void setMaxZoom(double maxZoom) {
		this.maxZoom = maxZoom;
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#setMinZoom(double)
	 */
	public void setMinZoom(double minZoom) {
		this.minZoom = minZoom;
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#setZoomStep(double)
	 */
	public void setZoomStep(double zoomStep) {
		this.zoomStep = zoomStep;
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#canZoomIn()
	 */
	public boolean canZoomIn() 
	{
		return (zoom + zoomStep < maxZoom) ? true : false;			 
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#canZoomOut()
	 */
	public boolean canZoomOut() 
	{
		return (zoom - zoomStep > minZoom) ? true : false;
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#zoomIn()
	 */
	public void zoomIn() 
	{		
		if (canZoomIn()) {
			setZoom(zoom + zoomStep);
		}
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#zoomOut()
	 */
	public void zoomOut() 
	{
		if (canZoomOut()) {
			setZoom(zoom - zoomStep);
		}
	}

	/**
	 * 
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport#getZoomAsString()
	 */
	public String getZoomAsString() 
	{
		double z = Math.rint(zoom * 100);
		return ""+z+" %"; //$NON-NLS-1$ //$NON-NLS-2$
	}
						
	/**
	 * zooms the IViewport of the ZoomSupport to the given Rectangle,
	 * which must have absolute coordinates
	 * This means the scale is calculated and set and the ViewLocation of the
	 * IViewport is adjusted 
	 *  
	 * @param r the rectangle to zoomTo
	 */
	public void zoomTo(Rectangle r) 
	{
		Rectangle absoluteRect = new Rectangle(r);
		Rectangle absoluteView = getViewport().getViewBounds();
				
		double oldZoom = getZoom();
		double zoomX = oldZoom;
		double zoomY = oldZoom;

		if (absoluteView.width != 0 && absoluteRect.width != 0)
			zoomX = (double)absoluteView.width / (double)absoluteRect.width;
		if (absoluteView.height != 0 && absoluteRect.height != 0)
			zoomY = (double)absoluteView.height / (double)absoluteRect.height;
						
	  double newZoom = Math.min(zoomX, zoomY);	 
	  setZoom(newZoom);
	  // maybe realZoom is beyond minZoom or maxZoom
	  double zoom = getZoom();

	  double newX = (double) ((absoluteRect.x) * zoom);
	  double newY = (double) ((absoluteRect.y) * zoom);	  	
	  	  	  	  	  
  	getViewport().setViewLocation((int)newX, (int)newY);
	  		  		
  	if (logger.isDebugEnabled()) {
  		logger.debug("absoluteRect = "+absoluteRect); //$NON-NLS-1$
  		logger.debug("absoluteView = "+absoluteView); //$NON-NLS-1$
  		logger.debug("newZoom = "+newZoom); //$NON-NLS-1$
  		logger.debug("newX = "+newX); //$NON-NLS-1$
  		logger.debug("newY = "+newY); //$NON-NLS-1$
  		logger.debug(""); //$NON-NLS-1$  		
  	}
	}
	
	private boolean zoomAll = true;
	public boolean isZoomAll() {
		return zoomAll;
	}
	
	public void setZoomAll(boolean zoomAll) {
		this.zoomAll = zoomAll;
//		if (zoomAll)
		doZoomAll();
	}			

	public void zoomAll() 
	{
		Rectangle realBounds = getViewport().getInitRealBounds();
		Rectangle viewBounds = getViewport().getInitViewBounds();
		double scaleX = viewBounds.getWidth() / realBounds.getWidth();
		double scaleY = viewBounds.getHeight() / realBounds.getHeight();
		double scale = Math.min(scaleX, scaleY);
		setZoom(scale);
	}
	
	protected void doZoomAll() 
	{
		Display.getDefault().asyncExec(new Runnable() {				
			public void run() {
				if (zoomAll) {
					logger.debug("zoomAll"); //$NON-NLS-1$
					zoomAll();
				}
			}				
		});						
	}	
	
	private PropertyChangeListener viewportListener = new PropertyChangeListener(){	
		public void propertyChange(PropertyChangeEvent evt) {
			doZoomAll();
		}	
	};
}
