/**
 * 
 */
package org.nightlabs.base.ui.timepattern.builder;

import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.timepattern.TimePatternFormatException;
import org.nightlabs.timepattern.TimePatternSet;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SingleExecTimePatternBuilderHop
extends WizardHop
implements ITimePatternSetBuilderWizardHop
{

	/**
	 * 
	 */
	public SingleExecTimePatternBuilderHop() {
		super(new SingleExecTimePatternBuilderHopPage());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.timepattern.builder.ITimePatternSetBuilderWizardHop#getHopDescription()
	 */
	public String getHopDescription() {
		return Messages.getString("org.nightlabs.base.ui.timepattern.builder.SingleExecTimePatternBuilderHop.hopDescription"); //$NON-NLS-1$
	}

	public void configureTimePatternSet(TimePatternSet timePatternSet)
	throws TimePatternFormatException
	{
		SingleExecTimePatternBuilderHopPage page = (SingleExecTimePatternBuilderHopPage) getEntryPage();
		page.configureTimePattern(timePatternSet.createTimePattern());
	}

}
