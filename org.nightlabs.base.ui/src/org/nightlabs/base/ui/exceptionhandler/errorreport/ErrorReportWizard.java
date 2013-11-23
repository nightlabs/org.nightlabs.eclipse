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

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.config.Config;
import org.nightlabs.util.Util;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReportWizard extends DynamicPathWizard
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ErrorReportWizard.class) ;

	private ErrorReport errorReport;
	private ErrorReportWizardEntryPage entryPage;
	private ErrorReportWizardCommentPage sendExceptionPage;
	private ErrorReportWizardSummaryPage exceptionSummaryPage;
	private ErrorReportWizardScreenShotPage screenShotPage;

	/**
	 * @param errorReport A raw <tt>ErrorReport</tt>. It will be populated with user comment
	 * and other data by this wizard.
	 */
	public ErrorReportWizard(ErrorReport errorReport)
	{
		if (errorReport == null)
			throw new IllegalArgumentException("errorReport is null!"); //$NON-NLS-1$

		this.errorReport = errorReport;
		entryPage = new ErrorReportWizardEntryPage();
		sendExceptionPage = new ErrorReportWizardCommentPage();
		screenShotPage = new ErrorReportWizardScreenShotPage();
		exceptionSummaryPage = new ErrorReportWizardSummaryPage();

		addPage(entryPage);
		addPage(sendExceptionPage);
		addPage(screenShotPage);
		addPage(exceptionSummaryPage);
	}

	/**
	 * Find the sender to use.
	 * 1st: Try to get the selected sender by consulting the config module
	 * 2nd: If this fails or no sender is selected, try to use the default sender
	 * 3rd: If all fails, use the email sender
	 * @return The sender to use
	 */
	private IErrorReportSender getSenderToUse()
	{
		IErrorReportSender sender = null;

		// try selected sender
		try {
			Config configuration = Config.sharedInstance();
			ErrorReportSenderCfMod cfMod  = configuration.createConfigModule(ErrorReportSenderCfMod.class);
			String errorReportSenderId = cfMod.getErrorReportSenderId();
			if(errorReportSenderId != null) {
				ErrorReportSenderDescriptor selectedSender = ErrorReportSenderRegistry.getRegistry().getSenders().get(errorReportSenderId);
				if(selectedSender != null) {
					sender = selectedSender.getInstance();
					logger.info("Using selected error report sender: "+selectedSender.getId()); //$NON-NLS-1$
				}
			}
		} catch(Throwable e) {
			logger.error("Error getting selected error report sender", e); //$NON-NLS-1$
		}


		if(sender == null) {
			// fall back to default sender
			try {
				ErrorReportSenderDescriptor defaultSender = ErrorReportSenderRegistry.getRegistry().getDefaultSender();
				if(defaultSender != null)
					sender = defaultSender.getInstance();
				logger.info("Using default error report sender: "+defaultSender.getId()); //$NON-NLS-1$
			} catch(Throwable e) {
				logger.error("Error getting default error report sender", e); //$NON-NLS-1$
			}
		}

		if(sender == null) {
			// fall back to email
			sender = new ErrorReportSenderEMail();
			logger.info("Using fall-back EMail error report sender"); //$NON-NLS-1$
		}

		return sender;
	}

	@Override
	public boolean performFinish()
	{
		try {
			errorReport.setIsSendScreenShot(screenShotPage.getIsSendsScreenshotImage());
			IErrorReportSender sender = getSenderToUse();
			sender.sendErrorReport(errorReport);
			return true;
		} catch (Throwable e) {
			logger.fatal("Sending ErrorReport failed!", e); //$NON-NLS-1$
			MessageDialog.openError(
					getShell(),
					Messages.getString("org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportWizard.sendingErrorReportFailedDialog.title"), //$NON-NLS-1$
					String.format(
							Messages.getString("org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportWizard.sendingErrorReportFailedDialog.message"), //$NON-NLS-1$
							new Object[] {
								e.getClass().getName(),
								e.getLocalizedMessage(),
								Util.getStackTraceAsString(e)
							}
					)
			);
		}
		return false;
	}

	public ErrorReport getErrorReport()
	{
		return errorReport;
	}
}
