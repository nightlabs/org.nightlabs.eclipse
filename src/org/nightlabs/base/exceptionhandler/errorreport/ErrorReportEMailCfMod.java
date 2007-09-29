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

import java.util.List;

import org.nightlabs.config.CfModList;
import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReportEMailCfMod extends ConfigModule
{
	private static final long serialVersionUID = 1L;

	private String mailFrom = null;
	private List<String> mailTo = null;
	private String smtpHost = null;
	private String smtpLocalhost = null;

	/* (non-Javadoc)
	 * @see org.nightlabs.config.ConfigModule#init()
	 */
	@Override
	public void init() throws InitException
	{
		super.init();
		if (mailFrom == null) {
			// since our sources are public, we write it this way for spam-protection - should be harder for a bot to read
			mailFrom = "jfire" + '@' + //$NON-NLS-1$
					"nightlabs.org"; //$NON-NLS-1$
		}
		if (mailTo == null) {
			mailTo = new CfModList<String>(this);
			mailTo.add(
					"bugreport" + '@' + //$NON-NLS-1$
					"nightlabs.org"); //$NON-NLS-1$
		}
		if (smtpHost == null)
			smtpHost = "mail.nightlabs.de"; //$NON-NLS-1$
		if(smtpLocalhost == null)
			smtpLocalhost = "www.nightlabs.org"; //$NON-NLS-1$
	}

	/**
	 * Get the mailFrom.
	 * @return the mailFrom
	 */
	public String getMailFrom()
	{
		return mailFrom;
	}

	/**
	 * Set the mailFrom.
	 * @param mailFrom the mailFrom to set
	 */
	public void setMailFrom(String mailFrom)
	{
		this.mailFrom = mailFrom;
		setChanged();
	}

	/**
	 * Get the mailTo.
	 * @return the mailTo
	 */
	public List<String> getMailTo()
	{
		return mailTo;
	}

	/**
	 * Set the mailTo.
	 * @param mailTo the mailTo to set
	 */
	public void setMailTo(List<String> mailTo)
	{
		this.mailTo = mailTo;
		setChanged();
	}

	/**
	 * Get the smtpHost.
	 * @return the smtpHost
	 */
	public String getSmtpHost()
	{
		return smtpHost;
	}

	/**
	 * Set the smtpHost.
	 * @param smtpHost the smtpHost to set
	 */
	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
		setChanged();
	}

	/**
	 * Get the smtpLocalhost.
	 * @return the smtpLocalhost
	 */
	public String getSmtpLocalhost()
	{
		return smtpLocalhost;
	}

	/**
	 * Set the smtpLocalhost.
	 * @param smtpLocalhost the smtpLocalhost to set
	 */
	public void setSmtpLocalhost(String smtpLocalhost)
	{
		this.smtpLocalhost = smtpLocalhost;
		setChanged();
	}
}
