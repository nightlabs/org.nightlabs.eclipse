/**
 * 
 */
package org.nightlabs.jfire.ui.errorreport.mantis;

import org.eclipse.ui.IStartup;
import org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportSenderCfMod;
import org.nightlabs.config.Config;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 */
public class MantisSubmissionStartup implements IStartup
{
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup()
	{
		ErrorReportSenderCfMod cfMod = Config.sharedInstance().createConfigModule(ErrorReportSenderCfMod.class);
		cfMod.setErrorReportSenderClass(MantisSubmissionModule.class.getName());
	}
}
