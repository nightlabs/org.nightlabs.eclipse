/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.tool;

import java.awt.Color;

import javax.swing.JToolTip;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;
import org.nightlabs.editor2d.viewer.ui.util.ToolUtil;


public class BaseTool
extends AbstractTool
{
	protected DrawComponent rollOverDC = null;
	private JToolTip toolTip = null;
	
	private boolean showRollOver = true;
	public void setShowRollOver(boolean b) {
		this.showRollOver = b;
	}
	public boolean isShowRollOver() {
		return showRollOver;
	}
	
	private boolean showTooltip = true;
	public boolean isShowTooltip() {
		return showTooltip;
	}
	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}
	
	@Override
	public void deactivate()
	{
		super.deactivate();
		
		if (showRollOver)
			rollOverDC = null;
		
		if (showTooltip)
			toolTip = null;
		
		mouseMovedDC = null;
	}
		
	protected DrawComponent mouseMovedDC = null;
	
	@Override
	public void mouseMoved(MouseEvent me)
	{
		super.mouseMoved(me);

		int currentX = getRelativeX(currentPoint.x);
		int currentY = getRelativeY(currentPoint.y);
				
		if (showRollOver) {
			removeTempContent(rollOverDC);
			rollOverDC = null;
		}
		
		if (showTooltip) {
			removeTempContent(toolTip);
			toolTip = null;
		}

		// TODO: dont hitTest on each mouse move
		mouseMovedDC = getViewer().getHitTestManager().findObjectAt(
				getViewer().getDrawComponent(), currentX, currentY, getConditional(), null);
		
		if (showRollOver)
		{
			if (mouseMovedDC != null) {
				rollOverDC = createRollOverDrawComponent(mouseMovedDC);
				if (rollOverDC != null)
					addToTempContent(rollOverDC);
			}
		}

		if (showTooltip) {
			toolTip = createToolTip(mouseMovedDC, null, me.getX(), me.getY());
			addToTempContent(toolTip);
		}
		
		if (showTooltip || showRollOver) {
			// TODO: avoid multiple repaints
			repaint();
		}
	}
						
	
	protected DrawComponent createRollOverDrawComponent(DrawComponent dc)
	{
		return ToolUtil.createFeedbackDrawComponent(dc, Color.BLACK, 5);
	}
		
	protected JToolTip createToolTip(DrawComponent dc, String text, int x, int y)
	{
		JToolTip toolTip = new JToolTip();
		toolTip.setLocation(x, y);
		toolTip.setSize(200, 200);

		if (text == null)
		{
			if (dc != null) {
				toolTip = new JToolTip();
				toolTip.setTipText(
						Messages.getString("org.nightlabs.editor2d.viewer.ui.tool.BaseTool.tooltip.name")+": "+dc.getName() + "\n" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						Messages.getString("org.nightlabs.editor2d.viewer.ui.tool.BaseTool.tooltip.id")+": "+dc.getId());				 //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else {
			toolTip.setTipText(text);
		}
		return toolTip;
	}
}

