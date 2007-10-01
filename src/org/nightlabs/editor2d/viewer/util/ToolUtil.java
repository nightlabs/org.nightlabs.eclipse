/**
 * 
 */
package org.nightlabs.editor2d.viewer.util;

import java.awt.Color;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent.LineStyle;
import org.nightlabs.editor2d.impl.RectangleDrawComponentImpl;
import org.nightlabs.editor2d.impl.ShapeDrawComponentImpl;
import org.nightlabs.editor2d.j2d.GeneralShape;

/**
 * Some Utility Methods for Tools
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class ToolUtil 
{

	protected ToolUtil() {
		super();
	}
	
	/**
	 * creates Feedback DrawComponent
	 * 
	 * @param dc The DrawComponent to create a Feedback for
	 * @param lineColor the lineColor of the Feedback DrawComponent
	 * @param lineWidth the lineWidth of the Feedback DrawComponent
	 * @param fill determines if the Feedback DrawComponent should be filled or not
	 * @param fillColor the fillColor of the Feedback DrawComponent
	 * @param lineStyle the lineStyle of the Feedback DrawComponent
	 * @return a feedback DrawComponent with the given look, if the DrawComponent is null,
	 * null is returned
	 */
	public static DrawComponent createFeedbackDrawComponent(DrawComponent dc, Color lineColor, 
//			int lineWidth, boolean fill, Color fillColor, int lineStyle)
			int lineWidth, boolean fill, Color fillColor, LineStyle lineStyle)			
	{
		if (dc != null) 
		{
			ShapeDrawComponent sdc = null;
			if (dc instanceof ShapeDrawComponent) {
				ShapeDrawComponent original = (ShapeDrawComponent) dc; 
				sdc = new ShapeDrawComponentImpl();
				sdc.setGeneralShape((GeneralShape)original.getGeneralShape().clone());
			}
			else {
				sdc = new RectangleDrawComponentImpl();
				sdc.setGeneralShape(new GeneralShape(dc.getBounds()));
			}
			sdc.setLineWidth(lineWidth);
			sdc.setLineStyle(lineStyle);
			sdc.setFill(fill);
			sdc.setLineColor(lineColor);
			sdc.setFillColor(fillColor);
			return sdc;					
		}
		return null;
	}		
	
	/**
	 * creates not filled Feedback DrawComponent with the given lineColor
	 * 
	 * @param dc the DrawComponent to create a feedback for
	 * @param lineColor the lineColor of the feedback DrawComponent
	 * @return a feedback DrawComponent with the given look, if the DrawComponent is null,
	 * null is returned
	 */
	public static DrawComponent createFeedbackDrawComponent(DrawComponent dc, Color lineColor)
	{
		Color fillColor = Color.RED;
		if (dc instanceof ShapeDrawComponent)
			fillColor = ((ShapeDrawComponent)dc).getFillColor();
		return createFeedbackDrawComponent(dc, lineColor, 10, false, fillColor, LineStyle.SOLID);			
	}
	
	/**
	 * creates not filled Feedback DrawComponent with the given lineColor
	 * 
	 * @param dc the DrawComponent to create a feedback for
	 * @param lineColor the lineColor of the feedback DrawComponent
	 * @param lineWidth the lineWidth of the feedback DrawComponent
	 * @return a feedback DrawComponent with the given look, if the DrawComponent is null,
	 * null is returned
	 */
	public static DrawComponent createFeedbackDrawComponent(DrawComponent dc, Color lineColor, int lineWidth)	{
		Color fillColor = Color.RED;
		if (dc instanceof ShapeDrawComponent)
			fillColor = ((ShapeDrawComponent)dc).getFillColor();
		return createFeedbackDrawComponent(dc, lineColor, lineWidth, false, fillColor, LineStyle.SOLID);			
	}	
	
	/**
	 * creates not filled Feedback DrawComponent with the given lineColor
	 * 
	 * @param dc the DrawComponent to create a feedback for
	 * @param lineColor the lineColor of the feedback DrawComponent
	 * @param lineWidth the lineWidth of the feedback DrawComponent
	 * @param fillColor the fillColor of the feedback DrawComponent
	 * @return a feedback DrawComponent with the given look, if the DrawComponent is null,
	 * null is returned
	 */
	public static DrawComponent createFeedbackDrawComponent(DrawComponent dc, Color lineColor, 
			int lineWidth, Color fillColor)	
	{
		return createFeedbackDrawComponent(dc, lineColor, lineWidth, false, fillColor, LineStyle.SOLID);			
	}		
}
