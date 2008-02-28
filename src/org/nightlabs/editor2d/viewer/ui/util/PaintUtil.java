/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.util;

import java.awt.Component;
import java.awt.Graphics2D;
import java.util.Iterator;

import javax.swing.JToolTip;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.util.RenderUtil;
import org.nightlabs.editor2d.viewer.ui.ITempContentManager;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class PaintUtil 
{
	public static void drawTempContent(Graphics2D g2d, ITempContentManager tempContentManager)
	{
		if (tempContentManager != null && tempContentManager.getTempContent() != null) {
			for (Iterator it = tempContentManager.getTempContent().iterator(); it.hasNext(); )
			{
				Object o = it.next();
				if (o != null) {
					if (o instanceof DrawComponent) {
						DrawComponent dc = (DrawComponent) o;
						Renderer r = null;
						if (dc.getRoot() != null) {
							r = dc.getRenderer();
						}
						if (r == null) {
							String renderMode = dc.getRenderMode();
							r = dc.getRenderModeManager().getRenderer(renderMode, dc.getRenderModeClass().getName());
						}						
						RenderUtil.paintJ2DRenderer(r, dc, g2d);
					}
					else if (o instanceof JToolTip)
					{
						// TODO: find out why Tooltips are not painted at given location
						JToolTip tooltip = (JToolTip) o;
//						tooltip.setComponent(this);
						tooltip.paint(g2d);										
					}
					else if (o instanceof Component) {
						Component c = (Component) o;
						c.paint(g2d);
					}
				}
			}
		}
	}
}
