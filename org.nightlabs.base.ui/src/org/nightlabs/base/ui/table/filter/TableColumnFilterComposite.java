package org.nightlabs.base.ui.table.filter;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableColumnFilterComposite extends XComposite 
{
	private TableColumnFilter filter;
	private TableColumnFilterTable table;
	private Button selectAllButton;
	
	private SelectionListener tableSelectionListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent evt) {
			filter.setSelectedFilters(new HashSet<String>(table.getCheckedElements()));
		}
		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			widgetSelected(arg0);
		}
	};
	
	/**
	 * @param parent
	 * @param style
	 */
	public TableColumnFilterComposite(Composite parent, int style, final TableColumnFilter filter) {
		super(parent, style);
		table = new TableColumnFilterTable(this, SWT.NONE);
		table.addCheckStateChangedListener(tableSelectionListener);
		setFilter(filter);
		selectAllButton = new Button(this, SWT.CHECK);
		selectAllButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectAllButton.setText("Select All");
		selectAllButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (filter != null) {
					filter.setSelectAll(selectAllButton.getSelection());
					setFilter(filter);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	/**
	 * @return the filter
	 */
	public TableColumnFilter getFilter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(TableColumnFilter filter) {
		this.filter = filter;
		if (filter != null) {
			table.setInput(filter.getPossibleFilterValues());
			table.setCheckedElements(filter.getSelectedFilterValues());
		}
	}

}
