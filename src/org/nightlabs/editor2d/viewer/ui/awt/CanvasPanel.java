/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.awt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.util.RenderingHintsManager;
import org.nightlabs.editor2d.viewer.ui.DrawComponentPaintable;
import org.nightlabs.editor2d.viewer.ui.ICanvas;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class CanvasPanel 
extends JPanel 
implements ICanvas 
{
	private static final Logger logger = Logger.getLogger(CanvasPanel.class);
	private boolean debugPaint = true;
	private RenderingHintsManager renderingHintsManager = RenderingHintsManager.sharedInstance();
	private DrawComponent dc;

	public CanvasPanel(DrawComponent dc) {
		super();
		this.dc = dc;
	}

	@Override
	public void dispose() {

	}

	@Override
	public double getScale() {
		Graphics2D g2d = (Graphics2D) getGraphics();
		double scaleX = g2d.getTransform().getScaleX();
		double scaleY = g2d.getTransform().getScaleY();
		return Math.min(scaleX, scaleY);
	}

	@Override
	public void setBackground(int red, int green, int blue) {
		setBackground(new Color(red, green, blue));
	}

	@Override
	public void setScale(double scale) {
		getGraphics2D().scale(scale, scale);
	}

	@Override
	public void translateX(float translateX) {
		getGraphics2D().translate(translateX, 0);
	}

	@Override
	public void translateY(float translateY) {
		getGraphics2D().translate(0, translateY);
	}

	protected Graphics2D getGraphics2D() {
		return (Graphics2D) getGraphics();
	}
		
	/**
	 * paints the DrawComponent into the Graphics of the BufferedImage
	 */
	protected void paintDrawComponent(Graphics2D g2d, DrawComponent dc)
	{
		long startTime = 0;
		if (debugPaint)
			startTime = System.currentTimeMillis();
		
		g2d.setRenderingHints(renderingHintsManager.getRenderingHints());
		DrawComponentPaintable.paintDrawComponent(dc, g2d);
		
		if (debugPaint) {
			long endTime = System.currentTimeMillis() - startTime;
			logger.debug("paintDrawComponent took "+endTime+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public void paint(Graphics g) {
		paintDrawComponent(getGraphics2D(), dc);
	}
	
}
