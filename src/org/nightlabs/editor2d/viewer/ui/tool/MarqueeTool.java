/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.tool;

import java.awt.Rectangle;
import java.util.List;

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
		setShowTooltip(false);
	}
		
	@Override
	public void mouseReleased(MouseEvent me)
	{
		super.mouseReleased(me);
		if (leftPressed)
			selectDrawComponents(rect == null ? null : rect.getBounds());

		leftPressed = false;
		rightPressed = false;
		rect = null;
	}

	protected void selectDrawComponents(Rectangle r)
	{
		List drawComponents = null;
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
