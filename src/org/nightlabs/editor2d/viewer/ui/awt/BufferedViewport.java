/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.awt;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.IBufferedViewport;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class BufferedViewport 
extends BufferedCanvas
implements IBufferedViewport
{
	private Rectangle initViewBounds;
	private Rectangle initRealBounds;	
	private Rectangle realBounds;
	private Rectangle viewBounds;
	
	/**
	 * @param dc
	 */
	public BufferedViewport(DrawComponent dc) {
		super(dc);
		init(dc.getBounds());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#getInitRealBounds()
	 */
	@Override
	public Rectangle getInitRealBounds() {
		return initRealBounds;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#getInitViewBounds()
	 */
	@Override
	public Rectangle getInitViewBounds() {
		return initViewBounds;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#getOffsetX()
	 */
	@Override
	public int getOffsetX() {
		return viewBounds.x - realBounds.x;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#getOffsetY()
	 */
	@Override
	public int getOffsetY() {
		return viewBounds.y - realBounds.y;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#getRealBounds()
	 */
	@Override
	public Rectangle getRealBounds() {
		return realBounds;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#getViewBounds()
	 */
	@Override
	public Rectangle getViewBounds() {
		return viewBounds;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#getViewLocation()
	 */
	@Override
	public Point2D getViewLocation() {
		return getViewBounds().getLocation();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#setRealBounds(java.awt.Rectangle)
	 */
	@Override
	public void setRealBounds(Rectangle realBounds) {
		this.realBounds = realBounds;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#setViewBounds(java.awt.Rectangle)
	 */
	@Override
	public void setViewBounds(Rectangle viewBounds) {
		this.viewBounds = viewBounds;
	}

	public void setViewLocation(int x, int y) {
		setViewBounds(new Rectangle(x, y, viewBounds.width, viewBounds.height));
	}
	
	public void setViewLocation(Point2D p) {
		setViewLocation((int)p.getX(), (int)p.getY());
	}

	protected void init(Rectangle realBounds)
	{
		initRealBounds = new Rectangle(realBounds);
		initViewBounds = getVisibleRect();		
		this.realBounds = realBounds;
		viewBounds = getVisibleRect();
	}	
}
