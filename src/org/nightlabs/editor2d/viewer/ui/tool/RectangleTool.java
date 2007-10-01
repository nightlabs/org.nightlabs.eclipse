/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.tool;

import java.awt.Color;
import java.awt.Rectangle;

import org.nightlabs.editor2d.RectangleDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent.LineStyle;
import org.nightlabs.editor2d.impl.RectangleDrawComponentImpl;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public abstract class RectangleTool 
//extends AbstractTool 
extends BaseTool
{

	public RectangleTool() {
		super();
	}

	protected RectangleDrawComponent rect;		
	private boolean rectAdded = false;

	private boolean showRectangle = true;	
	public boolean isShowRectangle() {
		return showRectangle;
	}
	public void setShowRectangle(boolean showRectangle) {
		this.showRectangle = showRectangle;
	}

	@Override
	public void activate() 
	{
		super.activate();
		if (showRectangle)
			initRectangle();
	}

	@Override	
	public void deactivate() 
	{
		super.deactivate();
		if (showRectangle)
			rect = null;
	}
	
	protected RectangleDrawComponent initRectangle() 
	{
		rect = new RectangleDrawComponentImpl();
		Rectangle r = new Rectangle(0, 0, 10, 10);
		rect.setGeneralShape(new GeneralShape(r));
		rect.setLineColor(Color.BLACK);
		rect.setFill(false);
		rect.setLineWidth(2);
//		rect.setLineStyle(2);	
		rect.setLineStyle(LineStyle.DASHED_1);		
		return rect;
	}	
	
	@Override
	public void mouseMoved(MouseEvent me) 
	{
		super.mouseMoved(me);

		if (showRectangle) 
		{
			int currentX = getRelativeX(currentPoint.x);
			int currentY = getRelativeY(currentPoint.y);
			
			// Draw Rectangle
			if (leftPressed) 
			{
				int startX = getRelativeX(startPoint.x);
				int startY = getRelativeY(startPoint.y);		
				int width = (int) Math.abs(currentX - startX);
				int height = (int) Math.abs(currentY - startY);

				if (!rectAdded) {
					initRectangle();
					rect.setLocation(startX, startY);
					addToTempContent(rect);	
					rectAdded = true;
				}

				if (startX > currentX)
					rect.setX(currentX);
				if (width != 0)
					rect.setWidth(width);
				
				if (startY > currentY)
					rect.setY(currentY);
				if (height != 0)
					rect.setHeight(height);
				
				// TODO: avoid multiple repaints 				
				repaint();							
			}					
		}
	}
	
	@Override	
	public void mouseReleased(MouseEvent me) 
	{
		super.mouseReleased(me);	
		if (showRectangle) {
			rectAdded = false;
			removeTempContent(rect);			
		}		
//		leftPressed = false;
//		rightPressed = false;
//		rect = null;
	}
	
}
