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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.nightlabs.base.ui.util.ImageUtil;
import org.nightlabs.config.Config;

//import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Fitas Amine - fitas[at]nightlabs[dot]de
 */
public class ErrorReportSenderEMail implements IErrorReportSender
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ErrorReportSenderEMail.class);

	/**
	 * @see org.nightlabs.IErrorReportSender.wizard.ErrorReportSender#sendErrorReport(org.nightlabs.exceptiontest.wizard.ErrorReport)
	 */
	public void sendErrorReport(ErrorReport errorReport)
	{
		File tempscreenshot = null;
		try {		

			ErrorReportEMailCfMod cfMod = Config.sharedInstance().createConfigModule(ErrorReportEMailCfMod.class);

			Properties props = new Properties();
			props.put("mail.smtp.auth", String.valueOf(cfMod.isAuthenticate())); //$NON-NLS-1$
			props.put("mail.host", cfMod.getSmtpHost()); //$NON-NLS-1$
			props.put("mail.smtp.localhost", cfMod.getSmtpLocalhost()); //$NON-NLS-1$
			props.put("mail.smtp.port", cfMod.getSmtpPort()); //$NON-NLS-1$
			props.put("mail.smtp.starttls.enable", String.valueOf(cfMod.isEnableTLS())); //$NON-NLS-1$
			
			Session mailConnection = Session.getInstance(props, null);		

			Message msg = new MimeMessage(mailConnection);
			Address mailFrom = new InternetAddress(cfMod.getMailFrom());

			msg.setFrom(mailFrom);

			for (String recipient : cfMod.getMailTo())
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));			

			msg.setSubject(errorReport.getFirstThrowable().getClass().getSimpleName() +  " " + errorReport.getTimeAsString()); //$NON-NLS-1$

			if(errorReport.getErrorScreenshot() != null && errorReport.getIsSendScreenShot() == true)
			{
				// create the message part 
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				//fill message
				messageBodyPart.setText(errorReport.toString());

				// Part two is attachment
				MimeBodyPart attachBodyPart = new MimeBodyPart();

				//	Create the screenShot on a file and delete on exit
				try {
					// Create temp file.
					tempscreenshot = File.createTempFile("screenShot", ".jpg"); //$NON-NLS-1$ //$NON-NLS-2$
					// Delete temp file when program exits (in case deletion below fails for whatever reason).
					tempscreenshot.deleteOnExit();

					ImageIO.write(
							ImageUtil.convertToAWT(errorReport.getErrorScreenshot()),
							"JPG", //$NON-NLS-1$
							tempscreenshot
					);

					DataSource source = new FileDataSource(tempscreenshot.getAbsolutePath());
					attachBodyPart.setDataHandler(new DataHandler(source));
					attachBodyPart.setFileName(tempscreenshot.getName());
				} catch (IOException e) {
					logger.fatal("Couldnt save the screenshot on disk.", e); //$NON-NLS-1$
				}

				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				multipart.addBodyPart(attachBodyPart);

				// Put parts in message
				msg.setContent(multipart);
			}
			else
				msg.setText(errorReport.toString());


			if(cfMod.isAuthenticate())
			{
				Transport tr = mailConnection.getTransport("smtp"); //$NON-NLS-1$
				try {
					tr.connect(cfMod.getSmtpHost(), cfMod.getSmtpUsername(), cfMod.getSmtpPassword());
					tr.sendMessage(msg, msg.getAllRecipients());
				} finally {
					tr.close();
				}
			}
			else
				Transport.send(msg);


			logger.info("Message was sent to "+ cfMod.getMailTo().size() +" recipient(s)"); //$NON-NLS-1$ //$NON-NLS-2$

		} catch (Exception e) {
			logger.fatal("Sending error report by eMail failed.", e); //$NON-NLS-1$
		} finally {
			if (tempscreenshot != null)
				tempscreenshot.delete();
		}
	}


}


