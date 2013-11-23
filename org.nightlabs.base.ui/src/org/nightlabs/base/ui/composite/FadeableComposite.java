/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.ui.composite;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.compatibility.Compatibility;
import org.nightlabs.eclipse.compatibility.CompatibleSWT;

/**
 * This composite grays out if {@link #setFaded(boolean)} is called with value <tt>true</tt>.
 * You should use it in order to give the user a feed-back while data
 * is loaded (or other time-consuming action is performed) and therefore, the composite (incl.
 * contents) is temporarily unavailable.
 *
 * @author Niklas Schiffler <nick@nightlabs.de>
 * @author marco schulze - marco at nightlabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FadeableComposite extends XComposite implements Fadeable
{
	public static final boolean FADE_WITH_IMAGE = true;

	/**
	 * key: {@link Control} control<br/>
	 * value: Boolean enabled
	 */
	private Map<Control, Boolean> controlEnabled = new HashMap<Control, Boolean>();

	private boolean getControlOriginalEnabled(Control control)
	{
		Boolean b = controlEnabled.get(control);
		if (b == null)
			return true;
		else
			return b.booleanValue();
	}

	private void setControlOriginalEnabled(Control control, boolean enabled)
	{
		if (!controlEnabled.containsKey(control))
			control.addDisposeListener(childDisposeListener);

		controlEnabled.put(control, Boolean.valueOf(enabled));
	}

	private DisposeListener childDisposeListener = new DisposeListener() {
		public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
			controlEnabled.remove(e.getSource());
		}
	};

	private int fadeCounter = 0;

	private class MyPaintListener implements PaintListener
	{
//		private Image img;
//		public MyPaintListener(Image img)
//		{
//			this.img = img;
//		}

//		/**
//		 * horizontal distance between dots
//		 */
//		private int gridX = 4;
//		/**
//		 * vertical distance between dots
//		 */
//		private int gridY = 4;

		public void paintControl(PaintEvent event)
		{
			GC gc = event.gc;
			Rectangle rect = gc.getClipping();

			gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
//			gc.setAlpha(127);
			// make grey out less opaque in order to make texts more readable.
			gc.setAlpha(0);
			gc.fillRectangle(rect);

//			for(int i = 0; i < rect.width; i += img.getImageData().width)
//				for(int j = 0; j < rect.height; j += img.getImageData().height)
//						gc.drawImage(img, i, j);

//			Control control = (Control) event.getSource();
//			Point controlAbsoluteCoordinate = control.toDisplay(0, 0);
//			int shiftX = controlAbsoluteCoordinate.x;
//			int shiftY = controlAbsoluteCoordinate.y;
//
			// NEVER create Color objects on the fly!!! See http://www.eclipse.org/articles/SWT%20Color%20Model/swt-color-model.htm
			// gc.setForeground(new Color(Display.getCurrent(), 0x99, 0x99, 0x99));
//			gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
//			for(int x = 0; x < rect.width; x++) {
//				if ((shiftX + x) % gridX == 0) {
//					for(int y = 0; y < rect.height; y++) {
//						if ((shiftY + y) % gridY == 0)
//							gc.drawPoint(x, y);
//					}
//				}
//			}
		}
	}

	private MyPaintListener pl = new MyPaintListener();

	/**
	 * @see XComposite#XComposite(Composite, int)
	 */
	public FadeableComposite(Composite parent, int style)
	{
		super(parent, style);
	}

	/**
	 * @see XComposite#XComposite(Composite, int)
	 */
	public FadeableComposite(Composite parent, int style, LayoutMode layoutMode)
	{
		super(parent, style, layoutMode);
	}

	public FadeableComposite(Composite parent, int style, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutDataMode);
	}

	public FadeableComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode, int cols) {
		super(parent, style, layoutMode, layoutDataMode, cols);
	}

	public FadeableComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
	}

	private Control focusControlAfterFade = null;
	private Control focusControlBeforeFade = null;

	/**
	 * This method must be called on the SWT GUI thread!
	 * <p>
	 * This method is nestable. That means, there is a counter and if you call
	 * <tt>setFaded(true)</tt> for example twice, one call to <tt>setFaded(false)</tt> will
	 * not yet reset the faded status, but only the second call. This allows multiple
	 * jobs to independently control a <tt>FadeableComposite</tt>'s faded status.
	 *
	 * @param faded Whether to fade (gray out) this composite.
	 */
	public void setFaded(boolean faded)
	{
		if (isDisposed())
			return;
		boolean beforeFade = faded && !isFaded();
		boolean beforeUnfade = !faded && isFaded();

		if (beforeFade)
		{
			focusControlBeforeFade = Display.getCurrent().getFocusControl();
		}

		_setFaded(faded);

		if (beforeFade)
		{
			focusControlAfterFade = Display.getCurrent().getFocusControl();
		}
		if (beforeUnfade)
		{
			if (Display.getCurrent().getFocusControl() == focusControlAfterFade)
			{
				if (focusControlBeforeFade != null && !focusControlBeforeFade.isDisposed())
				{
					focusControlBeforeFade.setFocus();
				}
				focusControlBeforeFade = null;
				focusControlAfterFade = null;
			}
		}
	}

	/**
	 * This method does the actual fading/unfading and fadeCounter management.
	 * It is used for recursions as the top level call to {@link #setFaded(boolean)}
	 * has to manage focus only once.
	 *
	 * @param faded Whether to fade (gray out) this composite.
	 */
	private void _setFaded(boolean faded)
	{
		if(faded == true)
		{
			if(fadeCounter == 0)
			{
				fade(this);
				this.redraw(0, 0, this.getBounds().width, this.getBounds().height, true);
			}
			fadeCounter++;
		}
		else
		{
			if(fadeCounter == 1)
			{
				unfade(this);
				this.redraw(0, 0, this.getBounds().width, this.getBounds().height, true);
			}
			if(fadeCounter > 0)
				fadeCounter--;
		}
	}

	public boolean isFaded()
	{
		return fadeCounter > 0;
	}

	private void fade(Control c)
	{
		if (c.isDisposed())
			return;
		setControlOriginalEnabled(c, c.isEnabled());

		if(c instanceof Composite)
		{
			Control[] cs = ((Composite)c).getChildren();
			for(int i = 0; i < cs.length; i++) {
				Control ctl = cs[i];
				if (ctl instanceof FadeableComposite)
					((FadeableComposite)ctl)._setFaded(true);
				else
					fade(ctl);
			}
		}

		c.setEnabled(false);
		
		if (FADE_WITH_IMAGE && Compatibility.isRCP)
			CompatibleSWT.addPaintListener(c, pl);
	}

	private void unfade(Control c)
	{
		if (c.isDisposed())
			return;
		c.setEnabled(getControlOriginalEnabled(c));

		if(c instanceof Composite)
		{
			Control[] cs = ((Composite)c).getChildren();
			for(int i = 0; i < cs.length; i++) {
				Control ctl = cs[i];
				if (ctl instanceof FadeableComposite)
					((FadeableComposite)ctl)._setFaded(false);
				else
					unfade(ctl);
			}
		}

		if (FADE_WITH_IMAGE && Compatibility.isRCP)
			CompatibleSWT.removePaintListener(c, pl);
	}
}
