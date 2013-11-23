package org.nightlabs.base.ui.table.filter;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableColumnFilterDialog extends Dialog 
{
	private TableColumnFilterComposite tableColumnFilterComposite;
	private TableColumnFilter filter;
	
	/**
	 * @param parentShell
	 */
	public TableColumnFilterDialog(Shell parentShell, TableColumnFilter filter) {
		super(parentShell);
		this.filter = filter;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(300, 300);
		newShell.setText("Filter");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		tableColumnFilterComposite = new TableColumnFilterComposite(parent, SWT.NONE, filter);
		tableColumnFilterComposite.setFilter(filter);
		return tableColumnFilterComposite;
	}
	
	public TableColumnFilter getFilter() {
		return filter;
	}
}
