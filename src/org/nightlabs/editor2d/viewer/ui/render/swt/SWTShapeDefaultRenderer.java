/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.render.swt;

import java.awt.Shape;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.nightlabs.base.ui.util.ColorUtil;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent.LineStyle;
import org.nightlabs.editor2d.viewer.ui.util.AWTSWTUtil;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class SWTShapeDefaultRenderer 
extends SWTBaseRenderer 
{

	public SWTShapeDefaultRenderer() 
	{
		super();
	}

	@Override
	public void paint(DrawComponent dc, GC g) 
	{
    ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
    Path path = convertShape(sdc.getGeneralShape());
    if (sdc.isFill()) {
      g.setBackground(ColorUtil.toSWTColor(sdc.getFillColor()));
      g.fillPath(path);
    }
    g.setForeground(ColorUtil.toSWTColor(sdc.getLineColor()));
//    g.setLineWidth(sdc.getLineWidth());
    g.setLineWidth((int)sdc.getLineWidth());    
    g.setLineStyle(convertLineStyle(sdc.getLineStyle()));
    g.drawPath(path);   
	}
	
	protected Path convertShape(Shape s) 
	{
		return AWTSWTUtil.convertShape(s, null, null);
	}
	 
//	protected int convertLineStyle(int lineStyle) 
//	{
//		return lineStyle;
//	}
	protected int convertLineStyle(LineStyle lineStyle) 
	{
		switch (lineStyle) 
		{
			case SOLID:
				return 1;
			case DASHED_1:
				return 2;
			case DASHED_2:
				return 3;
			case DASHED_3:
				return 4;
			case DASHED_4:
				return 5;
			default:
				return 1;
		}
	}	
	
}
