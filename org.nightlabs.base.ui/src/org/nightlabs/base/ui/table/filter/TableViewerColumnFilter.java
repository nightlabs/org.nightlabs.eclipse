package org.nightlabs.base.ui.table.filter;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * {@link ViewerFilter} which works on the basis of a {@link List} of {@link ITableColumnFilter}.
 * 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableViewerColumnFilter extends ViewerFilter 
{
	private List<ITableColumnFilter> filters;
	
	/**
	 * Creates a new TableViewerColumnFilter.
	 * 
	 * @param filters the {@link List} of {@link ITableColumnFilter} which should be used for filtering.
	 */
	public TableViewerColumnFilter(List<ITableColumnFilter> filters) {
		if (filters == null)
			throw new IllegalArgumentException("param filters must not be null");
		
		this.filters = filters;
	}

	public List<ITableColumnFilter> getFilters() {
		return filters;
	}
	
	public ITableColumnFilter getFilter(int index) {
		if (filters.size() > index)
			return filters.get(index);
		return null;
	}

	/**
	 * Sets the given {@link ITableColumnFilter} at the given index.
	 * 
	 * @param index the index of the filter to set
	 * @param filter the {@link ITableColumnFilter} to set
	 */
	public void setFilter(int index, ITableColumnFilter filter) {
		filters.set(index, filter);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer paramViewer, Object element1, Object element2) {
		for (ITableColumnFilter filter : filters) {
			if (!filter.filterElement(element2))
				return false;
		}
		return true;
	}
	
	/**
	 * Clears all {@link ITableColumnFilter}s.
	 */
	public void clear() {
		for (ITableColumnFilter filter : filters) {
			filter.clear();
		}
	}
}
