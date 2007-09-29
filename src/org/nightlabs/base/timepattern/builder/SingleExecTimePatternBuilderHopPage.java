/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.composite.DateTimeEdit;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.wizard.WizardHopPage;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternFormatException;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class SingleExecTimePatternBuilderHopPage extends WizardHopPage {

	private PatternExecutionTimeComposite startTimeComposite;
	private DateTimeEdit executionDateEdit;

	/**
	 * @param pageName
	 */
	public SingleExecTimePatternBuilderHopPage() {
		super(SingleExecTimePatternBuilderHopPage.class.getName(), Messages.getString("org.nightlabs.base.timepattern.builder.SingleExecTimePatternBuilderHopPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.base.timepattern.builder.SingleExecTimePatternBuilderHopPage.description")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		startTimeComposite = new PatternExecutionTimeComposite(wrapper, SWT.NONE);
		startTimeComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		(new Label(wrapper, SWT.SEPARATOR | SWT.HORIZONTAL)).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		XComposite dayWrapper = new XComposite(wrapper, SWT.NONE); 
		executionDateEdit = new DateTimeEdit(
				dayWrapper,
				DateFormatter.FLAGS_DATE_LONG_WEEKDAY,
				Messages.getString("org.nightlabs.base.timepattern.builder.SingleExecTimePatternBuilderHopPage.executionDateEdit.caption")); //$NON-NLS-1$
		return wrapper;
	}

	public void configureTimePattern(TimePattern timePattern) 
	throws TimePatternFormatException 
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(executionDateEdit.getDate());
		timePattern.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
		timePattern.setMonth(String.valueOf(calendar.get(Calendar.MONTH)+1));
		timePattern.setDay(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		timePattern.setDayOfWeek(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)-1));
		startTimeComposite.configurePattern(timePattern);
	}
}
