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

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.wizard.DynamicPathWizard;
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

	/**
	 * @param errorReport A raw <tt>ErrorReport</tt>. It will be populated with user comment
	 * and other data by this wizard.
	 */
	public ErrorReportWizard(ErrorReport errorReport)
	{
		this.errorReport = errorReport;
		entryPage = new ErrorReportWizardEntryPage();
		sendExceptionPage = new ErrorReportWizardCommentPage();
		exceptionSummaryPage = new ErrorReportWizardSummaryPage();
		addPage(entryPage);
		addPage(sendExceptionPage);
		addPage(exceptionSummaryPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		try {
//			obtain configuration
			Config configuration = Config.sharedInstance(); 

//			create new ConfigModule or obtain the one read from xml files
			ErrorReportSenderCfMod cfMod  = (ErrorReportSenderCfMod) configuration.createConfigModule(ErrorReportSenderCfMod.class);  			

			Class clazz;
			try {
				clazz = Class.forName(cfMod.getErrorReportSenderClass());
			} catch (Throwable x) {
				throw new ClassNotFoundException(
						"Invalid configuration parameter in \"" + ErrorReportSenderCfMod.class + "\": Unable to load class \"" + cfMod.getErrorReportSenderClass() + "\"!", x);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			}

			if (!ErrorReportSender.class.isAssignableFrom(clazz))
				throw new ClassCastException(
						"Invalid configuration parameter in \"" + ErrorReportSenderCfMod.class + "\": Class \"" + cfMod.getErrorReportSenderClass() + "\" does not implement interface \""+ErrorReportSender.class.getName()+"\"!");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

			ErrorReportSender sender = (ErrorReportSender) clazz.newInstance();
			sender.sendErrorReport(errorReport);
			return true;
		} catch (Throwable e) {
			logger.fatal("Sending ErrorReport failed!", e); //$NON-NLS-1$
			MessageDialog.openError(
					getShell(),
					Messages.getString("org.nightlabs.base.exceptionhandler.errorreport.ErrorReportWizard.sendingErrorReportFailedDialog.title"), //$NON-NLS-1$
					String.format(
							Messages.getString("org.nightlabs.base.exceptionhandler.errorreport.ErrorReportWizard.sendingErrorReportFailedDialog.message"), //$NON-NLS-1$
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
