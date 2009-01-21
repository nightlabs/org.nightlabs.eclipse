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
public class MonthlyTimePatternBuilderHop
extends WizardHop
implements ITimePatternSetBuilderWizardHop
{

	/**
	 * 
	 */
	public MonthlyTimePatternBuilderHop(boolean allowEditDate) {
		super(new MonthlyTimePatternBuilderHopPage(allowEditDate));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.timepattern.builder.ITimePatternSetBuilderWizardHop#getHopDescription()
	 */
	public String getHopDescription() {
		return Messages.getString("org.nightlabs.base.ui.timepattern.builder.MonthlyTimePatternBuilderHop.hopDescription"); //$NON-NLS-1$
	}

	public void configureTimePatternSet(TimePatternSet timePatternSet)
	throws TimePatternFormatException
	{
		MonthlyTimePatternBuilderHopPage page = (MonthlyTimePatternBuilderHopPage) getEntryPage();
		page.configurePattern(timePatternSet.createTimePattern());
	}

}
