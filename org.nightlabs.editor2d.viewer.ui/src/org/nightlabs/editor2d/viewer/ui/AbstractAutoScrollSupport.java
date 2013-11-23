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

package org.nightlabs.editor2d.viewer.ui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.nightlabs.editor2d.viewer.ui.preferences.Preferences;

/**
 * This is an abstract implementation of a generic AutoScrollSupport
 *
 * @author Daniel.Mazurek [dot] NightLabs [dot] de
 *
 */
public abstract class AbstractAutoScrollSupport
implements IAutoScrollSupport
{
	public static final int DEFAULT_SCROLL_STEP =
		Preferences.getPreferenceStore().getDefaultInt(Preferences.PREFERENCE_SCROLL_STEP);
	public static final int DEFAULT_SCROLL_TOLERANCE =
		Preferences.getPreferenceStore().getDefaultInt(Preferences.PREFERENCE_SCROLL_TOLERANCE);
	public static final int DEFAULT_TIMER_DELAY =
		Preferences.getPreferenceStore().getDefaultInt(Preferences.PREFERENCE_TIMER_DELAY);

	private int scrollStep = DEFAULT_SCROLL_STEP;
	private int scrollTolerance = DEFAULT_SCROLL_TOLERANCE;
	private int timerDelay = DEFAULT_TIMER_DELAY;

	private ActionListener scrollRight = new ActionListener()
	{
		public void actionPerformed(ActionEvent e) {
			doScrollRight(scrollStep);
		}
	};

	private ActionListener scrollLeft = new ActionListener()
	{
		public void actionPerformed(ActionEvent e) {
			doScrollLeft(scrollStep);
		}
	};

	private ActionListener scrollUp = new ActionListener()
	{
		public void actionPerformed(ActionEvent e) {
			doScrollUp(scrollStep);
		}
	};

	private ActionListener scrollDown = new ActionListener()
	{
		public void actionPerformed(ActionEvent e) {
			doScrollDown(scrollStep);
		}
	};

	private Timer timerLeft = new Timer(timerDelay, scrollLeft);
	private Timer timerRight = new Timer(timerDelay, scrollRight);
	private Timer timerUp = new Timer(timerDelay, scrollUp);
	private Timer timerDown = new Timer(timerDelay, scrollDown);

	private Rectangle upperScrollArea = null;
	private Rectangle bottomScrollArea = null;
	private Rectangle leftScrollArea = null;
	private Rectangle rightScrollArea = null;

	private boolean isInTop = false;
	private boolean isInBottom = false;
	private boolean isInLeft = false;
	private boolean isInRight = false;

	/**
	 *
	 * @param step the value which will be scrolled by each timerDelay
	 */
	public void setScrollStep(int step) {
		scrollStep = step;
	}

	/**
	 *
	 * @return the value which will be scrolled by each timerDelay
	 */
	public int getScrollStep() {
		return scrollStep;
	}

	/**
	 *
	 * @param scrollTolerance the value which determines size
	 * of the area in which autoScrolling begins
	 */
	public void setScrollTolerance(int scrollTolerance) {
		this.scrollTolerance = scrollTolerance;
	}

	/**
	 *
	 * @return the value which determines size
	 * of the area in which autoScrolling begins
	 */
	public int getScrollTolerance() {
		return scrollTolerance;
	}

	/**
	 *
	 * @param timerDelay determines the amount of time (milliseconds) which passes each time
	 * the AutoScrollTimer for the repsective direction is triggered
	 */
	public void setTimerDelay(int timerDelay) {
		this.timerDelay = timerDelay;
		timerDown.setDelay(timerDelay);
		timerUp.setDelay(timerDelay);
		timerLeft.setDelay(timerDelay);
		timerRight.setDelay(timerDelay);
	}

	/**
	 *
	 * @return determines the amount of time (milliseconds) which passes each time
	 * the AutoScrollTimer for the repsective direction is triggered
	 */
	public int getTimerDelay() {
		return timerDelay;
	}

	protected void initAutoScroll(Rectangle bounds)
	{
		int width = bounds.width;
		int height = bounds.height;
		upperScrollArea = new Rectangle(0, 0, width, scrollTolerance);
		bottomScrollArea = new Rectangle(0, height-scrollTolerance, width, scrollTolerance);
		leftScrollArea = new Rectangle(0, 0, scrollTolerance, height);
		rightScrollArea = new Rectangle(width - scrollTolerance, 0, scrollTolerance, height);
	}

//	protected abstract void scrollDown(int scrollStep);
//	protected abstract void scrollUp(int scrollStep);
//	protected abstract void scrollLeft(int scrollStep);
//	protected abstract void scrollRight(int scrollStep);

	protected abstract ICanvas getCanvas();

	protected void scrollDown(int scrollStep) {
		getCanvas().translateY(scrollStep);
		getCanvas().repaint();
	}

	protected void scrollUp(int scrollStep) {
		getCanvas().translateY(-scrollStep);
		getCanvas().repaint();
	}

	protected void scrollLeft(int scrollStep) {
		getCanvas().translateX(-scrollStep);
		getCanvas().repaint();
	}

	protected void scrollRight(int scrollStep) {
		getCanvas().translateX(scrollStep);
		getCanvas().repaint();
	}

	protected boolean doMouseMoved(int x, int y, boolean b, Rectangle scrollArea, Timer timer)
	{
		if (!scrollArea.contains(x,y)) {
			if (b == true) {
				b = false;
				timer.stop();
			}
		}
		else {
			if (b == false) {
				b = true;
				timer.restart();
			}
		}
		return b;
	}

	protected void mouseMoved(int x, int y)
	{
		isInTop = doMouseMoved(x, y, isInTop, upperScrollArea, timerUp);
		isInBottom = doMouseMoved(x, y, isInBottom, bottomScrollArea, timerDown);
		isInLeft = doMouseMoved(x, y, isInLeft, leftScrollArea, timerLeft);
		isInRight = doMouseMoved(x, y, isInRight, rightScrollArea, timerRight);
	}

	protected void mouseExited()
	{
		if (isInTop) {
			isInTop = false;
			timerUp.stop();
		}
		if (isInBottom) {
			isInBottom = false;
			timerDown.stop();
		}
		if (isInRight) {
			isInRight = false;
			timerRight.stop();
		}
		if (isInLeft) {
			isInLeft = false;
			timerLeft.stop();
		}
	}

	protected void stopTimers()
	{
		timerLeft.stop();
		timerRight.stop();
		timerUp.stop();
		timerDown.stop();
	}

	protected void doScrollDown(final int scrollStep) {
		scrollDown(scrollStep);
	}

	protected void doScrollUp(final int scrollStep) {
		scrollUp(scrollStep);
	}

	protected void doScrollLeft(final int scrollStep) {
		scrollLeft(scrollStep);
	}

	protected void doScrollRight(final int scrollStep){
		scrollRight(scrollStep);
	}

	public void dispose()
	{
		timerDown.removeActionListener(scrollDown);
		timerUp.removeActionListener(scrollUp);
		timerLeft.removeActionListener(scrollLeft);
		timerRight.removeActionListener(scrollRight);

		timerDown = null;
		timerUp = null;
		timerLeft = null;
		timerRight = null;

		bottomScrollArea = null;
		upperScrollArea = null;
		leftScrollArea = null;
		rightScrollArea = null;
	}
}
