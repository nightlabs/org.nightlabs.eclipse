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
public class MonthlyTimePatternBuilderHop 
extends WizardHop 
implements ITimePatternSetBuilderWizardHop 
{

	/**
	 * 
	 */
	public MonthlyTimePatternBuilderHop() {
		super(new MonthlyTimePatternBuilderHopPage());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.timepattern.builder.ITimePatternSetBuilderWizardHop#getHopDescription()
	 */
	public String getHopDescription() {
		return Messages.getString("org.nightlabs.base.timepattern.builder.MonthlyTimePatternBuilderHop.hopDescription"); //$NON-NLS-1$
	}

	public void configureTimePatternSet(TimePatternSet timePatternSet)
	throws TimePatternFormatException
	{
		MonthlyTimePatternBuilderHopPage page = (MonthlyTimePatternBuilderHopPage) getEntryPage();
		page.configurePattern(timePatternSet.createTimePattern());
	}

}
