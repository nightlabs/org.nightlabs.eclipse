/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.composite;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Text;

public class TimerText extends XComposite
{
	private Text text;

	protected class DelayTimer extends Timer {
		protected TimerTask timerTask = null;

		public DelayTimer(String name)
		{
			super(name);
		}
	}

	private DelayTimer timer;

	public DelayTimer getTimer()
	{
		return timer;
	}

	public TimerText(Composite parent, int style)
	{
		this(parent, style, null);
	}

	public TimerText(Composite parent, int style, TimerText otherTimerTextToSynchronizeWith)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		getGridData().grabExcessVerticalSpace = false;
		text = new Text(this, style);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (otherTimerTextToSynchronizeWith == null)
			timer = new DelayTimer(this.toString());
		else
			timer = otherTimerTextToSynchronizeWith.timer;

		text.addModifyListener(modifyListener);
	}
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
			ModifyEvent event = null;
			synchronized (delayedModifyListenersMutex) {
				if ((delayedModifyListeners != null && !delayedModifyListeners.isEmpty()) ||
						(modifyListeners != null && !modifyListeners.isEmpty())) {

					Event ev = new Event();
					ev.data = e.data;
					ev.display = e.display;
					ev.time = e.time;
					ev.widget = TimerText.this;
					event = new ModifyEvent(ev);
				}
			}
			final ModifyEvent timerModifyEvent = event;

			synchronized(timer) {
				if (timer.timerTask != null) {
					timer.timerTask.cancel();
					timer.timerTask = null;
				}

				if (event != null) {
					timer.timerTask = new TimerTask() {
						public void run()
						{
							synchronized (delayedModifyListenersMutex) {
								if (delayedModifyListeners == null)
									return;
	
								Object[] listeners = delayedModifyListeners.getListeners();
								for (int i = 0; i < listeners.length; ++i) {
									ModifyListener l = (ModifyListener) listeners[i];
									l.modifyText(timerModifyEvent);
								}
							}
	
							synchronized (timer) {
								timer.timerTask = null;
							}
						}
					};
					timer.schedule(timer.timerTask, 500);
				} // if (event != null) {
			} // synchronized(timer) {

			if (event == null || modifyListeners == null)
				return;

			Object[] listeners = modifyListeners.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
				ModifyListener l = (ModifyListener) listeners[i];
				l.modifyText(event);
			}
		};
	};

	private ListenerList modifyListeners = null;

	public void addModifyListener(ModifyListener listener)
	{
		if (listener == null)
			throw new NullPointerException("listener"); //$NON-NLS-1$

		if (modifyListeners == null)
			modifyListeners = new ListenerList();

		modifyListeners.add(listener);
	}

	public void removeModifyListener(ModifyListener listener)
	{
		if (listener == null)
			throw new NullPointerException("listener"); //$NON-NLS-1$

		if (modifyListeners == null)
			return;

		modifyListeners.remove(listener);
	}

	private ListenerList delayedModifyListeners = null;
	private Object delayedModifyListenersMutex = new Object();

	/**
	 * This method adds a <tt>ModifyListener</tt> which will not be triggered
	 * immediately, but after a certain delay. If a new modification happens
	 * during this delay, the first {@link org.eclipse.swt.events.ModifyEvent}
	 * will be discarded and the delay "countdown" is restarted.
	 * <p>
	 * Warning: This listener will be triggered on a worker thread (NOT the
	 * SWT GUI thread)!
	 *
	 * @param listener The listener to add.
	 */
	public void addDelayedModifyListener(ModifyListener listener)
	{
		if (listener == null)
			throw new NullPointerException("listener"); //$NON-NLS-1$

		synchronized (delayedModifyListenersMutex) {
			if (delayedModifyListeners == null)
				delayedModifyListeners = new ListenerList();
	
			delayedModifyListeners.add(listener);
		}
	}

	public void removeDelayedModifyListener(ModifyListener listener)
	{
		if (listener == null)
			throw new NullPointerException("listener"); //$NON-NLS-1$

		synchronized (delayedModifyListenersMutex) {
			if (delayedModifyListeners == null)
				return;
	
			delayedModifyListeners.remove(listener);
		}
	}



/////////////////////////////////////////////////////////////////////////////////
//// All following methods are delegate methods to text. Add more, if necessary.
/////////////////////////////////////////////////////////////////////////////////

	/**
	 * @see org.eclipse.swt.widgets.Text#append(java.lang.String)
	 */
	public void append(String string)
	{
		text.append(string);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#clearSelection()
	 */
	public void clearSelection()
	{
		text.clearSelection();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#copy()
	 */
	public void copy()
	{
		text.copy();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#cut()
	 */
	public void cut()
	{
		text.cut();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getBackground()
	 */
	public Color getBackground()
	{
		return text.getBackground();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getDoubleClickEnabled()
	 */
	public boolean getDoubleClickEnabled()
	{
		return text.getDoubleClickEnabled();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getEchoChar()
	 */
	public char getEchoChar()
	{
		return text.getEchoChar();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getEditable()
	 */
	public boolean getEditable()
	{
		return text.getEditable();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getEnabled()
	 */
	public boolean getEnabled()
	{
		return text.getEnabled();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getFont()
	 */
	public Font getFont()
	{
		return text.getFont();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getForeground()
	 */
	public Color getForeground()
	{
		return text.getForeground();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getMenu()
	 */
	public Menu getMenu()
	{
		return text.getMenu();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getMonitor()
	 */
	public Monitor getMonitor()
	{
		return text.getMonitor();
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#getStyle()
	 */
	public int getStyle()
	{
		return text.getStyle();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getTabs()
	 */
	public int getTabs()
	{
		return text.getTabs();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getText()
	 */
	public String getText()
	{
		return text.getText();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getText(int, int)
	 */
	public String getText(int start, int end)
	{
		return text.getText(start, end);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getTextLimit()
	 */
	public int getTextLimit()
	{
		return text.getTextLimit();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getToolTipText()
	 */
	public String getToolTipText()
	{
		return text.getToolTipText();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#isFocusControl()
	 */
	public boolean isFocusControl()
	{
		return text.isFocusControl();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#selectAll()
	 */
	public void selectAll()
	{
		text.selectAll();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	public void setBackground(Color color)
	{
		if (text != null)
			text.setBackground(color);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setForeground(org.eclipse.swt.graphics.Color)
	 */
	public void setForeground(Color color)
	{
		if (text != null)
			text.setForeground(color);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setEditable(boolean)
	 */
	public void setEditable(boolean editable)
	{
		text.setEditable(editable);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		text.setEnabled(enabled);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setFocus()
	 */
	public boolean setFocus()
	{
		return text.setFocus();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setMenu(org.eclipse.swt.widgets.Menu)
	 */
	public void setMenu(Menu menu)
	{
		text.setMenu(menu);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setSelection(int, int)
	 */
	public void setSelection(int start, int end)
	{
		text.setSelection(start, end);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setSelection(int)
	 */
	public void setSelection(int start)
	{
		text.setSelection(start);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setSelection(org.eclipse.swt.graphics.Point)
	 */
	public void setSelection(Point selection)
	{
		text.setSelection(selection);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setText(java.lang.String)
	 */
	public void setText(String string)
	{
		if (text.getText().equals(string))
			return;

		text.setText(string);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setTextLimit(int)
	 */
	public void setTextLimit(int limit)
	{
		text.setTextLimit(limit);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setToolTipText(java.lang.String)
	 */
	public void setToolTipText(String string)
	{
		text.setToolTipText(string);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#showSelection()
	 */
	public void showSelection()
	{
		text.showSelection();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#traverse(int)
	 */
	public boolean traverse(int traversal)
	{
		return text.traverse(traversal);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#update()
	 */
	public void update()
	{
		text.update();
	}
}
