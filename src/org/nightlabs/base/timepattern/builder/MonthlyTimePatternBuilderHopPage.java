/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.wizard.WizardHopPage;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternFormatException;

/**
 * @author alex
 *
 */
public class MonthlyTimePatternBuilderHopPage extends WizardHopPage {

	private PatternExecutionTimeComposite startTimeComposite;

	private Group dayWrapper;
	private SortedSet<Integer> selectedDays = new TreeSet<Integer>();
	private Group monthWrapper;
	private SortedSet<Integer> selectedMonths = new TreeSet<Integer>();

	public MonthlyTimePatternBuilderHopPage() {
		super(MonthlyTimePatternBuilderHopPage.class.getName(), Messages.getString("org.nightlabs.base.timepattern.builder.MonthlyTimePatternBuilderHopPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.base.timepattern.builder.MonthlyTimePatternBuilderHopPage.description")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		startTimeComposite = new PatternExecutionTimeComposite(wrapper, SWT.NONE);
		(new Label(wrapper, SWT.SEPARATOR | SWT.HORIZONTAL)).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		XComposite selectionWrapper = new XComposite(wrapper, SWT.NONE);
		dayWrapper = new Group(selectionWrapper, SWT.NONE);
		dayWrapper.setText(Messages.getString("org.nightlabs.base.timepattern.builder.MonthlyTimePatternBuilderHopPage.dayWrapper.text")); //$NON-NLS-1$
		dayWrapper.setLayout(new GridLayout(11,true));
		dayWrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (int i = 1; i <= 31; i++) {
			Button dayButton = new Button(dayWrapper, SWT.CHECK);
			dayButton.setText(Integer.toString(i));
			dayButton.setData(new Integer(i));
			dayButton.addSelectionListener(daySelectListner);
		}
		monthWrapper = new Group(selectionWrapper, SWT.NONE);
		monthWrapper.setText(Messages.getString("org.nightlabs.base.timepattern.builder.MonthlyTimePatternBuilderHopPage.monthWrapper.text")); //$NON-NLS-1$
		monthWrapper.setLayout(new GridLayout(4,true));
		monthWrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (int i = 1; i <= 12; i++) {
			Button monthButton = new Button(monthWrapper, SWT.CHECK);
			monthButton.setText(Messages.getString("org.nightlabs.base.timepattern.builder.MonthlyTimePatternBuilderHopPage.month" + i + ".text")); //$NON-NLS-1$  //$NON-NLS-2$
			monthButton.setData(new Integer(i));
			monthButton.setSelection(true);
			selectedMonths.add((Integer) monthButton.getData());
			monthButton.addSelectionListener(monthSelectListner);
		}
		return wrapper;
	}

	private SelectionListener daySelectListner = new SelectionListener () {
		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() instanceof Button) {
				Button dayButton = (Button) e.getSource();
				if (dayButton.getSelection())
					selectedDays.add((Integer)dayButton.getData());
				else
					selectedDays.remove((Integer)dayButton.getData());
			}
		}
		
	};
	
	private SelectionListener monthSelectListner = new SelectionListener () {
		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() instanceof Button) {
				Button dayButton = (Button) e.getSource();
				if (dayButton.getSelection())
					selectedMonths.add((Integer)dayButton.getData());
				else
					selectedMonths.remove((Integer)dayButton.getData());
			}
		}
		
	};
	
	public void configurePattern(TimePattern timePattern) 
	throws TimePatternFormatException 
	{
		timePattern.setYear("*"); //$NON-NLS-1$
		startTimeComposite.configurePattern(timePattern);
		if (selectedDays.size() == 31)
			timePattern.setDay("*"); //$NON-NLS-1$
		else
			timePattern.setDay(getNumSelectionString(selectedDays));
		
		timePattern.setDayOfWeek("*"); //$NON-NLS-1$
		
		if (selectedMonths.size() == 12)
			timePattern.setMonth("*"); //$NON-NLS-1$
		else
			timePattern.setMonth(getNumSelectionString(selectedMonths));
		
	}
	
	private String getNumSelectionString(SortedSet<Integer> selected){
		int lastValue = -1;
		int lastWritten = -1;
		boolean addInterval = false;
		String selString = ""; //$NON-NLS-1$
		for (Iterator it = selected.iterator(); it.hasNext();) {
			Integer selValue = (Integer) it.next();
			if ((lastValue+1) == selValue) {
				addInterval = true;
				lastValue = selValue;
				if (it.hasNext())
					continue;
			}
			if (addInterval) {
				if (lastWritten <= 0) {
					selString = "1"; //$NON-NLS-1$
					if (selValue != 1)
						selString = selString + "-" + lastValue; //$NON-NLS-1$
				} 
				else {
					selString = selString + "-" + lastValue; //$NON-NLS-1$
					if (!it.hasNext())
						break;
				}
			}
			if (!"".equals(selString)) //$NON-NLS-1$
				selString = selString + ","; //$NON-NLS-1$
			selString = selString + selValue;
			lastValue = selValue;
			lastWritten = selValue;
			addInterval = false;
		}
		return selString;
	}
}
