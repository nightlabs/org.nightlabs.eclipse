package org.nightlabs.base.composite;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.l10n.DateFormatProvider;

public class DateTimeEditLookupDialog
		extends Dialog
{
	private DateTimeEdit dateTimeEdit;
	private Calendar calendar;

	@SuppressWarnings("unused") //$NON-NLS-1$
	private Spinner year = null;
	private Spinner month = null;
	@SuppressWarnings("unused") //$NON-NLS-1$
	private Spinner day = null;

	@SuppressWarnings("unused") //$NON-NLS-1$
	private Spinner hour = null;
	@SuppressWarnings("unused") //$NON-NLS-1$
	private Spinner minute = null;
	@SuppressWarnings("unused") //$NON-NLS-1$
	private Spinner second = null;
	@SuppressWarnings("unused") //$NON-NLS-1$
	private Spinner milliSec = null;

	private Map<Integer, Spinner> calendarField2Spinner = new HashMap<Integer, Spinner>();

	protected Spinner getSpinnerByCalendarField(int field)
	{
		Spinner spinner = calendarField2Spinner.get(field);
		if (spinner == null)
			throw new IllegalStateException("No Spinner registered for field " + field); //$NON-NLS-1$

		return spinner;
	}

	protected int getCalendarFieldBySpinner(Spinner spinner)
	{
		return ((Integer)spinner.getData()).intValue();
	}

	public DateTimeEditLookupDialog(Shell parentShell, DateTimeEdit dateTimeEdit)
	{
		super(parentShell);
		this.dateTimeEdit = dateTimeEdit;
		this.calendar = Calendar.getInstance();
		this.calendar.setTime(dateTimeEdit.getDate());
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite page = (Composite) super.createDialogArea(parent);
		int numColumns = 0;

		if ((DateFormatProvider.DATE & dateTimeEdit.getFlags()) == DateFormatProvider.DATE) {
			++numColumns;
			XComposite dateComp = new XComposite(page, SWT.BORDER);
			dateComp.getGridLayout().numColumns = 3;

			new Label(dateComp, SWT.NONE).setText(Messages.getString("org.nightlabs.base.composite.DateTimeEditLookupDialog.yearLabel.text")); //$NON-NLS-1$
			new Label(dateComp, SWT.NONE).setText(Messages.getString("org.nightlabs.base.composite.DateTimeEditLookupDialog.monthLabel.text")); //$NON-NLS-1$
			new Label(dateComp, SWT.NONE).setText(Messages.getString("org.nightlabs.base.composite.DateTimeEditLookupDialog.dayLabel.text")); //$NON-NLS-1$

			year = createSpinner(dateComp, Calendar.YEAR);
			month = createSpinner(dateComp, Calendar.MONTH);
			month.setMaximum(month.getMaximum() + 1); // unfortunately, months start with 0 in java :-(
			day = createSpinner(dateComp, Calendar.DAY_OF_MONTH);
		}

		if ((DateFormatProvider.TIME & dateTimeEdit.getFlags()) == DateFormatProvider.TIME) {
			++numColumns;
			XComposite timeComp = new XComposite(page, SWT.BORDER);
			timeComp.getGridLayout().numColumns = 2;

			new Label(timeComp, SWT.NONE).setText(Messages.getString("org.nightlabs.base.composite.DateTimeEditLookupDialog.hourLabel.text")); //$NON-NLS-1$
			new Label(timeComp, SWT.NONE).setText(Messages.getString("org.nightlabs.base.composite.DateTimeEditLookupDialog.minuteLabel.text")); //$NON-NLS-1$

			if ((DateFormatProvider.TIME_SEC & dateTimeEdit.getFlags()) == DateFormatProvider.TIME_SEC)
				new Label(timeComp, SWT.NONE).setText(Messages.getString("org.nightlabs.base.composite.DateTimeEditLookupDialog.secondLabel.text")); //$NON-NLS-1$

			if ((DateFormatProvider.TIME_MSEC & dateTimeEdit.getFlags()) == DateFormatProvider.TIME_MSEC)
				new Label(timeComp, SWT.NONE).setText(Messages.getString("org.nightlabs.base.composite.DateTimeEditLookupDialog.millisecLabel.text")); //$NON-NLS-1$

			hour = createSpinner(timeComp, Calendar.HOUR_OF_DAY);
			minute = createSpinner(timeComp, Calendar.MINUTE);

			if ((DateFormatProvider.TIME_SEC & dateTimeEdit.getFlags()) == DateFormatProvider.TIME_SEC) {
				++timeComp.getGridLayout().numColumns;
				second = createSpinner(timeComp, Calendar.SECOND);
			}

			if ((DateFormatProvider.TIME_MSEC & dateTimeEdit.getFlags()) == DateFormatProvider.TIME_MSEC) {
				++timeComp.getGridLayout().numColumns;
				milliSec = createSpinner(timeComp, Calendar.MILLISECOND);
			}
		}

		page.setLayout(new GridLayout(numColumns, false));
		return page;
	}

	protected Spinner createSpinner(Composite parent, int field)
	{
		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setMinimum(calendar.getMinimum(field) - 1);
		spinner.setMaximum(calendar.getMaximum(field) +  1);
		spinner.setSelection(getCalendarValue(field));
		spinner.setData(new Integer(field));
		calendarField2Spinner.put(field, spinner);
		spinner.addSelectionListener(fieldChangedListener);
		return spinner;
	}

	@Override
	protected void initializeBounds()
	{
		super.initializeBounds();
		getShell().pack();
		Point mouse = Display.getDefault().getCursorLocation();
		Rectangle r = getShell().getBounds(); 
		r.x = mouse.x;
		r.y = mouse.y;
		getShell().setBounds(r);
	}

	protected int getCalendarValue(int field)
	{
		int val = calendar.get(field);

		// why the hell do the months start with 0?!?!?!
		if (Calendar.MONTH == field)
			++val;

		return val;
	}

	protected void setCalendarValue(int field, int value)
	{
		// why the hell do the months start with 0?!?!?!
		if (Calendar.MONTH == field)
			--value;

		calendar.set(field, value);
	}

	private SelectionListener fieldChangedListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			Spinner spinner = (Spinner) e.getSource();
			updateField(spinner, getCalendarFieldBySpinner(spinner));
		}
	};

	protected void updateField(Spinner spinner, int field)
	{
		int newVal = spinner.getSelection();
		int oldVal = getCalendarValue(field);
		calendar.add(field, newVal - oldVal);
		for (Map.Entry<Integer, Spinner> me : calendarField2Spinner.entrySet()) {
			me.getValue().setSelection(getCalendarValue(me.getKey().intValue()));
		}
	}

	public Date getDate()
	{
		return calendar.getTime();
	}
}
