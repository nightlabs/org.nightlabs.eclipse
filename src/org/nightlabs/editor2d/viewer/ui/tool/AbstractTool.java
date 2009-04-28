/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.editor2d.viewer.ui.tool;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.IDrawComponentConditional;
import org.nightlabs.editor2d.viewer.ui.IViewer;
import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;
import org.nightlabs.editor2d.viewer.ui.event.MouseListener;
import org.nightlabs.editor2d.viewer.ui.event.MouseMoveListener;
import org.nightlabs.editor2d.viewer.ui.preferences.Preferences;

public abstract class AbstractTool
implements ITool, MouseListener, MouseMoveListener
{
//	private static final Logger logger = Logger.getLogger(AbstractTool.class);

	public static final String ID_DEFAULT = "DefaultToolID";  //$NON-NLS-1$
	private String id = ID_DEFAULT;
	private IViewer viewer = null;
	protected Point startPoint = null;
	protected Point currentPoint = null;
	protected Point deltaPoint = null;
	private IDrawComponentConditional conditional = null;
	private boolean repaintNeeded = false;
	private IToolManager toolManager = null;
	private boolean leftPressed = false;
	private boolean rightPressed = false;

	public boolean isLeftPressed() {
		return leftPressed;
	}

	public boolean isRightPressed() {
		return rightPressed;
	}

	public boolean isRepaintNeeded() {
		return repaintNeeded;
	}

	public void setRepaintNeeded(boolean repaint) {
		this.repaintNeeded = repaint;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void activate() {
		startPoint = new Point();
		currentPoint = new Point();
		deltaPoint = new Point();
	}

	public void deactivate() {
		startPoint = null;
		currentPoint = null;
		deltaPoint = null;
	}

	public void setViewer(IViewer viewer) {
		this.viewer = viewer;
	}

	public IViewer getViewer() {
		return viewer;
	}

	protected void addToTempContent(Object o) {
		if (checkTempContentManager()) {
			getViewer().getBufferedCanvas().getTempContentManager().addToTempContent(o);
			repaintNeeded = true;
		}
	}

	protected boolean checkTempContentManager() {
		return getViewer() != null && getViewer().getBufferedCanvas() != null && getViewer().getBufferedCanvas().getTempContentManager() != null;
	}

	protected boolean removeTempContent(Object o)
	{
		boolean removed = false;
		if (checkTempContentManager()) {
			removed = getViewer().getBufferedCanvas().getTempContentManager().removeFromTempContent(o);
			repaintNeeded = true;
		}
		return removed;
	}

	protected boolean removeManyFromTempContent(Collection<?> objects)
	{
		boolean removed = false;
		if (checkTempContentManager()) {
			removed = getViewer().getBufferedCanvas().getTempContentManager().removeManyFromTempContent(objects);
			repaintNeeded = true;
		}
		return removed;
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		currentPoint.setLocation(me.getX(), me.getY());
		if (isLeftPressed()) {
			deltaPoint.setLocation(me.getX() - startPoint.x, me.getY() - startPoint.y);
		}
		doMouseMoved(me);
	}

	/**
	 * Subclasses can override this method to react on mouseMovement
	 * @param me the {@link MouseEvent}
	 */
	protected void doMouseMoved(MouseEvent me) {
		// Does nothing by default
	}

	@Override
	public void mousePressed(MouseEvent me) {
		if (me.getButton() == java.awt.event.MouseEvent.BUTTON1)
			leftPressed = true;
		if (me.getButton() == java.awt.event.MouseEvent.BUTTON3)
			rightPressed = true;

		startPoint.setLocation(me.getX(), me.getY());
		doMousePressed(me);
	}

	/**
	 * Subclasses can override this method to react on mousePressed
	 * @param me the {@link MouseEvent}
	 */
	protected void doMousePressed(MouseEvent me) {
		// Does nothing by default
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		doMouseReleased(me);
		leftPressed = false;
		rightPressed = false;
	}

	/**
	 * Subclasses can override this method to react on mouseRelease
	 * @param me the {@link MouseEvent}
	 */
	protected void doMouseReleased(MouseEvent me) {
		// Does nothing by default
	}

	protected double getZoom() {
		return getViewer().getZoom();
	}

	protected int getAbsoluteScrollOffsetX() {
		return getViewer().getViewport().getOffsetX();
	}

	protected int getAbsoluteScrollOffsetY() {
		return getViewer().getViewport().getOffsetY();
	}

	protected int getRelativeScrollOffsetX() {
		return (int) Math.rint(getAbsoluteScrollOffsetX() / getZoom());
	}

	protected int getRelativeScrollOffsetY() {
		return (int) Math.rint(getAbsoluteScrollOffsetY() / getZoom());
	}

	public int getRelativeX(int x) {
		return ((int) Math.rint(x / getZoom())) + getRelativeScrollOffsetX();
	}

	public int getRelativeY(int y) {
		return ((int) Math.rint(y / getZoom())) + getRelativeScrollOffsetY();
	}

	public void setConditional(IDrawComponentConditional conditional) {
		this.conditional = conditional;
	}

	public IDrawComponentConditional getConditional() {
		return conditional;
	}

//	public DrawComponent getDrawComponent(int x, int y, IDrawComponentConditional conditional,
//			Collection<DrawComponent> excludeList)
//	{
//		return getViewer().getHitTestManager().findObjectAt(getViewer().getDrawComponent(),
//				x, y, conditional, excludeList);
//	}

	public DrawComponent getDrawComponent(int x, int y, IDrawComponentConditional conditional,
			Collection<DrawComponent> excludeList)
	{
//		long start = System.currentTimeMillis();
		double hitTolerance = Preferences.getPreferenceStore().getDouble(Preferences.PREFERENCE_HIT_TOLERANCE);
		Rectangle2D rect = new Rectangle2D.Double(x - hitTolerance, y - hitTolerance, hitTolerance * 2, hitTolerance * 2);
		List<DrawComponent> dcs = getViewer().getHitTestManager().findObjectsAt(getViewer().getDrawComponent(),
				rect, conditional, excludeList);
		DrawComponent dc = null;
		if (!dcs.isEmpty()) {
			dc = dcs.iterator().next();
		}
//		if (logger.isDebugEnabled()) {
//			long duration = System.currentTimeMillis() - start;
//			logger.debug("x = "+x);
//			logger.debug("y = "+y);
//			logger.debug("hitTolerance = "+hitTolerance);
//			logger.debug("rect = "+rect);
//			logger.debug("found dc = "+dc);
//			logger.debug("hitTest took = "+duration+" ms!");
//		}
		return dc;
	}

	public DrawComponent getDrawComponent(int x, int y)
	{
		return getDrawComponent(x, y, conditional, null);
	}

	@Override
	public IToolManager getToolManager() {
		return toolManager;
	}

	@Override
	public void setToolManager(IToolManager toolManager) {
		this.toolManager = toolManager;
	}

}
