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

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.jfire.compatibility.CompatibleSWT;
import org.nightlabs.l10n.DateFormatProvider;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.l10n.DateParseException;

/**
 * A date time edit control.
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @deprecated Use {@link DateTimeControl} instead. Do the caption and
 * 		checkbox stuff yourself.
 */
@Deprecated
public class DateTimeEdit extends XComposite
{
	private Text text;
	private Button lookupButton;
	private Date date;
	private long flags;
	private Button active;

	protected static boolean autoCalendarButton(long flags)
	{
		return (DateFormatProvider.DATE & flags) != 0;
	}

	/**
	 * This constructor calls {@link #DateTimeEdit(Composite, long, Date)}
	 * with <tt>new Date()</tt>.
	 * @deprecated Use {@link DateTimeControl} instead. Do the caption and
	 * 		checkbox stuff yourself.
	 */
	@Deprecated
	public DateTimeEdit(Composite parent, long flags)
	{
		this(parent, flags, new Date());
	}

	/**
	 * This constructor calls {@link #DateTimeEdit(Composite, long, Date, String)}
	 * with <tt>new Date()</tt>.
	 * @deprecated Use {@link DateTimeControl} instead. Do the caption and
	 * 		checkbox stuff yourself.
	 */
	@Deprecated
	public DateTimeEdit(Composite parent, long flags, String caption)
	{
		this(parent, flags, new Date(), caption, true);
	}

	/**
	 * This constructor calls {@link #DateTimeEdit(Composite, long, Date, String)}
	 * with <tt>caption = null</tt>.
	 * @deprecated Use {@link DateTimeControl} instead. Do the caption and
	 * 		checkbox stuff yourself.
	 */
	@Deprecated
	public DateTimeEdit(Composite parent, long flags, Date date)
	{
		this(parent, flags, date, null, true);
	}

	public static final long FLAGS_SHOW_ACTIVE_CHECK_BOX = 0x100000000L;

	/**
	 * @param parent The SWT parent.
	 * @param flags One of the "FLAGS_"-constants in {@link DateFormatter} - if needed, combined with {@link #FLAGS_SHOW_ACTIVE_CHECK_BOX}.
	 * @param date The current date to display. May be <code>null</code> to indicate that no initial date should be set.
	 * @param caption Either <tt>null</tt> or a text that should be displayed above the date-input.
	 * @deprecated Use {@link DateTimeControl} instead. Do the caption and
	 * 		checkbox stuff yourself.
	 */
	@Deprecated
	public DateTimeEdit(Composite parent, long flags, Date date, String caption, boolean top)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.flags = flags;
		this.getGridData().grabExcessHorizontalSpace = false;
		this.getGridData().grabExcessVerticalSpace = false;
		this.getGridData().horizontalAlignment = SWT.BEGINNING;
		this.getGridData().verticalAlignment = SWT.BEGINNING;

		if (top)
			getGridLayout().numColumns = 3;
		else
			getGridLayout().numColumns = 4;

		if (caption != null) {
			Control control;
			if ((FLAGS_SHOW_ACTIVE_CHECK_BOX & flags) == FLAGS_SHOW_ACTIVE_CHECK_BOX) {
				active = new Button(this, SWT.CHECK);
				active.setText(caption);
				control = active;
			}
			else {
//				Label l = new Label(this, SWT.WRAP);
				Label l = new Label(this, SWT.NONE);
				l.setText(caption);
				control = l;
			}
			GridData gd = new GridData();
			if (top)
				gd.horizontalSpan = getGridLayout().numColumns;
			control.setLayoutData(gd);
		}

		if ((FLAGS_SHOW_ACTIVE_CHECK_BOX & flags) == FLAGS_SHOW_ACTIVE_CHECK_BOX) {
			if (active == null) {
				++getGridLayout().numColumns;
				active = new Button(this, SWT.CHECK);
			}

			active.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					activeSelected();
				}
			});
		}

		text = new Text(this, getBorderStyle());
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(textModifyListener);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (text.getText().equals("")) //$NON-NLS-1$
					setDate(null);
				else if (DateTimeEdit.this.date != null)
					setTimestamp(DateTimeEdit.this.date.getTime());
			}
		});

		this.setDate(date); // text needs to exist

//		if ((DateFormatProvider.DATE & flags) != 0) {
			lookupButton = new Button(this, SWT.FLAT);
			lookupButton.setText("..."); //$NON-NLS-1$
			lookupButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					lookupButtonClicked();
				}
			});
//		}

		activeSelected();
	}

	private void activeSelected()
	{
		text.setEnabled(isActive());
		lookupButton.setEnabled(isActive());
	}

	private void lookupButtonClicked()
	{
		CalendarDateTimeEditLookupDialog dialog = new CalendarDateTimeEditLookupDialog(getShell(),
				flags, lookupButton.toDisplay(0, 0));
		Calendar cal = Calendar.getInstance();
		if(date != null)
			cal.setTime(date);
		dialog.setInitialDate(cal);
		if (dialog.open() == Window.OK) {
			setDate(dialog.getDate().getTime());
		}
	}

	private LinkedList<ModifyListener> modifyListeners = null;

	public void addModifyListener(ModifyListener modifyListener)
	{
		if (modifyListeners == null)
			modifyListeners = new LinkedList<ModifyListener>();

		modifyListeners.add(modifyListener);
	}

	public boolean removeModifyListener(ModifyListener modifyListener)
	{
		if (modifyListeners == null)
			return false;

		return modifyListeners.remove(modifyListener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose()
	{
		text.removeModifyListener(textModifyListener);

		super.dispose();
	}

	private ModifyListener textModifyListener = new ModifyListener() {
		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		public void modifyText(ModifyEvent e)
		{
			try {
				date = DateFormatter.parseDate(text.getText());
				dateParseException = null;
			} catch (DateParseException x) {
				dateParseException = x;
			}

			if (modifyListeners == null)
				return;

			Event event = new Event();
			event.widget = DateTimeEdit.this;
			event.display = e.display;
			event.time = CompatibleSWT.getModifyEventTime(e);
			event.data = e.data;
			ModifyEvent me = new ModifyEvent(event);
			for (Iterator<ModifyListener> it = modifyListeners.iterator(); it.hasNext(); ) {
				ModifyListener l = it.next();
				l.modifyText(me);
			}
		}
	};

	private DateParseException dateParseException;

	/**
	 * @return Returns either the last {@link DateParseException} that occured or
	 *		<tt>null</tt>, if all is fine.
	 */
	public DateParseException getDateParseException()
	{
		return dateParseException;
	}

	/**
	 * @param timestamp The timestamp to set.
	 */
	public void setTimestamp(long timestamp)
	{
		if (date == null)
			date = new Date(timestamp);
		else
			date.setTime(timestamp);

		text.setText(DateFormatter.formatDate(date, flags));
		this.dateParseException = null;
	}
	/**
	 * @return Returns the timestamp
	 * @throws NullPointerException if no date is set
	 */
	public long getTimestamp()
	{
		if (date == null)
			throw new NullPointerException("No date set."); //$NON-NLS-1$

		return date.getTime();
	}

	/**
	 * @return Returns the text. Note, that this is the raw text of what the user
	 *		entered and it is not parsable if {@link #getDateParseException()} returns
	 *		not <tt>null</tt>.
	 */
	public String getText()
	{
		return text.getText();
	}

	/**
	 * @param date The date to set. May be null to indicate that no date should be set.
	 */
	public void setDate(Date date)
	{
		this.date = date;
		if (date != null)
			text.setText(DateFormatter.formatDate(date, flags));
		this.dateParseException = null;
	}
	/**
	 * @return Returns the date.
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 * @return Returns the flags.
	 */
	public long getFlags()
	{
		return flags;
	}

	public boolean isActive()
	{
		return active == null ? true : active.getSelection();
	}

	public void setActive(boolean active)
	{
		if (this.active == null)
			return;

		this.active.setSelection(active);
		activeSelected();
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Button#addSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void addActiveChangeListener(SelectionListener listener)
	{
		active.addSelectionListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Button#removeSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void removeActiveChangeListener(SelectionListener listener)
	{
		active.removeSelectionListener(listener);
	}

	public void notifyActionChangeListener()
	{
		final Event event = new Event();
		event.item = active;
		event.display = active.getDisplay();
		event.type = SWT.Selection;
		event.widget = active;
		active.notifyListeners(SWT.Selection, event);
	}
}
