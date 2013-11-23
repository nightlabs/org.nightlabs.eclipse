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

package org.nightlabs.base.ui.exceptionhandler.errorreport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.graphics.ImageData;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.io.DataBuffer;
import org.nightlabs.util.IOUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReport
implements Serializable
{
	private static final long serialVersionUID = 2L;

	//	Tobias: replaced by collection of ExceptionPair in order to provide error reports of multiple exceptions.

	private String userComment;
	private Properties systemProperties;
	private ImageData errorScreenShot = null;
	private Boolean IsSendScreenShot = false;
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
	//	* Initialize this error report with a thrown and a trigger exception.
	//	* @param thrownException The exception thrown
	//	* @param triggerException The exception that triggered the error handler
	//	*/
	//	public ErrorReport(Throwable thrownException, Throwable triggerException)
	//	{
	//	setThrownException(thrownException);
	//	setTriggerException(triggerException);
	//	this.systemProperties = System.getProperties();
	//	this.time = new Date();
	//	}

	//	/**
	//	* @return The thrownException.
	//	*/
	//	public Throwable getThrownException()
	//	{
	//	return thrownException;
	//	}

	//	/**
	//	* @param error The thrownException to set.
	//	*/
	//	public void setThrownException(Throwable error)
	//	{
	//	if (error == null)
	//	throw new NullPointerException("Parameter thrownException must not be null!");
	//	this.thrownException = error;
	//	}

	//	/**
	//	* @return The triggerException.
	//	*/
	//	public Throwable getTriggerException()
	//	{
	//	return triggerException;
	//	}

	//	/**
	//	* @param triggerException The triggerException to set.
	//	*/
	//	public void setTriggerException(Throwable triggerException)
	//	{
	//	if (triggerException == null)
	//	throw new NullPointerException("Parameter triggerException must not be null!");
	//	this.triggerException = triggerException;
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

	/* 
	 * * @param userComment The userComment to set.
	 */
	public void setErrorScreenshot(ImageData errorScreenShot)
	{
		this.errorScreenShot = errorScreenShot;
	}

	public ImageData  getErrorScreenshot()
	{
		return this.errorScreenShot;
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
	@Override
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

	public Document toXmlDocument()
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			assert(false);
		}
		Document doc = db.newDocument();
		
		Element errorReport = (Element)doc.appendChild(doc.createElement("errorreport")); //$NON-NLS-1$
		
		// time
		Element time =  (Element)errorReport.appendChild(doc.createElement("time")); //$NON-NLS-1$
		time.appendChild(doc.createTextNode(getTimeAsString()));
		
		// user comment
		if(userComment != null && !userComment.isEmpty()) {
			Element comment =  (Element)errorReport.appendChild(doc.createElement("usercomment")); //$NON-NLS-1$
			comment.appendChild(doc.createTextNode(userComment));
		}
		
		// exceptions
		if(throwablePairList != null && !throwablePairList.isEmpty()) {
			Element exceptions =  (Element)errorReport.appendChild(doc.createElement("exceptions")); //$NON-NLS-1$
			for (CauseEffectThrowablePair causeEffectThrowablePair : throwablePairList) {
				Throwable throwable = causeEffectThrowablePair.getEffectThrowable();
				appendThrowable(doc, exceptions, throwable);
			}
		}
		
		// bundles
		List<Bundle> bundles = getOrderedBundles();
		if(bundles != null && !bundles.isEmpty()) {
			Element bundlesEl =  (Element)errorReport.appendChild(doc.createElement("bundles")); //$NON-NLS-1$
			for (Bundle bundle : bundles)
				appendBundle(doc, bundlesEl, bundle);
		}
		
		// system properties
		if(systemProperties != null && !systemProperties.isEmpty()) {
			Element properties =  (Element)errorReport.appendChild(doc.createElement("systemproperties")); //$NON-NLS-1$
			Set<Object> keys = systemProperties.keySet();
			Iterator<Object> i = keys.iterator();
			while(i.hasNext()) {
				String key = (String)i.next();
				Element entry = (Element)properties.appendChild(doc.createElement("entry")); //$NON-NLS-1$
				entry.setAttribute("key", key); //$NON-NLS-1$
				entry.appendChild(doc.createTextNode(systemProperties.getProperty(key)));
			}
		}
		
		return doc;
	}

	private void appendBundleHeader(Document doc, Element bundleEl, Bundle bundle, String headerEntry)
	{
		String elementName = headerEntry.toLowerCase().replaceAll("[^a-z0-9]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		Element symbolicName =  (Element)bundleEl.appendChild(doc.createElement(elementName));
		Dictionary<?, ?> headers = bundle.getHeaders();
		if(headers == null)
			return;
		String data = (String)headers.get(headerEntry);
		if(data == null || data.isEmpty())
			return;
		symbolicName.appendChild(doc.createTextNode(data));
	}
	
	private void appendBundle(Document doc, Element bundlesEl, Bundle bundle)
	{
		Element bundleEl =  (Element)bundlesEl.appendChild(doc.createElement("bundle")); //$NON-NLS-1$
		appendBundleHeader(doc, bundleEl, bundle, Constants.BUNDLE_SYMBOLICNAME);
		appendBundleHeader(doc, bundleEl, bundle, Constants.BUNDLE_VERSION);
		appendBundleHeader(doc, bundleEl, bundle, Constants.BUNDLE_NAME);
		appendBundleHeader(doc, bundleEl, bundle, Constants.BUNDLE_VENDOR);
	}

	public void writeXml(OutputStream os, String encoding)
	throws IOException
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = null;
		try {
			t = tf.newTransformer();
			//t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, PROPS_DTD_URI);
			t.setOutputProperty(OutputKeys.STANDALONE, "yes"); //$NON-NLS-1$
			t.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			t.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			t.setOutputProperty(OutputKeys.ENCODING, encoding);
		} catch (TransformerConfigurationException tce) {
			assert(false);
		}
		DOMSource doms = new DOMSource(toXmlDocument());
		StreamResult sr = new StreamResult(os);
		try {
			t.transform(doms, sr);
		} catch (TransformerException te) {
			IOException ioe = new IOException();
			ioe.initCause(te);
			throw ioe;
		}
	}
	
	public String toXmlString()
	{
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			writeXml(bout, IOUtil.CHARSET_NAME_UTF_8);
			return bout.toString(IOUtil.CHARSET_NAME_UTF_8);
		} catch (IOException e) {
			// should never happen
			throw new RuntimeException(e);
		}
	}
	
	private static void appendThrowable(Document doc, Element exceptions, Throwable throwable)
	{
		Element exception =  (Element)exceptions.appendChild(doc.createElement("exception")); //$NON-NLS-1$
		Element className =  (Element)exception.appendChild(doc.createElement("classname")); //$NON-NLS-1$
		className.appendChild(doc.createTextNode(throwable.getClass().getName()));
		Element stackTrace =  (Element)exception.appendChild(doc.createElement("stacktrace")); //$NON-NLS-1$
		stackTrace.appendChild(doc.createTextNode(getExceptionStackTraceAsString(throwable)));
	}

	public static String getExceptionStackTraceAsString(Throwable exception)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		pw.close();
		return sw.getBuffer().toString();
	}

	public static List<Bundle> getOrderedBundles()
	{
		NLBasePlugin plugin = NLBasePlugin.getDefault();
		if(plugin != null) {
			List<Bundle> bundles = Arrays.asList(plugin.getBundle().getBundleContext().getBundles());
			Collections.sort(bundles, new Comparator<Bundle>() {
				/* (non-Javadoc)
				 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
				 */
				@Override
				public int compare(Bundle o1, Bundle o2)
				{
					String name1 = o1.getSymbolicName();
					String name2 = o2.getSymbolicName();
					int res = name1.compareTo(name2);
					if(res != 0)
						return res;
					Dictionary<?, ?> headers1 = o1.getHeaders();
					Dictionary<?, ?> headers2 = o2.getHeaders();
					if(headers1 == null)
						return -1;
					if(headers2 == null)
						return 1;
					return ((String)headers1.get(Constants.BUNDLE_VERSION)).compareTo((String)headers2.get(Constants.BUNDLE_VERSION));
				}
			});
			return bundles;
		}
		return null;
	}
	
	public static String getTimeAsString(Date time)
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(time); //$NON-NLS-1$
	}

	public Boolean getIsSendScreenShot() {
		return IsSendScreenShot;
	}

	public void setIsSendScreenShot(Boolean sendScreenShot) {
		this.IsSendScreenShot = sendScreenShot;
	}
}