/**
 * 
 */
package org.nightlabs.base.timepattern.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.wizard.DynamicPathWizard;
import org.nightlabs.base.wizard.WizardHopPage;
import org.nightlabs.timepattern.TimePatternFormatException;
import org.nightlabs.timepattern.TimePatternSet;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TimePatternSetBuilderEntryWizardPage extends WizardHopPage {

	private List<ITimePatternSetBuilderWizardHop> builderHops = new ArrayList<ITimePatternSetBuilderWizardHop>();
	private ITimePatternSetBuilderWizardHop currentBuilderHop;
	
	public TimePatternSetBuilderEntryWizardPage() {		
		super(
				TimePatternSetBuilderEntryWizardPage.class.getName(), 
				Messages.getString("org.nightlabs.base.timepattern.builder.TimePatternSetBuilderEntryWizardPage.title") //$NON-NLS-1$
			);
		setDescription(Messages.getString("org.nightlabs.base.timepattern.builder.TimePatternSetBuilderEntryWizardPage.description")); //$NON-NLS-1$
	}

	/**
	 * @param pageName
	 */
	public TimePatternSetBuilderEntryWizardPage(String pageName) {
		super(pageName);
	}

	/**
	 * @param pageName
	 * @param title
	 */
	public TimePatternSetBuilderEntryWizardPage(String pageName, String title) {
		super(pageName, title);
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public TimePatternSetBuilderEntryWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	public void addBuilderHop(ITimePatternSetBuilderWizardHop builderHop) {
		builderHops.add(builderHop);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		for (ITimePatternSetBuilderWizardHop builderHop : builderHops) {
			Button button = new Button(wrapper, SWT.RADIO);
			button.setData(builderHop);
			button.setText(builderHop.getHopDescription());
			button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			button.addSelectionListener(builderSelectListener);
		}
		return wrapper;
	}

	private SelectionListener builderSelectListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() instanceof Button) {
				Object bo = ((Button)e.getSource()).getData();
				if (bo instanceof ITimePatternSetBuilderWizardHop) {
					if (currentBuilderHop != null) {
						if (getWizard() instanceof DynamicPathWizard) {
							DynamicPathWizard wiz = (DynamicPathWizard)getWizard();
							wiz.removeDynamicWizardPage(currentBuilderHop.getEntryPage());
						}
					}
					currentBuilderHop = (ITimePatternSetBuilderWizardHop)bo;
					if (currentBuilderHop != null) {
						if (getWizard() instanceof DynamicPathWizard) {
							DynamicPathWizard wiz = (DynamicPathWizard)getWizard();
							wiz.addDynamicWizardPage(currentBuilderHop.getEntryPage());
						}					
					}
					getContainer().updateButtons();
				}
			}
		}
		
	};

	public void configureTimePatternSet(TimePatternSet timePatternSet) throws TimePatternFormatException {
		currentBuilderHop.configureTimePatternSet(timePatternSet);
	}
}
