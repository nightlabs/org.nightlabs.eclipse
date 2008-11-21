package org.nightlabs.jfire.ui.errorreport.mantis;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReport;
import org.nightlabs.base.ui.exceptionhandler.errorreport.IErrorReportSender;
import org.nightlabs.base.ui.util.ImageUtil;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 */
public class MantisSubmissionModule implements IErrorReportSender 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.exceptionhandler.errorreport.IErrorReportSender#sendErrorReport(org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReport)
	 */
	
	private String loginURL = "https://www.jfire.org/user.php";
	private String startSessionURL = "https://www.jfire.org/modules/bugs/index.php";
	private String selectProjectURL = "https://www.jfire.org/modules/bugs/set_project.php";
	private String submissionURL = "https://www.jfire.org/modules/bugs/bug_report.php";
	private String userName = "autoreporter";
	private String password= "tR33S!/";

	/**
	 * Provides a part source for uploading the screenshot in a multipart form
	 * 
	 */
	private class ScreenshotPartSource implements PartSource
	{
		private String message = null;
		private File tmpFile = null;
		
		public ScreenshotPartSource(ErrorReport report)
		{
			try
			{
				if(report.getIsSendScreenShot())
				{
					tmpFile = File.createTempFile("screenShot", ".jpg");
					tmpFile.deleteOnExit();
					ImageIO.write(
							ImageUtil.convertToAWT(report.getErrorScreenshot()),
							"JPG", //$NON-NLS-1$
							tmpFile
					);
				}
				else
					message = report.toString();
			}
			catch (IOException e)
			{
			}
		}
		
		@Override
		public InputStream createInputStream() throws IOException
		{
			if(tmpFile != null)
				return new FileInputStream(tmpFile);
			else
				return new ByteArrayInputStream(message.getBytes());
		}
		@Override
		public String getFileName()
		{
			if(tmpFile != null)
			{
				return "screenshot.jpg";
			}
			else
				return "error_report.txt";
		}
		@Override
		public long getLength()
		{
			if(tmpFile != null)
				return tmpFile.length();
			else
				return message.length();
		}
		
	}
	
	@Override
	public void sendErrorReport(final ErrorReport errorReport) 
	{
		HttpClient client = new HttpClient();
    try
		{
    	// jfire.org xoops login
  		PostMethod m = new PostMethod(loginURL);
  		m.addParameter("uname", userName);
  		m.addParameter("pass", password);
  		m.addParameter("op", "login");
  		m.setRequestHeader("Referer", "https://www.jfire.org/modules/content/");
  		m.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
			client.executeMethod(m);

			String sessionCookie = null;
			Cookie[] cookies = client.getState().getCookies();
			for(int i = 0; i < cookies.length; i++)
			{
				if("PHPSESSID".equals(cookies[i].getName()))
					sessionCookie = cookies[i].getValue();
			}
			if(sessionCookie == null);
			{
				// TODO: do something
//				return; // eclipse error marker bug
			}
			
			Header[] headers = m.getResponseHeaders();
      String res = new String(m.getResponseBody());

      // start mantis session
      m = new PostMethod(startSessionURL);
  		m.setRequestHeader("Referer", loginURL);
  		m.setRequestHeader("Cookie", "PHPSESSID=" + sessionCookie + ";");
  		m.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
  		

			client.executeMethod(m);

			String mantisCookie = null;
			cookies = client.getState().getCookies();
			for(int i = 0; i < cookies.length; i++)
			{
				if("MANTIS_STRING_COOKIE".equals(cookies[i].getName()))
					mantisCookie = cookies[i].getValue();
			}
			if(mantisCookie == null);
			{
				// TODO: do something
//				return; // eclipse error marker bug
			}

			headers = m.getResponseHeaders();
      res = new String(m.getResponseBody());

      
      //select mantis project
      m = new PostMethod(selectProjectURL);
  		m.setRequestHeader("Referer", "https://www.jfire.org/modules/bugs/login_select_proj_page.php?ref=bug_report_page.php");
  		m.setRequestHeader("Cookie", "PHPSESSID=" + sessionCookie + "; MANTIS_STRING_COOKIE=" + mantisCookie + ";");
  		m.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
  		m.setParameter("ref", "bug_report_page.php");
  		m.setParameter("project_id", "29");
  		

			client.executeMethod(m);

			headers = m.getResponseHeaders();
      res = new String(m.getResponseBody());

			String mantisProjectCookie = null;
			cookies = client.getState().getCookies();
			for(int i = 0; i < cookies.length; i++)
			{
				if("MANTIS_PROJECT_COOKIE".equals(cookies[i].getName()))
					mantisProjectCookie = cookies[i].getValue();
			}
			if(mantisProjectCookie == null);
			{
				mantisProjectCookie="29";
				// TODO: do something
//				return; // eclipse error marker bug
			}
  		
			// create field content
			String shortText = ErrorReport.getTimeAsString(errorReport.getTime()) + " : " +
				errorReport.getFirstThrowable().getClass().getSimpleName();
			Pattern p = Pattern.compile("/^[^\\r\\n]+[\\r\\n]+\\s+at\\s+([^\\s]+).*/", Pattern.MULTILINE);
			Matcher matcher = p.matcher(ErrorReport.getExceptionStackTraceAsString(errorReport.getFirstThrowable()));
			if(matcher.find())
				shortText += " " + matcher.group(1);
			
			String comment = ErrorReport.getTimeAsString(errorReport.getTime()) + "\n\n";
			if(errorReport.getUserComment() != null)
					comment += errorReport.getUserComment() + "\n\n---\n\n";
			comment += ErrorReport.getExceptionStackTraceAsString(errorReport.getFirstThrowable()).substring(0, 200) + "...";
				
			
			// issue submission
			m = new PostMethod(submissionURL);
      Part[] parts = {
      	new StringPart("m_id","0"), // ?
      	new StringPart("project_id","29"), // 29 = jfire.org 'Automated submissions'
      	new StringPart("handler_id","0"), // nobody?
      	new StringPart("reproducibility","70"), // 70 = 'have not tried'
      	new StringPart("severity","60"), // 60 = major
      	new StringPart("priority","30"), // 30 = normal
      	new StringPart("summary", shortText),
      	new StringPart("description", comment),
      	new StringPart("additional_info",errorReport.toString()),
      	new StringPart("view_state","50"),
      	new StringPart("max_file_size","2000000"),
      	new FilePart("file", new ScreenshotPartSource(errorReport)),
      };
  		m.setRequestHeader("Referer", "https://www.jfire.org/modules/bugs/bug_report_page.php");
  		m.setRequestHeader("Cookie", "PHPSESSID=" + sessionCookie + "; MANTIS_STRING_COOKIE=" + mantisCookie + "; MANTIS_PROJECT_COOKIE=23%3B13;");
  		m.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
      m.setRequestEntity(new MultipartRequestEntity(parts, m.getParams()));

      client.executeMethod(m);
			headers = m.getResponseHeaders();
      res = new String(m.getResponseBody());
      int x = 0;
      
		}
		catch (HttpException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] argv)
	{
		try
		{
			try
			{
				throw new RuntimeException("blahblah_1");
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		catch (Exception e)
		{
			ErrorReport rep = new ErrorReport(e, e.getCause());
			MantisSubmissionModule sub = new MantisSubmissionModule();
			sub.sendErrorReport(rep);
		}
	}
	
}
