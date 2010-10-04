package org.nightlabs.base.ui.composite;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.base.ui.form.NightlabsFormsToolkit;
import org.nightlabs.base.ui.resource.SharedImages;
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
	private Button clearButton;
	private Date date;
	private long flags;
	private boolean allowPast;

	/**
	 * Create a new DateTimeComposite for the current date and time.
	 * @param parent The SWT parent.
	 * @param style The SWT style.
	 * @param flags One of the "FLAGS_"-constants in {@link DateFormatter}.
	 */
	public DateTimeControl(Composite parent, int style, long flags)
	{
		this(parent, style, flags, new Date());
		this.allowPast = true;
	}

	/**
	 * Create a new DateTimeComposite for the current date and time.
	 * @param parent The SWT parent.
	 * @param style The SWT style.
	 * @param flags One of the "FLAGS_"-constants in {@link DateFormatter}.
	 * @param allowPast dont allow a past date
	 */
	public DateTimeControl(Composite parent, boolean allowPast , int style, long flags)
	{
		this(parent, style, flags, new Date());
		this.allowPast = allowPast;
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
		this.allowPast = true;

		getGridLayout().numColumns = 3;

		text = new Text(this, getBorderStyle());
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(textModifyListener);
		text.setEditable(true);
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
		// This needs to be done, somehow as the layout is tight wrapping and the borders
		// are otherwise not shown in a Form environment (at least for gtk)
		NightlabsFormsToolkit.adjustLayoutForBorderPainting(text);

		this.setDate(date); // text needs to exist

		lookupButton = new Button(this, SWT.NONE);
		lookupButton.setText("..."); //$NON-NLS-1$
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

		clearButton = new Button(this, SWT.NONE);
		clearButton.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), DateTimeControl.class, "clearButton"));
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearButtonClicked();
			}
		});
		
		super.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				fireFocusEvent(e, true);
			}
			@Override
			public void focusLost(FocusEvent e) {
				fireFocusEvent(e, false);
			}
		});

		text.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				fireFocusEvent(e, true);
			}
			@Override
			public void focusLost(FocusEvent e) {
				fireFocusEvent(e, false);
			}
		});
	}

	
	public void setDateEditable(Boolean editable) {
		text.setEditable(editable);
	}
	
	/**
	 * Called when the lookup button was clicked. Open a date time
	 * lookup dialog at the buttons position.
	 */
	private void lookupButtonClicked()
	{
		CalendarDateTimeEditLookupDialog dialog = new CalendarDateTimeEditLookupDialog(
				getShell(),
				allowPast,flags,
				lookupButton.toDisplay(0, 0)
		);
		Calendar cal = Calendar.getInstance();
		if (date == null)
			date = new Date();
		cal.setTime(date);

		dialog.setInitialDate(cal);
		// No needs to propagate selection Event if Cancel was clicked
		if (dialog.open() == Window.OK) {
			setDate(dialog.getDate().getTime());
			Object[] listeners = selectionListeners.getListeners();
			if (listeners.length < 1)
				return;

			Event event = new Event();
			event.widget = DateTimeControl.this;
			event.display = getDisplay();
			//		event.time = e.time;
			//		event.data = e.data;
			SelectionEvent se = new SelectionEvent(event);
			for (Object listener : listeners) {
				SelectionListener l = (SelectionListener) listener;
				l.widgetSelected(se);
			}
		}
	}

	private void fireFocusEvent(FocusEvent originalFocusEvent, boolean gained)
	{
		Object[] listeners = focusListeners.getListeners();
		if (listeners.length < 1)
			return;

		Event event = new Event();
		event.widget = DateTimeControl.this;
		event.display = originalFocusEvent.display;
		event.time = originalFocusEvent.time;
		event.data = originalFocusEvent.data;
		FocusEvent fe = new FocusEvent(event);
		for (Object listener : listeners) {
			FocusListener l = (FocusListener) listener;
			if (gained)
				l.focusGained(fe);
			else
				l.focusLost(fe);
		}
	}

	private ListenerList focusListeners = new ListenerList();

	@Override
	public void addFocusListener(FocusListener listener) {
		focusListeners.add(listener);
	}
	@Override
	public void removeFocusListener(FocusListener listener) {
		focusListeners.remove(listener);
	}

	private ListenerList selectionListeners = new ListenerList();
	private ListenerList clearSelectionListeners = new ListenerList();
	
	/**
	 * Add a listener that is triggered whenever the user selected date and time in a calendar/time dialog.
	 * <p>
	 * In contrast to the {@link ModifyListener}s (see {@link #addModifyListener(ModifyListener)}) that are
	 * triggered whenever the text in the input field is modified (by keyboard or by copy'n'paste), the
	 * {@link SelectionListener}s are only triggered when the lookup-button was clicked and the dialog
	 * closed via "OK" (not "Cancel").
	 * </p>
	 * @param listener the listener to be added.
	 */
	public void addSelectionListener(SelectionListener listener)
	{
		selectionListeners.add(listener);
	}
	/**
	 * Remove a listener that has been added by {@link #addSelectionListener(SelectionListener)} before.
	 * @param listener the listener to be removed.
	 * @see #addSelectionListener(SelectionListener)
	 */
	public void removeSelectionListener(SelectionListener listener)
	{
		selectionListeners.remove(listener);
	}
	
	public void addClearSelectionListener(SelectionListener listener)
	{
		clearSelectionListeners.add(listener);
	}

	public void removeClearSelectionListener(SelectionListener listener)
	{
		clearSelectionListeners.remove(listener);
	}

	private ListenerList modifyListeners = new ListenerList();

	public void addModifyListener(ModifyListener modifyListener)
	{
		modifyListeners.add(modifyListener);
	}
	public void removeModifyListener(ModifyListener modifyListener)
	{
		modifyListeners.remove(modifyListener);
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

	private boolean suppressModifyEvent = false;

	private ModifyListener textModifyListener = new ModifyListener() {
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		@Override
		public void modifyText(ModifyEvent e)
		{
			if (suppressModifyEvent)
				return;

			try {
				date = DateFormatter.parseDate(text.getText());
				dateParseException = null;
			} catch (DateParseException x) {
				dateParseException = x;
			}

			Object[] listeners = modifyListeners.getListeners();
			if (listeners.length < 1)
				return;

			Event event = new Event();
			event.widget = DateTimeControl.this;
			event.display = e.display;
			event.time = e.time;
			event.data = e.data;
			ModifyEvent me = new ModifyEvent(event);
			for (Object listener : listeners) {
				ModifyListener l = (ModifyListener) listener;
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

		suppressModifyEvent = true;
		try {
			text.setText(DateFormatter.formatDate(date, flags));
		} finally {
			suppressModifyEvent = false;
		}
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
		if (date != null) {
			suppressModifyEvent = true;
			try {
				text.setText(DateFormatter.formatDate(date, flags));
			} finally {
				suppressModifyEvent = false;
			}
		}
		else {
			suppressModifyEvent = true;
			text.setText(""); //$NON-NLS-1$
			suppressModifyEvent = false;
		}
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

	/**
	 * Clears the text & sets date = null. Called when the clear button was clicked.
	 */
	private void clearButtonClicked() {
		this.date = null;
		
		suppressModifyEvent = true;
		text.setText(""); //$NON-NLS-1$
		
		Object[] listeners = clearSelectionListeners.getListeners();
		if (listeners.length < 1)
			return;

		Event event = new Event();
		event.widget = DateTimeControl.this;
		event.display = getDisplay();
		SelectionEvent se = new SelectionEvent(event);
		for (Object listener : listeners) {
			SelectionListener l = (SelectionListener) listener;
			l.widgetSelected(se);
		}
		
		suppressModifyEvent = false;
	}

	/**
	 * Sets the text of the lookup button
	 * @param text the text to set for the lookup button.
	 */
	public void setButtonText(String text) {
		lookupButton.setText(text);
	}
}
