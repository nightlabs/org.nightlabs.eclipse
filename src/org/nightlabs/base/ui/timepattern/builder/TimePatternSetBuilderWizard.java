/**
 * 
 */
package org.nightlabs.base.ui.timepattern.builder;

import java.util.Iterator;

import org.eclipse.jface.window.Window;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternFormatException;
import org.nightlabs.timepattern.TimePatternSet;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TimePatternSetBuilderWizard extends DynamicPathWizard {

	private TimePatternSetBuilderEntryWizardPage entryPage;
	private TimePatternSet timePatternSet;
	private boolean clearBeforeBuild;

	
	public TimePatternSetBuilderWizard(TimePatternSet timePatternSet) {
		this(timePatternSet, true);
	}
	
	/**
	 * 
	 */
	public TimePatternSetBuilderWizard(TimePatternSet timePatternSet, boolean clearBeforeBuild) {
		this.clearBeforeBuild = clearBeforeBuild;
		this.timePatternSet = timePatternSet;
		entryPage = new TimePatternSetBuilderEntryWizardPage();
		addPage(entryPage);
	}

	public void addBuilderHop(ITimePatternSetBuilderWizardHop builderHop) {
		entryPage.addBuilderHop(builderHop);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (clearBeforeBuild)
			timePatternSet.clearTimePatterns();
		try {
			entryPage.configureTimePatternSet(timePatternSet);
		} catch (TimePatternFormatException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	public static boolean open(TimePatternSet timePatternSet) {
			return  open(timePatternSet,true);
	}

	public static boolean open(TimePatternSet timePatternSet,boolean allowEditDate) {
		TimePatternSetBuilderWizard wiz = new TimePatternSetBuilderWizard(timePatternSet);
		wiz.addBuilderHop(new DailyTimePatternBuilderHop(allowEditDate));
		wiz.addBuilderHop(new MonthlyTimePatternBuilderHop(allowEditDate));
		wiz.addBuilderHop(new SingleExecTimePatternBuilderHop(allowEditDate));
		DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wiz);
		return dlg.open() == Window.OK;
	}



}
