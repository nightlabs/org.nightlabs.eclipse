package org.nightlabs.editor2d.figures;

import java.awt.Graphics2D;
import java.util.Iterator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.Renderer;

public class ContainerFreeformLayer 
extends FreeformLayer 
implements RendererFigure
{
	public ContainerFreeformLayer() 
	{
		super.setBounds(new Rectangle(-Integer.MAX_VALUE / 2, -Integer.MAX_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE));
	}
	
	/**
	 * Overridden to paint only children
	 */
	public void paint(Graphics2D graphics) 
	{
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure figure = (Figure) iter.next();
			if (figure instanceof DrawComponentFigure) {
				((DrawComponentFigure)figure).paint(graphics);
			}
		}
	}  
	
	/**
	 * Overridden to paint only children
	 */
	public void paint(Graphics graphics) 
	{
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure figure = (Figure) iter.next();
			figure.paint(graphics);
		}
	}
	
	protected Renderer renderer;   
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}
	
	protected DrawComponent drawComponent;  
	public void setDrawComponent(DrawComponent drawComponent) {
		this.drawComponent = drawComponent;
	}
	
	/**
	 * @see FreeformFigure#setFreeformBounds(Rectangle)
	 */
	public void setFreeformBounds(Rectangle bounds) 
	{
//  	clearBuffer();
//    LOGGER.debug("setFreeformBounds("+bounds+")");
//  	helper.setFreeformBounds(bounds);
	} 
	
	public void setBounds(Rectangle rect) 
	{
//	clearBuffer();
//  LOGGER.debug("setFreeformBounds("+bounds+")");
//	helper.setFreeformBounds(bounds);		
	}
  	
}
