package org.nightlabs.base.ui.composite;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.util.DateTimeUtil;
import org.nightlabs.l10n.DateFormatProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class CalendarDateTimeEditLookupDialog
extends Dialog
{	
	private DateTime calendarDateTime;
	private DateTime timeDateTime;
	private DateTime dateDateTime;
	private Calendar date;	
	private DateTimeEdit dateTimeEdit;
	private Point initialLocation;
	
	public CalendarDateTimeEditLookupDialog(Shell parentShell, DateTimeEdit dateTimeEdit)
	{
		this(parentShell, dateTimeEdit, null);
	}

	public CalendarDateTimeEditLookupDialog(Shell parentShell, DateTimeEdit dateTimeEdit, Point initialLocation)
	{
		super(parentShell);
		this.dateTimeEdit = dateTimeEdit;
		date = Calendar.getInstance();
		Date otherDate = dateTimeEdit.getDate();
		if(otherDate != null)
			date.setTime(otherDate);
		this.initialLocation = initialLocation;
	}

	@Override
	protected Point getInitialLocation(Point initialSize)
	{
		if (initialLocation != null)
			return initialLocation;

		return super.getInitialLocation(initialSize);
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(Messages.getString("org.nightlabs.base.ui.composite.CalendarDateTimeEditLookupDialog.title")); //$NON-NLS-1$
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite page = (Composite) super.createDialogArea(parent);
		int numColumns = 0;

		if ((DateFormatProvider.DATE & dateTimeEdit.getFlags()) == DateFormatProvider.DATE) {
			++numColumns;
			XComposite dateComp = new XComposite(page, SWT.NONE);

			calendarDateTime = new DateTime(dateComp, SWT.CALENDAR | SWT.BORDER);
			DateTimeUtil.setDate(date, calendarDateTime);
			calendarDateTime.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					dateDateTime.setYear(calendarDateTime.getYear());
					dateDateTime.setMonth(calendarDateTime.getMonth());
					dateDateTime.setDay(calendarDateTime.getDay());
				}
			});
			
			if ((DateFormatProvider.TIME & dateTimeEdit.getFlags()) != DateFormatProvider.TIME)
				createDateDateTime(dateComp);
		}

		if ((DateFormatProvider.TIME & dateTimeEdit.getFlags()) == DateFormatProvider.TIME) {
			++numColumns;
			XComposite timeComp = new XComposite(page, SWT.NONE);
			timeComp.getGridLayout().numColumns = 2;
			
			new Label(timeComp, SWT.NONE).setText(Messages.getString("CalendarDateTimeEditLookupDialog.label.date")); //$NON-NLS-1$
			createDateDateTime(timeComp);
			
			int timeStyle = SWT.TIME;
			
			if ((DateFormatProvider.TIME_SEC & dateTimeEdit.getFlags()) == DateFormatProvider.TIME_SEC)
				timeStyle = timeStyle | SWT.MEDIUM;
			
			if ((DateFormatProvider.TIME_MSEC & dateTimeEdit.getFlags()) == DateFormatProvider.TIME_MSEC)
				timeStyle = timeStyle | SWT.LONG;
			
			new Label(timeComp, SWT.NONE).setText(Messages.getString("CalendarDateTimeEditLookupDialog.label.time")); //$NON-NLS-1$
			timeDateTime = new DateTime(timeComp, timeStyle | SWT.BORDER);
			timeDateTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			DateTimeUtil.setDate(date, timeDateTime);	
		}
		
		page.setLayout(new GridLayout(numColumns, false));
		return page;
	}
		
	private void createDateDateTime(Composite parent) {
		dateDateTime = new DateTime(parent, SWT.DATE | SWT.BORDER);
		dateDateTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		DateTimeUtil.setDate(date, dateDateTime);
		dateDateTime.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				calendarDateTime.setYear(dateDateTime.getYear());
				calendarDateTime.setMonth(dateDateTime.getMonth());
				calendarDateTime.setDay(dateDateTime.getDay());
			}
		});
	}
	
	public Calendar getDate() {
		return date;
	}

	@Override
	protected void okPressed() 
	{
		if (calendarDateTime != null) {
			date.set(Calendar.YEAR, calendarDateTime.getYear());
			date.set(Calendar.MONTH, calendarDateTime.getMonth());
			date.set(Calendar.DAY_OF_MONTH, calendarDateTime.getDay());
//			date.setYear(calendarDateTime.getYear());
//			date.setMonth(calendarDateTime.getMonth());
//			date.setDate(calendarDateTime.getDay());
		}
		if (timeDateTime != null) {
			date.set(Calendar.HOUR_OF_DAY, calendarDateTime.getHours());
			date.set(Calendar.MINUTE, calendarDateTime.getMinutes());
			date.set(Calendar.SECOND, calendarDateTime.getSeconds());
//			date.setHours(timeDateTime.getHours());
//			date.setMinutes(timeDateTime.getMinutes());
//			date.setSeconds(timeDateTime.getSeconds());
		}
		super.okPressed();
	}
	
}