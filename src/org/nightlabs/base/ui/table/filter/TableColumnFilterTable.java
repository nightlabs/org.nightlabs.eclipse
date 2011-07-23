package org.nightlabs.base.ui.table.filter;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableColumnFilterTable extends AbstractTableComposite<String> 
{
	/**
	 * @param parent
	 * @param style
	 */
	public TableColumnFilterTable(Composite parent, int style) {
		super(parent, style, true, SWT.CHECK | SWT.BORDER | SWT.SINGLE);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc = new TableColumn(table, SWT.NONE);
		tc.setText("Name");
		WeightedTableLayout layout = new WeightedTableLayout(new int[] {1});
//		layout.setReduceHorizontalScrollbarFix(true); // If necessary, CompatibleSWT.getVerticalScrollBarWidth(...) should be modified as this is now always used by WeightedTableLayout - without enabling a fix like this.
		table.setLayout(layout);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setSorter(new ViewerSorter());
	}
}
