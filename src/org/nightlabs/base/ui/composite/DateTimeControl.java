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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.l10n.DateParseException;

/**
 * A date time edit control.
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class DateTimeControl extends XComposite {
	private Text text;
	private Button lookupButton;
	private Date date;
	private long flags;

	/**
	 * Create a new DateTimeComposite for the current date and time.
	 * @param parent The SWT parent.
	 * @param style The SWT style.
	 * @param flags One of the "FLAGS_"-constants in {@link DateFormatter}.
	 */
	public DateTimeControl(Composite parent, int style, long flags)
	{
		this(parent, style, flags, new Date());
	}

	/**
	 * Create a new DateTimeComposite for the given date and time.
	 * @param parent The SWT parent.
	 * @param style The SWT style.
	 * @param flags One of the "FLAGS_"-constants in {@link DateFormatter}.
	 * @param date The date to display. May be <code>null</code> to indicate that no initial date should be set.
	 */
	public DateTimeControl(Composite parent, int style, long flags, Date date)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		this.flags = flags;

		getGridLayout().numColumns = 2;

		text = new Text(this, getBorderStyle());
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(textModifyListener);
		text.addFocusListener(new FocusAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
			 */
			@Override
			public void focusLost(FocusEvent e) {
				if (text.getText().equals("")) //$NON-NLS-1$
					setDate(null);
				else if (DateTimeControl.this.date != null)
					setTimestamp(DateTimeControl.this.date.getTime());
			}
		});

		this.setDate(date); // text needs to exist

		lookupButton = new Button(this, SWT.NONE);
		lookupButton.setText("...");
		lookupButton.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				lookupButtonClicked();
			}
		});
	}

	/**
	 * Called when the lookup button was clicked. Open a date time
	 * lookup dialog at the buttons position.
	 */
	private void lookupButtonClicked()
	{
		CalendarDateTimeEditLookupDialog dialog = new CalendarDateTimeEditLookupDialog(getShell(), 
				flags, lookupButton.toDisplay(0, 0));
		Calendar cal = Calendar.getInstance();
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

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose()
	{
		text.removeModifyListener(textModifyListener);
		super.dispose();
	}

	private ModifyListener textModifyListener = new ModifyListener() {
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		@Override
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
			event.widget = DateTimeControl.this;
			event.display = e.display;
			event.time = e.time;
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
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.composite.XComposite#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		text.setEnabled(enabled);
		lookupButton.setEnabled(enabled);
	}
	
	/**
	 * Set this control editable.
	 * @param editable <code>true</code> to make the control editable -
	 * 		<code>false</code> to make it un-editable.
	 */
	public void setEditable(boolean editable) 
	{
		text.setEditable(editable);
		lookupButton.setEnabled(editable);
	}
}
