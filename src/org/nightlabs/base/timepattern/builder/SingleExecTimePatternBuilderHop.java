/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.wizard.WizardHop;
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
	 * @see org.nightlabs.base.timepattern.builder.ITimePatternSetBuilderWizardHop#getHopDescription()
	 */
	public String getHopDescription() {
		return Messages.getString("org.nightlabs.base.timepattern.builder.SingleExecTimePatternBuilderHop.hopDescription"); //$NON-NLS-1$
	}

	public void configureTimePatternSet(TimePatternSet timePatternSet)
	throws TimePatternFormatException
	{
		SingleExecTimePatternBuilderHopPage page = (SingleExecTimePatternBuilderHopPage) getEntryPage();
		page.configureTimePattern(timePatternSet.createTimePattern());
	}

}
