package org.nightlabs.base.ui.table.filter;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;

/**
 * Abstract base class which extends {@link AbstractTableComposite} but additionally provides
 * column filtering options like known from common spreedsheet applications 
 * like microsoft excel or open office calc.
 *  
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractTableColumnFilterComposite<E> extends AbstractTableComposite<E>
{
	private ITableColumnFilterSupport tableColumnFilterSupport;
	
	/**
	 * @param parent
	 * @param style
	 */
	public AbstractTableColumnFilterComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void initTable() {
		super.initTable();
		tableColumnFilterSupport = createTableColumnFilterSupport(getTableViewer());
	}
	
	@Override
	public void setInput(Object input) {
		super.setInput(input);
		tableColumnFilterSupport.createColumnFilters(getTableViewer());
	}

	/**
	 * By default this method return a {@link TableColumnFilterSupport}.
	 * Subclasses may override to provide a custom implementation of {@link ITableColumnFilterSupport}.
	 * 
	 * @param tableViewer the {@link TableViewer} to add column filtering support to
	 * @return the implementation of {@link ITableColumnFilterSupport}.
	 */
	protected ITableColumnFilterSupport createTableColumnFilterSupport(TableViewer tableViewer) {
		return new TableColumnFilterSupport(tableViewer);
	}
}
