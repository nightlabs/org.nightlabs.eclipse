/**
 * 
 */
package org.nightlabs.base.ui.timepattern.builder;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.l10n.IDateFormatter;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternFormatException;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PatternExecutionTimeComposite extends XComposite {

	// TODO: Add each X hours and each X minutes

	private final DateTimeEdit startTimeEdit;

	public DateTimeEdit getStartTimeEdit() {
		return startTimeEdit;
	}

	/**
	 * @param parent
	 * @param style
	 */
	public PatternExecutionTimeComposite(final Composite parent, final int style) {
		super(parent, style);
		startTimeEdit = new DateTimeEdit(
				this,
				IDateFormatter.FLAGS_TIME_HMS,
				Messages.getString("org.nightlabs.base.ui.timepattern.builder.PatternExecutionTimeComposite.startTimeEdit.caption") //$NON-NLS-1$
		);
	}

	public void configurePattern(final TimePattern timePattern)
	throws TimePatternFormatException
	{
		final Calendar calendar = new GregorianCalendar();
		calendar.setTime(startTimeEdit.getDate());
		timePattern.setHour(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
		timePattern.setMinute(String.valueOf(calendar.get(Calendar.MINUTE)));

	}
}
