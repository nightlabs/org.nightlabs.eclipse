package org.nightlabs.base.ui.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.DateTime;

/**
 * Utility methods for working with {@link DateTime}s. 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 */
public class DateTimeUtil 
{	
	private static final Logger logger = Logger.getLogger(DateTimeUtil.class);
	
	/**
	 * Returns the Date from the given DateTime.
	 * @param dateTime the {@link DateTime} to get a {@link Date} from
	 * @return the Date from the given DateTime
	 */
	public static Date getDate(DateTime dateTime) {
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
			logger.debug("year:"+year+",month:"+month+",day:"+day+",hour:"+hour+",minute:"+minute+",second:"+second);
			logger.debug("date = "+date);
			logger.debug("");
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
			logger.debug("calendar date = "+calendar.getTime());
			logger.debug("dateTime date = "+getDate(dateTime));
			logger.debug("");
		}
	}
	
	/**
	 * Fills the given {@link DateTime} with the values of the given {@link Date}.
	 * @param calendar the {@link Date} to set for the given {@link DateTime}
	 * @param dateTime the {@link DateTime} to fill with the value of the given {@link Date}
	 */
	public static void setDate(Date date, DateTime dateTime) {
		dateTime.setYear(date.getYear());
		dateTime.setMonth(date.getMonth());
		dateTime.setDay(date.getDate());
		dateTime.setHours(date.getHours());
		dateTime.setMinutes(date.getMinutes());
		dateTime.setSeconds(date.getSeconds());
	}
	
}
