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
 */
public class ErrorReportSenderCfMod extends ConfigModule
{
	private static final long serialVersionUID = 1L;
	
	private String errorReportSenderClass;
	private boolean attachScreenShotToErrorReport_default;
	private boolean attachScreenShotToErrorReport_decide;
	
	@Override
	public void init() throws InitException
	{
		super.init();
		if (errorReportSenderClass == null)
			errorReportSenderClass = ErrorReportSenderEMail.class.getName();
	}
	public String getErrorReportSenderClass()
	{
		return errorReportSenderClass;
	}
	public void setErrorReportSenderClass(String errorReportSenderClass)
	{
		this.errorReportSenderClass = errorReportSenderClass;
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
