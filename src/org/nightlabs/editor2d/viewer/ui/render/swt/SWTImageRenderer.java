/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.render.swt;

import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
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

	@Override
	public void paint(DrawComponent dc, GC gc) 
	{
    ImageDrawComponent image = (ImageDrawComponent) dc;
    if (image.getImage() != null) {
    	Image img = convertImage(image.getImage());
      gc.drawImage(img, image.getX(), image.getY());    	
    }
	}
	
	protected Image convertImage(BufferedImage img) 
	{
		return AWTSWTUtil.toSWTImage(img, null);
	}	
}
