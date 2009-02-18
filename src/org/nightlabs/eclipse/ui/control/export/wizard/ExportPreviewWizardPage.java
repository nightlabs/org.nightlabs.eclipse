/**
 *
 */
package org.nightlabs.eclipse.ui.control.export.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.eclipse.ui.control.export.FocusHistory;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class ExportPreviewWizardPage extends WizardPage {

	protected ExportPreviewWizardPage(String pageName) {
		super(pageName);
		setTitle("Preview");
		setDescription("Description");
	}

	@Override
	public void createControl(Composite parent) {
		 Composite container = new Composite(parent, SWT.NULL);
		 GridLayout gridLayout = new GridLayout();
		 container.setLayout(gridLayout);

		 new Label(container, SWT.NONE).setText(":" + FocusHistory.sharedInstance().getItems().getLast().getWidget());

		 setControl(container);
	}

}
