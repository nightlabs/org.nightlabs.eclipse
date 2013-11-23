/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.tool;

import java.awt.Rectangle;
import java.util.List;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class MarqueeTool
extends RectangleTool
{
	public static final String ID = MarqueeTool.class.getName();
	
	public MarqueeTool()
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
		if (isLeftPressed() && getRectangle() != null) {
			selectDrawComponents(getRectangle().getBounds());			
			getToolManager().setActiveTool(getToolManager().getDefaultTool());
		}
	}

	protected void selectDrawComponents(Rectangle r)
	{
		List<DrawComponent> drawComponents = null;
		if (r != null)
			drawComponents = getViewer().getHitTestManager().findObjectsAt(
					getViewer().getDrawComponent(),
					r, getConditional(), null);

		if (drawComponents == null || drawComponents.isEmpty()) {
			getViewer().getSelectionManager().clearSelection(true, true);
		}
		else {
			getViewer().getSelectionManager().clearSelection(false, false);
			getViewer().getSelectionManager().addSelectedDrawComponents(drawComponents);
		}
	}

}
