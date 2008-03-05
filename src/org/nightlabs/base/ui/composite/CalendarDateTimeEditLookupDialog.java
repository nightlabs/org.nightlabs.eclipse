package org.nightlabs.base.ui.composite;

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
import org.nightlabs.base.ui.util.DateUtil;
import org.nightlabs.l10n.DateFormatProvider;

/**
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class CalendarDateTimeEditLookupDialog
extends Dialog
{	
	private DateTime calendarDateTime;
	private DateTime timeDateTime;
	private DateTime dateDateTime;
	private Date date;	
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
		date = dateTimeEdit.getDate();
		if (date == null)
			date = new Date();
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
			DateUtil.setDate(date, calendarDateTime);
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
			timeDateTime = new DateTime(timeComp, timeStyle);
			timeDateTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			DateUtil.setDate(date, timeDateTime);	
		}
		
		page.setLayout(new GridLayout(numColumns, false));
		return page;
	}
		
	private void createDateDateTime(Composite parent) {
		dateDateTime = new DateTime(parent, SWT.DATE);
		dateDateTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		DateUtil.setDate(date, calendarDateTime);
		dateDateTime.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				calendarDateTime.setYear(dateDateTime.getYear());
				calendarDateTime.setMonth(dateDateTime.getMonth());
				calendarDateTime.setDay(dateDateTime.getDay());
			}
		});
	}
	
	public Date getDate() {
		return date;
	}

	@Override
	protected void okPressed() 
	{
		// It is unfortunately not possible to work with a calendar when
		// using a DateTime
		if (calendarDateTime != null) {
			date.setYear(calendarDateTime.getYear());
			date.setMonth(calendarDateTime.getMonth());
			date.setDate(calendarDateTime.getDay());
		}
		if (timeDateTime != null) {
			date.setHours(timeDateTime.getHours());
			date.setMinutes(timeDateTime.getMinutes());
			date.setSeconds(timeDateTime.getSeconds());
		}
		super.okPressed();
	}
	
}