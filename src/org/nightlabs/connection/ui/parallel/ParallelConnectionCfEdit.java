package org.nightlabs.connection.ui.parallel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.connection.ui.AbstractConnectionCfEdit;

public class ParallelConnectionCfEdit
extends AbstractConnectionCfEdit
{
	protected Composite _createConnectionCfEditComposite(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE);
		new Label(page, SWT.WRAP).setText("Parallel connections are not yet supported! Sorry!"); //$NON-NLS-1$
		return page;
	}
}
