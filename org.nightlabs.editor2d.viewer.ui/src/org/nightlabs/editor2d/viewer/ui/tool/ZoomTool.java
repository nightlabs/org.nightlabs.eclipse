/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.tool;

import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class ZoomTool
extends RectangleTool
{
	public static final String ID = ZoomTool.class.getName();
	
	public ZoomTool()
	{
		super();
		setID(ID);
		setShowRollOver(false);
//		setShowTooltip(false);
	}

	@Override
	protected void doMouseReleased(MouseEvent me)
	{
		super.doMouseReleased(me);
		if (getRectangle() != null && isLeftPressed()) {
			getViewer().getZoomSupport().zoomTo(getRectangle().getBounds());
			getToolManager().setActiveTool(getToolManager().getDefaultTool());	
		}
	}
			
}
