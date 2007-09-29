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

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.l10n.DateFormatProvider;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.l10n.DateParseException;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
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
	 */
	public DateTimeEdit(Composite parent, long flags)
	{
		this(parent, flags, new Date());
	}

	/**
	 * This constructor calls {@link #DateTimeEdit(Composite, long, Date, String)}
	 * with <tt>new Date()</tt>.
	 */
	public DateTimeEdit(Composite parent, long flags, String caption)
	{
		this(parent, flags, new Date(), caption);
	}

	/**
	 * This constructor calls {@link #DateTimeEdit(Composite, long, Date, String)}
	 * with <tt>caption = null</tt>.
	 */
	public DateTimeEdit(Composite parent, long flags, Date date)
	{
		this(parent, flags, date, null);
	}

	public static final long FLAGS_SHOW_ACTIVE_CHECK_BOX = 0x100000000L;

	/**
	 * @param parent The SWT parent.
	 * @param flags One of the "FLAGS_"-constants in {@link DateFormatter} - if needed, combined with {@link #FLAGS_SHOW_ACTIVE_CHECK_BOX}.
	 * @param date The current date to display. May be <code>null</code> to indicate that no initial date should be set.
	 * @param caption Either <tt>null</tt> or a text that should be displayed above the date-input.
	 */
	public DateTimeEdit(Composite parent, long flags, Date date, String caption)
	{
		super(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
		this.flags = flags;
		this.getGridData().grabExcessHorizontalSpace = false;
		this.getGridData().grabExcessVerticalSpace = false;
		this.getGridData().horizontalAlignment = SWT.BEGINNING;
		this.getGridData().verticalAlignment = SWT.BEGINNING;

		getGridLayout().numColumns = 3;

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
			public void focusLost(FocusEvent e) {
				if (text.getText().equals(""))
					setDate(null);
				else if (DateTimeEdit.this.date != null)
					setTimestamp(DateTimeEdit.this.date.getTime());
			}
		});

		this.setDate(date); // text needs to exist

//		if ((DateFormatProvider.DATE & flags) != 0) {
			lookupButton = new Button(this, SWT.NONE);
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
		CalendarDateTimeEditLookupDialog dialog = new CalendarDateTimeEditLookupDialog(getShell(), this, lookupButton.toDisplay(0, 0));
		if (dialog.open() == CalendarDateTimeEditLookupDialog.OK) {
			setDate(dialog.getDate());
		}
//		DateTimeEditLookupDialog dialog = new DateTimeEditLookupDialog(getShell(), this);
//		if (dialog.open() == CalendarDateTimeEditLookupDialog.OK) {
//			setDate(dialog.getDate());
//		}
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
			event.time = e.time;
			event.data = e.data;
			ModifyEvent me = new ModifyEvent(event);
			for (Iterator it = modifyListeners.iterator(); it.hasNext(); ) {
				ModifyListener l = (ModifyListener) it.next();
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
			throw new NullPointerException("No date set.");
		
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
}
