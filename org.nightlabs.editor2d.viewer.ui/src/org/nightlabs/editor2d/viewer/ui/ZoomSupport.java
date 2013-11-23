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

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.widgets.Display;

public class ZoomSupport
implements IZoomSupport
{
	private static final Logger logger = Logger.getLogger(ZoomSupport.class);

	public ZoomSupport(IViewport viewport) {
		this.viewport = viewport;
		viewport.addPropertyChangeListener(viewportListener);
	}

	private IViewport viewport = null;

	@Override
	public IViewport getViewport() {
		return viewport;
	}
	@Override
	public void setViewport(IViewport viewport) {
		this.viewport = viewport;
	}

	private ListenerList zoomListeners = new ListenerList();

	@Override
	public void addZoomListener(IZoomListener zoomListener) {
		zoomListeners.add(zoomListener);
	}

	@Override
	public void removeZoomListener(IZoomListener zoomListener) {
		zoomListeners.remove(zoomListener);
	}

	protected void fireZoomChanged()
	{
		for (Object l : zoomListeners.getListeners()) {
			IZoomListener listener = (IZoomListener) l;
			listener.zoomChanged(zoom);
		}
	}

	private double zoom = 1.0;

	@Override
	public double getZoom() {
		return zoom;
	}

	@Override
	public void setZoom(double zoomFactor)
	{
		if (zoomFactor > getMaxZoom())
			zoomFactor = getMaxZoom();

		if (zoomFactor < getMinZoom())
			zoomFactor = getMinZoom();

		if (Math.abs(zoomFactor - this.zoom) > 0.0001) {
			this.zoom = zoomFactor;
			fireZoomChanged();
		}

		doZoomAll();
	}

	private double maxZoom = 10.0d;

	@Override
	public double getMaxZoom() {
		return maxZoom;
	}

	private double minZoom = 0.01d;

	@Override
	public double getMinZoom() {
		return 0;
	}

	private double zoomStep = 0.25d;

	@Override
	public double getZoomStep() {
		return zoomStep;
	}

	@Override
	public void setMaxZoom(double maxZoom) {
		this.maxZoom = maxZoom;
	}

	@Override
	public void setMinZoom(double minZoom) {
		this.minZoom = minZoom;
	}

	@Override
	public void setZoomStep(double zoomStep) {
		this.zoomStep = zoomStep;
	}

	@Override
	public boolean canZoomIn() {
		return (zoom + zoomStep < maxZoom) ? true : false;
	}

	@Override
	public boolean canZoomOut() {
		return (zoom - zoomStep > minZoom) ? true : false;
	}

	@Override
	public void zoomIn()
	{
		if (canZoomIn()) {
			setZoom(zoom + zoomStep);
		}
	}

	@Override
	public void zoomOut()
	{
		if (canZoomOut()) {
			setZoom(zoom - zoomStep);
		}
	}

	@Override
	public String getZoomAsString()
	{
		double z = Math.rint(zoom * 100);
		return ""+z+" %"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void zoomTo(Rectangle r)
	{
		// if once a zoomTo was performed set zoomAll to false
		zoomAll = false;

		Rectangle absoluteRect = new Rectangle(r);
		Rectangle absoluteView = new Rectangle(getViewport().getViewBounds());

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

		double newX = ((absoluteRect.x) * zoom);
		double newY = ((absoluteRect.y) * zoom);

		getViewport().setViewLocation((int)newX, (int)newY);

		// FIXME: Workaround to avoid strange redraw bugs when zoom to rectangle
		Display.getDefault().timerExec(250, new Runnable(){
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						getViewport().notifyChange();
					}
				});
			}
		});

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

	@Override
	public boolean isZoomAll() {
		return zoomAll;
	}

	@Override
	public void setZoomAll(boolean zoomAll) {
		this.zoomAll = zoomAll;
		doZoomAll();
	}

	@Override
	public void zoomAll()
	{
		double oldScale = getZoom();
		Rectangle realBounds = getViewport().getInitRealBounds();
		Rectangle viewBounds = getViewport().getInitViewBounds();
		double scaleX = viewBounds.getWidth() / realBounds.getWidth();
		double scaleY = viewBounds.getHeight() / realBounds.getHeight();
		double scale = Math.min(scaleX, scaleY);

		double diff = Math.abs(oldScale - scale);
		if (diff > 0.0001 && !Double.isInfinite(diff))
		{
			if (logger.isDebugEnabled()) {
				logger.debug("realBounds = "+realBounds); //$NON-NLS-1$
				logger.debug("viewBounds = "+viewBounds); //$NON-NLS-1$
				logger.debug("scaleX = "+scaleX); //$NON-NLS-1$
				logger.debug("scaleY = "+scaleY); //$NON-NLS-1$
				logger.debug("oldScale = "+oldScale); //$NON-NLS-1$
				logger.debug("scale = "+scale); //$NON-NLS-1$
				logger.debug("diff = "+diff);				 //$NON-NLS-1$
			}
			setZoom(scale);
		}
	}

	private static final ThreadLocal<int[]> doZoomAllReferenceCounter = new ThreadLocal<int[]>() {
		@Override
		protected int[] initialValue() {
			return new int[] { 0 };
		}
	};

	protected void doZoomAll() {
		if (zoomAll) {
			int[] recursionCounter = doZoomAllReferenceCounter.get();
			try {
				if (++recursionCounter[0] > 100) {
					logger.warn("doZoomAll: detected recursion and will exit!");
					return;
				}

				zoomAll();
			} finally {
				if (--recursionCounter[0] <= 0)
					doZoomAllReferenceCounter.remove();
			}
		}
	}

	//	protected void doZoomAll()
	//	{
	////		Display.getDefault().asyncExec(new Runnable() {
	////			public void run() {
	////				if (zoomAll) {
	////					logger.debug("zoomAll"); //$NON-NLS-1$
	//					zoomAll();
	////				}
	////			}
	////		});
	//	}

	private PropertyChangeListener viewportListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();

			if (IViewport.REAL_CHANGE.equals(propertyName) || IViewport.VIEW_CHANGE.equals(propertyName))
				doZoomAll();
		}
	};
}
