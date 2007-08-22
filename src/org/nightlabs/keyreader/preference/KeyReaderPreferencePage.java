package org.nightlabs.keyreader.preference;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComposite.LayoutDataMode;
import org.nightlabs.jfire.base.preferences.LSDPreferencePage;

//TODO LSDPreferencePage should only be used when KeyReaderConfigModule comes from server not when used as lib in client
public class KeyReaderPreferencePage
extends LSDPreferencePage
{
	private static final Logger logger = Logger.getLogger(KeyReaderPreferencePage.class);
	private KeyReaderPreferenceComposite keyReaderPreferenceComposite;

	@Override
	public void createPartContents(Composite parent)
	{
		keyReaderPreferenceComposite = new KeyReaderPreferenceComposite(parent, SWT.NONE, LayoutDataMode.NONE);
		updateApplyButton();
	}

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		getDefaultsButton().setEnabled(false);
	}

	@Override
	protected void performDefaults()
	{
		logger.debug("performDefaults");
		super.performDefaults();
	}

	@Override
	public boolean performCancel()
	{
		logger.debug("performCancel");
		return super.performCancel();
	}
	@Override
	public boolean performOk()
	{
		logger.debug("performOk");
		keyReaderPreferenceComposite.save();
		return super.performOk();
	}

}
