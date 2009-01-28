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

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;

/**
 * @author Simon Lehmann - simon at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReportSenderCfMod extends ConfigModule
{
	private static final long serialVersionUID = 1L;
	
	private String errorReportSenderId;
	private Boolean attachScreenShotToErrorReport_default;
	private Boolean attachScreenShotToErrorReport_decide;
	
	@Override
	public void init() throws InitException
	{
		super.init();
		// dont set a default for the sender id. null means auto-select
		
		// set attachScreenShotToErrorReport_decide to true by default but disable sending by default.
		// JFire changes this value depending on security rules. In this case, this value only
		// applies as long as no JFire user was logged in.
		if(attachScreenShotToErrorReport_decide == null)
			attachScreenShotToErrorReport_decide = true;
		if(attachScreenShotToErrorReport_default == null)
			attachScreenShotToErrorReport_default = false;
	}
	
	/**
	 * Get the errorReportSenderId.
	 * @return the errorReportSenderId
	 */
	public String getErrorReportSenderId()
	{
		return errorReportSenderId;
	}
	
	/**
	 * Set the errorReportSenderId.
	 * @param errorReportSenderId the errorReportSenderId to set
	 */
	public void setErrorReportSenderId(String errorReportSenderId)
	{
		this.errorReportSenderId = errorReportSenderId;
		setChanged();
	}
	
	/**
	 * Is the user allowed to override the default value specified by {@link #isAttachScreenShotToErrorReport_default()}?
	 *
	 * @return whether the user is allowed to decide about sending a screen shot.
	 */
	public boolean isAttachScreenShotToErrorReport_decide() {
		return attachScreenShotToErrorReport_decide;
	}
	/**
	 * Set whether the user is allowed to override the default value specified by {@link #isAttachScreenShotToErrorReport_default()}.
	 *
	 * @param attachScreenShotToErrorReport_decide whether the user is allowed to decide (or must take the default value).
	 */
	public void setAttachScreenShotToErrorReport_decide(boolean attachScreenShotToErrorReport_decide) {
		this.attachScreenShotToErrorReport_decide = attachScreenShotToErrorReport_decide;
		setChanged();
	}

	/**
	 * Send a screen shot by default?
	 *
	 * @return the default value.
	 */
	public boolean isAttachScreenShotToErrorReport_default() {
		return attachScreenShotToErrorReport_default;
	}
	/**
	 * Set the default value, whether to send a screen shot.
	 *
	 * @param attachScreenShotToErrorReport_default the new default value.
	 */
	public void setAttachScreenShotToErrorReport_default(boolean attachScreenShotToErrorReport_default) {
		this.attachScreenShotToErrorReport_default = attachScreenShotToErrorReport_default;
		setChanged();
	}
}
