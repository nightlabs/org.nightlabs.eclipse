package org.nightlabs.base.ui.table.filter;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

/**
 * Default implementation of {@link ITableColumnFilterActionHandler} which is used by
 * {@link TableColumnFilter}.
 * 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableColumnFilterActionHandler implements ITableColumnFilterActionHandler 
{
	private Composite parent;
	private TableColumnFilter filter;
	
	public TableColumnFilterActionHandler(Composite parent, TableColumnFilter filter) {
		this.parent = parent;
		this.filter = filter;
	}
	
	@Override
	public boolean editFilter() {
		TableColumnFilterDialog dialog = new TableColumnFilterDialog(parent.getShell(), filter);
		if (dialog.open() == Window.OK) {
			filter = dialog.getFilter();
			return true;
		}
		return false;
	}
	
	@Override
	public ITableColumnFilter getTableColumnFilter() {
		return filter;
	}
}
