/**
 * 
 */
package org.nightlabs.eclipse.ui.control.export.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class ExportOptionWizardPage 
extends WizardPage
{
	private final String[] DEFAULT_SEPERATORS = new String[]{",",";",":"};
	
	//UI
	private Combo seperatorCombo;
	
	protected ExportOptionWizardPage(String pageName) {
		super(pageName);
		setTitle("Option");
		setDescription("Description");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL); 
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);
		
		new Label(container, SWT.NONE).setText("Selected Seperator :");
		
		seperatorCombo = new Combo(container, SWT.DROP_DOWN);
		seperatorCombo.setItems(DEFAULT_SEPERATORS);
		GridData gridData = new GridData();
		seperatorCombo.setLayoutData(gridData);
		
		setControl(container);
	}

}
