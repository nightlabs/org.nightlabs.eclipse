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
		l.setText("Year");
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText("Month");
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText("Day");
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText("Hour");
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText("Minute");
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText("Second");
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		year = new Text(this, getBorderStyle() | SWT.CENTER | SWT.CENTER);
		year.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		year.setText("Y");
		year.addFocusListener(checkPatternFocusListener);
		month = new Text(this, getBorderStyle() | SWT.CENTER);
		month.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		month.setText("M");
		month.addFocusListener(checkPatternFocusListener);
		dayOfMonth = new Text(this, getBorderStyle() | SWT.CENTER);
		dayOfMonth.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dayOfMonth.setText("D");
		dayOfMonth.addFocusListener(checkPatternFocusListener);
		hour = new Text(this, getBorderStyle() | SWT.CENTER);
		hour.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hour.setText("h");
		hour.addFocusListener(checkPatternFocusListener);
		minute = new Text(this, getBorderStyle() | SWT.CENTER);
		minute.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		minute.setText("m");
		minute.addFocusListener(checkPatternFocusListener);
		second = new Text(this, getBorderStyle() | SWT.CENTER);
		second.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		second.setText("s");
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
