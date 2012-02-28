package org.nightlabs.keyreader.ui.preference;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;

//TODO LSDPreferencePage should only be used when KeyReaderConfigModule comes from server not when used as lib in client
public class KeyReaderPreferencePage
extends PreferencePage
implements IWorkbenchPreferencePage
{
	static {
		System.out.println("KeyReaderPreferencePage static initialiser called"); //$NON-NLS-1$
	}

	private static final Logger logger = Logger.getLogger(KeyReaderPreferencePage.class);
	private KeyReaderPreferenceComposite keyReaderPreferenceComposite;

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		getDefaultsButton().setEnabled(false);
	}

	@Override
	protected void performDefaults()
	{
		logger.debug("performDefaults"); //$NON-NLS-1$
		super.performDefaults();
	}

	@Override
	public boolean performCancel()
	{
		logger.debug("performCancel"); //$NON-NLS-1$
		return super.performCancel();
	}
	@Override
	public boolean performOk()
	{
		logger.debug("performOk"); //$NON-NLS-1$
		keyReaderPreferenceComposite.save();
		return super.performOk();
	}

	@Override
	protected Control createContents(Composite parent) {
		keyReaderPreferenceComposite = new KeyReaderPreferenceComposite(parent, SWT.NONE, LayoutDataMode.NONE);
		updateApplyButton();
		return keyReaderPreferenceComposite;
	}

	@Override
	public void init(IWorkbench arg0) {
	}

}
