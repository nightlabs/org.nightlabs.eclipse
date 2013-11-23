package org.nightlabs.base.ui.table.filter;

import org.eclipse.jface.viewers.TableViewer;

/**
 * Interface for adding column filtering support for {@link TableViewer}s.
 * 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface ITableColumnFilterSupport 
{
	/**
	 * Creates column filters for the given {@link TableViewer}.
	 * @param tableViewer the {@link TableViewer} to add column filtering support
	 */
	public void createColumnFilters(TableViewer tableViewer);
}
