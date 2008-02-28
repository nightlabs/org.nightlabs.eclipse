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
extends BaseTool
{
	public RectangleTool() {
		super();
	}

	private RectangleDrawComponent rect;
	private boolean rectAdded = false;
	private boolean showRectangle = true;
	
	public boolean isShowRectangle() {
		return showRectangle;
	}
	public void setShowRectangle(boolean showRectangle) {
		this.showRectangle = showRectangle;
	}

	protected RectangleDrawComponent getRectangle() {
		return rect;
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
		rect = null;
	}
	
	protected void initRectangle()
	{
		rect = new RectangleDrawComponentImpl();
		Rectangle r = new Rectangle(0, 0, 10, 10);
		rect.setGeneralShape(new GeneralShape(r));
		rect.setLineColor(Color.BLACK);
		rect.setFill(false);
		rect.setLineWidth(2);
		rect.setLineStyle(LineStyle.DASHED_1);
	}
	
	protected boolean isRectangleRequirementFulfilled(MouseEvent me) {
		return (showRectangle && isLeftPressed());
	}
	
	@Override
	public void mouseMoved(MouseEvent me)
	{
		super.mouseMoved(me);
		if (isRectangleRequirementFulfilled(me)) {
			drawRectangle();
		}
	}
	
	@Override
	protected void doMouseReleased(MouseEvent me)
	{
		super.doMouseReleased(me);
		if (isRectangleRequirementFulfilled(me)) {
			rectAdded = false;
			removeTempContent(rect);
//			rect = null;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent me) {
		super.mouseReleased(me);
		rect = null;
	}
	
	protected void drawRectangle() 
	{
		int currentX = getRelativeX(currentPoint.x);
		int currentY = getRelativeY(currentPoint.y);
		int startX = getRelativeX(startPoint.x);
		int startY = getRelativeY(startPoint.y);
		int width = Math.abs(currentX - startX);
		int height = Math.abs(currentY - startY);

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
		
//		// TODO: avoid multiple repaints
//		repaint();
		setRepaintNeeded(true);
	}
}
