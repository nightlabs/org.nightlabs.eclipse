/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.render.swt;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Transform;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.render.j2d.J2DRenderContext;
import org.nightlabs.editor2d.render.j2d.J2DShapeDefaultRenderer;
import org.nightlabs.editor2d.viewer.ui.util.AWTSWTUtil;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class SWTImageRenderer
extends SWTBaseRenderer
{

	public SWTImageRenderer()
	{
		super();
	}

//	@Override
//	public void paint(DrawComponent dc, GC gc)
//	{
//		ImageDrawComponent image = (ImageDrawComponent) dc;
//		if (image.getImage() != null) {
//			Image img = convertImage(image.getImage());
//			gc.drawImage(img, image.getX(), image.getY());
//		}
//	}

	@Override
	public void paint(DrawComponent dc, GC gc)
	{
		// TODO FIXME XXX Fix image rotation with GEF
		ImageDrawComponent image = (ImageDrawComponent) dc;
		
		if (image.getImage() != null) {
			Transform gcTransform = new Transform(null);
			gc.getTransform(gcTransform);
			Transform imageTransform = AWTSWTUtil.toSWTTransform(image.getAffineTransform());
//			imageTransform.multiply(gcTransform);
			gc.setTransform(imageTransform);
			Rectangle bounds = image.getOriginalImageShape().getBounds();
			Image img = convertImage(image.getImage());
//			gc.drawImage(img, bounds.x, bounds.y, bounds.width, bounds.height, image.getX(), image.getY(), image.getWidth(), image.getHeight());
			gc.drawImage(img, image.getX(), image.getY());
			gc.setTransform(gcTransform);
		}
	}	
	
//	/**
//	 * @see J2DRenderContext#paint(DrawComponent)
//	 */
//	@Override
//	public void paint(DrawComponent dc, Graphics2D g2d)
//	{
//		ImageDrawComponent image = (ImageDrawComponent) dc;
//		if (image.getImage() != null)
//		{
//			graphicsTransform = g2d.getTransform();
//			at = new AffineTransform(image.getAffineTransform());
//			at.preConcatenate(graphicsTransform);
//			g2d.setTransform(at);
//			Rectangle bounds = image.getOriginalImageShape().getBounds();
//			g2d.drawImage(image.getImage(), bounds.x, bounds.y, bounds.width, bounds.height, null);
//			if (image.isTemplate()) {
//				g2d.setPaint(J2DShapeDefaultRenderer.TEMPLATE_COLOR);
//				g2d.fill(image.getOriginalImageShape());
//			}
//			g2d.setTransform(graphicsTransform);
//		}
//	}
	
	protected Image convertImage(BufferedImage img)
	{
		return AWTSWTUtil.toSWTImage(img, null);
	}
}
