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

package org.nightlabs.base.exceptionhandler.errorreport;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.Assert;
import org.nightlabs.io.DataBuffer;
import org.nightlabs.util.IOUtil;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReport
implements Serializable
{
	private static final long serialVersionUID = 2L;
	
//	Tobias: replaced by collection of ExceptionPair in order to provide error reports of multiple exceptions.
//	
	private String userComment;
	private Properties systemProperties;
	private Date time;
	
	private List<CauseEffectThrowablePair> throwablePairList;
	
	/**
	 * Initialize an empty error report. 
	 */
	public ErrorReport(Throwable throwable, Throwable causeThrowable)
	{
		throwablePairList = new LinkedList<CauseEffectThrowablePair>();		
		addThrowablePair(throwable, causeThrowable);
		time = new Date();
		this.systemProperties = System.getProperties();
	}
	
	public void addThrowablePair(Throwable throwable, Throwable causeThrowable) {
		Assert.isNotNull(throwable);
		Assert.isNotNull(causeThrowable);
		throwablePairList.add(new CauseEffectThrowablePair(throwable, causeThrowable));
	}
	
	public List<CauseEffectThrowablePair> getThrowablePairList() {
		return throwablePairList;
	}
	
	public Throwable getFirstThrowable() {
		return throwablePairList.get(0).getEffectThrowable();
	}

//	/**
//	 * Initialize this error report with a thrown and a trigger exception.
//	 * @param thrownException The exception thrown
//	 * @param triggerException The exception that triggered the error handler
//	 */
//	public ErrorReport(Throwable thrownException, Throwable triggerException)
//	{
//		setThrownException(thrownException);
//		setTriggerException(triggerException);
//		this.systemProperties = System.getProperties();
//		this.time = new Date();
//	}
	
//	/**
//	 * @return The thrownException.
//	 */
//	public Throwable getThrownException()
//	{
//		return thrownException;
//	}
//
//	/**
//	 * @param error The thrownException to set.
//	 */
//	public void setThrownException(Throwable error)
//	{
//		if (error == null)
//			throw new NullPointerException("Parameter thrownException must not be null!");
//		this.thrownException = error;
//	}
//
//	/**
//	 * @return The triggerException.
//	 */
//	public Throwable getTriggerException()
//	{
//		return triggerException;
//	}
//
//	/**
//	 * @param triggerException The triggerException to set.
//	 */
//	public void setTriggerException(Throwable triggerException)
//	{
//		if (triggerException == null)
//			throw new NullPointerException("Parameter triggerException must not be null!");
//		this.triggerException = triggerException;
//	}

	/**
	 * @return Returns the userComment.
	 */
	public String getUserComment()
	{
		return userComment;
	}

	/**
  public String getErrorStackTraceAsString(Throwable error)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    error.printStackTrace(pw);
    pw.close();
    return sw.getBuffer().toString();
  }


  public String getCurrentTimeAsString()
  {
    SimpleDateFormat bartDateFormat =
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String timestring = new String();
    Date date = new Date();
    timestring =bartDateFormat.format(date);
    return timestring;
  }

	 * @param userComment The userComment to set.
	 */
	public void setUserComment(String userComment)
	{
		this.userComment = userComment;
	}

	/**
	 * @return The system properties associated with this error report
	 */
	public Properties getSystemProperties()
	{
		return systemProperties;
	}

	/**
	 * @param systemProperties The system properties to associate with this error report
	 */
	public void setSystemProperties(Properties systemProperties)
	{
		this.systemProperties = systemProperties;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		if (time == null)
			throw new NullPointerException("Parameter time must not be null!"); //$NON-NLS-1$
		this.time = time;
	}

	protected String getTimeAsString()
	{
		return getTimeAsString(time);
	}

	/**
	 * Formats the thrownException report into sth. like this:
	 * 
	 * ---
	 * Time:
	 * 2007-06-27 16:11:56
	 * 
	 * 
	 * User Comment:
	 * bla bla bla
	 * 
	 * Thrown exception stack trace(s)
	 * java.lang.Exception: shfgiushf
	 *   at xxx
	 * 
	 * java.lang.Exception: shfgiushf
	 *   at xxx
	 *   
	 * 
	 * System properties:
	 * ...
	 * ---
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		
//		StringBuffer props = new StringBuffer();
//		for (Iterator it = systemProperties.entrySet().iterator(); it.hasNext(); ) {
//		Map.Entry me = (Map.Entry)it.next();
//		props.append(me.getKey());
//		props.append('=');
//		props.append(me.getValue());
//		props.append('\n');
//		}

		StringBuffer sb = new StringBuffer("Time:\n"+ getTimeAsString() +"\n\nUser Comment:\n" + userComment + "\n\nThrown exception stack trace(s):\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		for (CauseEffectThrowablePair causeEffectThrowablePair : throwablePairList) {
			sb.append(getExceptionStackTraceAsString(causeEffectThrowablePair.getEffectThrowable()) + "\n"); //$NON-NLS-1$
		}
//		"Thrown exception stack trace(s):\n" + getExceptionStackTraceAsString(thrownException) +
		sb.append("\nSystem Properties:\n"); //$NON-NLS-1$
		
		try {
			DataBuffer db = new DataBuffer(1024);
			OutputStream out = db.createOutputStream();
			systemProperties.storeToXML(out, ""); //$NON-NLS-1$
			out.close();
			InputStream in = db.createInputStream();
			InputStreamReader reader = new InputStreamReader(in, IOUtil.CHARSET_UTF_8);
			while (reader.ready()) {
				sb.append((char)reader.read());
			}
		} catch (Exception x) {
			sb.append("Dumping system properties failed: " + x.getMessage()); //$NON-NLS-1$
		}
		
		return sb.toString();
	}

	public static String getExceptionStackTraceAsString(Throwable exception)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		pw.close();
		return sw.getBuffer().toString();
	}

	public static String getTimeAsString(Date time)
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time); //$NON-NLS-1$
	}
}