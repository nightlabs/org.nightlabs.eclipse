package org.nightlabs.base.ui.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.nightlabs.base.ui.resource.Messages;

/**
 * Utility methods for working with {@link DateTime}s. 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class DateTimeUtil 
{	
	private static final Logger logger = Logger.getLogger(DateTimeUtil.class);
	
	/**
	 * Returns the Date from the given DateTime.
	 * @param dateTime the {@link DateTime} to get a {@link Date} from
	 * @return the Date from the given DateTime
	 */
	public static Date getDate(DateTime dateTime) 
	{
		// WORKAROUND: to fix the wrong hours, minutes and seconds if 
		// SWT.CALENDAR style is set
		Calendar calendar = Calendar.getInstance();
		if ((dateTime.getStyle() & SWT.CALENDAR) != 0 ||
				(dateTime.getStyle() & SWT.DATE) != 0) 
		{
			return getDate(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), 
					calendar.get(Calendar.HOUR_OF_DAY), 
					calendar.get(Calendar.MINUTE), 
					calendar.get(Calendar.SECOND));			
		}
		else if ((dateTime.getStyle() & SWT.TIME) != 0) 
		{
			return getDate(calendar.get(Calendar.YEAR), 
					calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 
					dateTime.getHours(), dateTime.getMinutes(), dateTime.getSeconds());			
		}		
		return getDate(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), 
				dateTime.getHours(), dateTime.getMinutes(), dateTime.getSeconds());			
	}
	
	/**
	 * Creates a {@link Date} for the given parameters.
	 * @param year the year
	 * @param month the month (0-based)
	 * @param day the day of the month
	 * @param hour the hour
	 * @param minute the minute
	 * @param second the second
	 * @return the corresponding date
	 */
	public static Date getDate(int year, int month, int day, int hour, 
			int minute, int second) 
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, hour, minute, second);
		Date date = calendar.getTime();
//		Date date = new Date(year-1900, month, day, hour, minute, second);
		if (logger.isDebugEnabled()) {
			logger.debug("year:"+year+",month:"+month+",day:"+day+",hour:"+hour+",minute:"+minute+",second:"+second); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			logger.debug("date = "+date); //$NON-NLS-1$
			logger.debug(""); //$NON-NLS-1$
		}
		return date;
	}
	
	/**
	 * Fills the given {@link DateTime} with the values of the given {@link Calendar}.
	 * @param calendar the {@link Calendar} to set for the given {@link DateTime}
	 * @param dateTime the {@link DateTime} to fill with the value of the given {@link Calendar}
	 */
	public static void setDate(Calendar calendar, DateTime dateTime) {
		dateTime.setYear(calendar.get(Calendar.YEAR));
		dateTime.setMonth(calendar.get(Calendar.MONTH));
		dateTime.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		dateTime.setHours(calendar.get(Calendar.HOUR_OF_DAY));
		dateTime.setMinutes(calendar.get(Calendar.MINUTE));
		dateTime.setSeconds(calendar.get(Calendar.SECOND));
//		setDate(calendar.getTime(), dateTime);
		if (logger.isDebugEnabled()) {
			logger.debug("calendar date = "+calendar.getTime()); //$NON-NLS-1$
			logger.debug("dateTime date = "+getDate(dateTime)); //$NON-NLS-1$
			logger.debug(""); //$NON-NLS-1$
		}
	}
	
	/**
	 * Fills the given {@link DateTime} with the values of the given {@link Date}.
	 * @param calendar the {@link Date} to set for the given {@link DateTime}
	 * @param dateTime the {@link DateTime} to fill with the value of the given {@link Date}
	 * @deprecated Use {@link #setDate(Calendar, DateTime)}
	 */
	@Deprecated
	public static void setDate(Date date, DateTime dateTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		setDate(cal, dateTime);
	}
}
