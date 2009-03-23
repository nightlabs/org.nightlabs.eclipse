/**
 * 
 */
package org.nightlabs.base.ui.timepattern.input;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.timepattern.InputTimePattern;

/**
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public class InputTimePatternComposite extends XComposite {

	private static Date testDate = new Date();
	
	private Text year;
	private Text month;
	private Text dayOfMonth;
	private Text hour;
	private Text minute;
	private Text second;
	
	private boolean checkPatternOnFocusOut = false;
	private FocusListener checkPatternFocusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
			if (!checkPatternOnFocusOut)
				return;
			InputTimePattern pattern = getInputTimePattern();
			try {
				pattern.getTime(testDate);
			} catch (Exception ex) {
				// TODO: Show message to the user
			}
		}
	};
	
	/**
	 * @param parent
	 * @param style
	 */
	public InputTimePatternComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.LEFT_RIGHT_WRAPPER);
		getGridLayout().numColumns = 6;
		Label l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.year")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.month")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.day")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.hour")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.minute")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.second")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		year = new Text(this, getBorderStyle() | SWT.CENTER | SWT.CENTER);
		year.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		year.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.y")); //$NON-NLS-1$
		year.addFocusListener(checkPatternFocusListener);
		month = new Text(this, getBorderStyle() | SWT.CENTER);
		month.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		month.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.m")); //$NON-NLS-1$
		month.addFocusListener(checkPatternFocusListener);
		dayOfMonth = new Text(this, getBorderStyle() | SWT.CENTER);
		dayOfMonth.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dayOfMonth.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.d")); //$NON-NLS-1$
		dayOfMonth.addFocusListener(checkPatternFocusListener);
		hour = new Text(this, getBorderStyle() | SWT.CENTER);
		hour.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hour.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.h")); //$NON-NLS-1$
		hour.addFocusListener(checkPatternFocusListener);
		minute = new Text(this, getBorderStyle() | SWT.CENTER);
		minute.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		minute.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.min")); //$NON-NLS-1$
		minute.addFocusListener(checkPatternFocusListener);
		second = new Text(this, getBorderStyle() | SWT.CENTER);
		second.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		second.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.s")); //$NON-NLS-1$
		second.addFocusListener(checkPatternFocusListener);
	}
	
	public void setInputTimePattern(InputTimePattern input) {
		year.setText(input.getYear());
		month.setText(input.getMonth());
		dayOfMonth.setText(input.getDayOfMonth());
		hour.setText(input.getHour());
		minute.setText(input.getMinute());
		second.setText(input.getSecond());
	}

	public InputTimePattern getInputTimePattern() {
		InputTimePattern pattern = new InputTimePattern(
				year.getText().trim(),
				month.getText().trim(),
				dayOfMonth.getText().trim(),
				hour.getText().trim(),
				minute.getText().trim(),
				second.getText().trim()
			);
		return pattern;
	}
	
	public void setCheckPatternOnFocusOut(boolean checkPatternOnFocusOut) {
		this.checkPatternOnFocusOut = checkPatternOnFocusOut;
	}
}
