/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.awt;

import java.awt.Graphics;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.IBufferedCanvas;
import org.nightlabs.editor2d.viewer.ui.ITempContentManager;
import org.nightlabs.editor2d.viewer.ui.TempContentManager;
import org.nightlabs.editor2d.viewer.ui.util.PaintUtil;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class BufferedCanvas 
extends CanvasPanel
implements IBufferedCanvas
{
	public BufferedCanvas(DrawComponent dc) {
		super(dc);
	}

	private ITempContentManager tempContentManager = new TempContentManager();
	
	@Override
	public ITempContentManager getTempContentManager() {
		return tempContentManager;
	}

	@Override
	public void notifyChange() {
		repaint();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.viewer.ui.awt.CanvasPanel#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		PaintUtil.drawTempContent(getGraphics2D(), tempContentManager);
	}

}
