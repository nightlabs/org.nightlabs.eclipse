/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.wizard.WizardHopPage;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternFormatException;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Marco Schulze - Marco at NightLabs dot de 
 */
public class DailyTimePatternBuilderHopPage extends WizardHopPage {

	private PatternExecutionTimeComposite startTimeEdit;
	private XComposite typeWrapper;

	private Button typeDaily;

	private Button typeChooseDays;
	private XComposite chooseDaysWrapper;
	private Button chooseSun;
	private Button chooseMon;
	private Button chooseTue;
	private Button chooseWed;
	private Button chooseThu;
	private Button chooseFri;
	private Button chooseSat;

	private Button typeEachXDay;
	private XComposite eachXDayWrapper;
	private Spinner eachXDaySpinner;

	/**
	 * @param pageName
	 */
	public DailyTimePatternBuilderHopPage() {
		super(
				DailyTimePatternBuilderHopPage.class.getName(),
				Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.description")); //$NON-NLS-1$
//		setMessage("Configure the time pattern for a daily execution.");
	}

	/**
	 * @param pageName
	 * @param title
	 */
	public DailyTimePatternBuilderHopPage(String pageName, String title) {
		super(pageName, title);
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public DailyTimePatternBuilderHopPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		startTimeEdit = new PatternExecutionTimeComposite(wrapper, SWT.NONE);

		(new Label(wrapper, SWT.SEPARATOR | SWT.HORIZONTAL))
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		typeWrapper = new XComposite(wrapper, SWT.NONE);
		// typeWrapper.setText(NLBasePlugin.getResourceString("timepattern.builderWizard.daily.typeGroup.name"));

		typeWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));
		typeWrapper.setLayout(new GridLayout());
		typeDaily = new Button(typeWrapper, SWT.RADIO);
		typeDaily.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.typeDaily.text")); //$NON-NLS-1$
		typeDaily.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeDaily.addSelectionListener(typeSelectionListener);

		typeChooseDays = new Button(typeWrapper, SWT.RADIO);
		typeChooseDays.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.typeChooseDays.text")); //$NON-NLS-1$
		typeChooseDays.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeChooseDays.addSelectionListener(typeSelectionListener);
		chooseDaysWrapper = new XComposite(typeWrapper, SWT.NONE);
		chooseDaysWrapper.getGridData().horizontalIndent = 30;
		chooseDaysWrapper.setLayout(new RowLayout());
		chooseSun = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseSun.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.chooseSun.text")); //$NON-NLS-1$
		chooseMon = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseMon.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.chooseMon.text")); //$NON-NLS-1$
		chooseTue = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseTue.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.chooseTue.text")); //$NON-NLS-1$
		chooseWed = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseWed.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.chooseWed.text")); //$NON-NLS-1$
		chooseThu = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseThu.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.chooseThu.text")); //$NON-NLS-1$
		chooseFri = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseFri.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.chooseFri.text")); //$NON-NLS-1$
		chooseSat = new Button(chooseDaysWrapper, SWT.CHECK);
		chooseSat.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.chooseSat.text")); //$NON-NLS-1$

		typeEachXDay = new Button(typeWrapper, SWT.RADIO);
		typeEachXDay.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.typeEachXDays.text")); //$NON-NLS-1$
		typeEachXDay.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeEachXDay.addSelectionListener(typeSelectionListener);
		eachXDayWrapper = new XComposite(typeWrapper, SWT.NONE);
		eachXDayWrapper.setLayout(new GridLayout(3, false));
		eachXDayWrapper.getGridData().horizontalIndent = 30;
		Label eachXDayLabel_beforeSpinner = new Label(eachXDayWrapper, SWT.NONE);
		eachXDayLabel_beforeSpinner.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.eachXDayLabel_beforeSpinner.text")); //$NON-NLS-1$
		eachXDaySpinner = new Spinner(eachXDayWrapper, SWT.BORDER);
		eachXDaySpinner.setMaximum(31);
		Label eachXDayLabel_afterSpinner = new Label(eachXDayWrapper, SWT.NONE);
		eachXDayLabel_afterSpinner.setText(Messages.getString("org.nightlabs.base.timepattern.builder.DailyTimePatternBuilderHopPage.eachXDayLabel_afterSpinner.text")); //$NON-NLS-1$

		typeDaily.setSelection(true);

		updateEnabled();
		return wrapper;
	}

	private SelectionListener typeSelectionListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			updateEnabled();
		}
	};

	protected void updateEnabled() {
		chooseDaysWrapper.setEnabled(typeChooseDays.getSelection());
		eachXDayWrapper.setEnabled(typeEachXDay.getSelection());
	}

	public void configureTimePatternSet(TimePattern pattern) 
	throws TimePatternFormatException 
	{
//		TimePattern pattern = timePatternSet.createTimePattern();
		pattern.setYear("*"); //$NON-NLS-1$
		pattern.setMonth("*"); //$NON-NLS-1$
		if (typeDaily.getSelection()) {
			pattern.setDay("*"); //$NON-NLS-1$
			pattern.setDayOfWeek("*"); //$NON-NLS-1$
		}
		else if (typeChooseDays.getSelection()) {
			String dayStr = ""; //$NON-NLS-1$
			if (chooseSun.getSelection()) {
				if (!"".equals(dayStr)) //$NON-NLS-1$
					dayStr = dayStr + ","; //$NON-NLS-1$
				dayStr = dayStr + "0"; //$NON-NLS-1$
			}
			if (chooseMon.getSelection()) {
				if (!"".equals(dayStr)) //$NON-NLS-1$
					dayStr = dayStr + ","; //$NON-NLS-1$
				dayStr = dayStr + "1"; //$NON-NLS-1$
			}
			if (chooseTue.getSelection()) {
				if (!"".equals(dayStr)) //$NON-NLS-1$
					dayStr = dayStr + ","; //$NON-NLS-1$
				dayStr = dayStr + "2"; //$NON-NLS-1$
			}
			if (chooseWed.getSelection()) {
				if (!"".equals(dayStr)) //$NON-NLS-1$
					dayStr = dayStr + ","; //$NON-NLS-1$
				dayStr = dayStr + "3"; //$NON-NLS-1$
			}
			if (chooseThu.getSelection()) {
				if (!"".equals(dayStr)) //$NON-NLS-1$
					dayStr = dayStr + ","; //$NON-NLS-1$
				dayStr = dayStr + "4"; //$NON-NLS-1$
			}
			if (chooseFri.getSelection()) {
				if (!"".equals(dayStr)) //$NON-NLS-1$
					dayStr = dayStr + ","; //$NON-NLS-1$
				dayStr = dayStr + "5"; //$NON-NLS-1$
			}
			if (chooseSat.getSelection()) {
				if (!"".equals(dayStr)) //$NON-NLS-1$
					dayStr = dayStr + ","; //$NON-NLS-1$
				dayStr = dayStr + "6"; //$NON-NLS-1$
			}
			pattern.setDay("*"); //$NON-NLS-1$
			pattern.setDayOfWeek(dayStr);
		}
		else if (typeEachXDay.getSelection()) {
			pattern.setDay("*/"+eachXDaySpinner.getSelection()); //$NON-NLS-1$
			pattern.setDayOfWeek("*"); //$NON-NLS-1$
		}
		
		startTimeEdit.configurePattern(pattern);
	}
}
