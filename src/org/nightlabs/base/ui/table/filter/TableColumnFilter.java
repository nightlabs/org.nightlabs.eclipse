package org.nightlabs.base.ui.table.filter;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Default implementation of {@link ITableColumnFilter} which can filter the available text values 
 * which are displayed in an {@link TableColumn}.
 * 
 * All possible values are shown by default in a dialog and the once which should be filtered can be selected via checkboxes.
 * The behavior is similar to the column filtering options which are provided by Excel/Access or other spreedsheet applications.
 * 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableColumnFilter
implements ITableColumnFilter
{	
	private ITableLabelProvider labelProvider;
	private Set<String> selectedFilters = new HashSet<String>();
	private Set<String> possibleValues = new TreeSet<String>();
	private int columnIndex;
	private boolean selectAll = false;
	
	public TableColumnFilter(int columnIndex, ITableLabelProvider labelProvider, Set<String> possibleValues) 
	{
		this.columnIndex = columnIndex;
		this.labelProvider = labelProvider;
		this.possibleValues = possibleValues;
	}
	
	@Override
	public boolean filterElement(Object element) 
	{
		String value = labelProvider.getColumnText(element, columnIndex);
		if (!selectedFilters.isEmpty())
			return selectedFilters.contains(value);
		return true;
	}
	
	public void setPossibleFilterValues(Set<String> values) 
	{
		possibleValues = new TreeSet<String>(values);
	}

	public Set<String> getPossibleFilterValues()
	{
		return possibleValues;
	}

	public void addSelectedFilter(String value) 
	{
		if (possibleValues.contains(value))
			selectedFilters.add(value);
	}

	public void setSelectedFilters(Set<String> values)
	{
		if (possibleValues.containsAll(values))
			selectedFilters = values;
	}
	
	public void removeSelectedFilter(String value) 
	{
		if (possibleValues.contains(value))
			selectedFilters.remove(value);
	}
	
	public Set<String> getSelectedFilterValues() 
	{
		return selectedFilters;
	}
	
	public boolean isSelectAll() 
	{
		return selectAll;
	}
	
	public void setSelectAll(boolean selectAll) 
	{
		this.selectAll = selectAll;
		selectedFilters.clear();
		if (selectAll) {
			selectedFilters.addAll(getPossibleFilterValues());			
		}
	}
	
	@Override
	public void clear() 
	{
		selectedFilters.clear();
	}
	
	@Override
	public String getFilterText() {
		String txt = "None"; 
		if (!isEmpty()) {
			txt = getSelectedFilterValues().iterator().next();
		}
		return txt;
	}
	
	@Override
	public ITableColumnFilterActionHandler createActionHandler(Composite parent) {
		return new TableColumnFilterActionHandler(parent, this);
	}
	
	@Override
	public boolean isEmpty() {
		return getSelectedFilterValues().isEmpty();
	}

}
