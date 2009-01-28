package org.nightlabs.jfire.ui.errorreport.mantis;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class JFireOrgMantisErrorReportSender extends AbstractMantisErrorReportSender 
{
	protected String getMantisBaseUrl()
	{
		return "https://www.jfire.org/modules/bugs";
	}
	
	/**
	 * Get the userName.
	 * @return the userName
	 */
	protected String getUserName()
	{
		return "autoreporter";
	}
	
	/**
	 * Get the password.
	 * @return the password
	 */
	protected String getPassword()
	{
		return "tR33S!/";
	}
	
	/**
	 * Get the projectId.
	 * @return the projectId
	 */
	public int getProjectId()
	{
		return 29;
	}
	
	protected PostMethod doLogin(HttpClient client) throws IOException
	{
		// jfire.org xoops login
		PostMethod m = new PostMethod("https://www.jfire.org/user.php");
		m.addParameter("uname", getUserName());
		m.addParameter("pass", getPassword());
		m.addParameter("op", "login");
		m.setRequestHeader("Referer", "https://www.jfire.org/modules/content/");
		client.executeMethod(m);

		// start mantis session
		m = new PostMethod(getMantisBaseUrl()+"/index.php");
		m.setRequestHeader("Referer", "https://www.jfire.org/user.php");
		client.executeMethod(m);
		return m;
	}
}
