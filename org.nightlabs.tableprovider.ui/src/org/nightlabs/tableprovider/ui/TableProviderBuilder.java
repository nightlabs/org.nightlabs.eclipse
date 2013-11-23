package org.nightlabs.tableprovider.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableProviderBuilder
{
//	public List<TableColumn> createTableColumns(Table table, String elementClass, String scope) {
//		Collection<TableProvider<?, ?>> tableProviders = getTableProviders(elementClass);
//		List<String> columns = getColumnNames(tableProviders, scope);
//		List<TableColumn> tableColumns = new ArrayList<TableColumn>(columns.size());
//		for (String columnName : columns) {
//			TableColumn tc = new TableColumn(table, SWT.NONE);
//			tc.setText(columnName);
//			tableColumns.add(tc);
//		}
//		return tableColumns;
//	}
//
//	public ColumnLabelProvider createColumnLabelProvider(String elementClass, String scope, Set<String> type) {
//		Collection<TableProvider<?, ?>> tableProviders = getTableProviders(elementClass);
//		TableProviderColumnLabelProvider columnLabelProvider = new TableProviderColumnLabelProvider(tableProviders, type, scope);
//		return columnLabelProvider;
//	}
//
//	public List<ColumnLabelProvider> createColumnLabelProviders(String elementClass, String scope) {
//		Collection<TableProvider<?, ?>> tableProviders = getTableProviders(elementClass);
//		List<Set<String>> typesInOrder = getTypes(tableProviders, scope);
//		List<ColumnLabelProvider> columnLabelProviders = new ArrayList<ColumnLabelProvider>();
//		for (Set<String> types : typesInOrder){
//			ColumnLabelProvider clp = createColumnLabelProvider(elementClass, scope, types);
//			columnLabelProviders.add(clp);
//		}
//		return columnLabelProviders;
//	}
//
//	public List<TableColumnLabelProviderPair> createTableColumnLabelProviderPairs(Table table, String elementClass, String scope) {
//		List<TableColumn> tableColumns = createTableColumns(table, elementClass, scope);
//		List<ColumnLabelProvider> columnLabelProviders = createColumnLabelProviders(elementClass, scope);
//		List<TableColumnLabelProviderPair> pairs = new ArrayList<TableColumnLabelProviderPair>(tableColumns.size());
//		for (int i=0; i<tableColumns.size(); i++){
//			TableColumnLabelProviderPair pair = new TableColumnLabelProviderPair(tableColumns.get(i), columnLabelProviders.get(i));
//			pairs.add(pair);
//		}
//		return pairs;
//	}
//
//	private Collection<TableProvider<?, ?>> tableProviders = null;
//	public Collection<TableProvider<?, ?>> getTableProviders(String elementClass) {
//		if (tableProviders == null) {
//			tableProviders = TableProviderRegistry.sharedInstance().createTableProviders(elementClass);
//		}
//		return tableProviders;
//	}

	public List<String> getColumnNames(Collection<TableProvider<?, ?>> tableProviders, String scope) {
		List<String> columns = new ArrayList<String>();
		for (TableProvider<?, ?> tp : tableProviders) {
			String[] types = tp.getTypes(scope);
			for (int i = 0; i < types.length; i++) {
				String type = types[i];
				String columnName = tp.getTypeName(type);
				if (columns.size() > i) {
					String oldColumnName = columns.get(i);
					if (oldColumnName != null && !oldColumnName.equals(columnName)) {
						columnName = oldColumnName + "/" + columnName;
					}
				}
				if (columns.size() > i) {
					columns.set(i, columnName);
				}
				else {
					columns.add(columnName);
				}
			}
		}
		return columns;
	}

	public List<Set<String>> getTypes(Collection<TableProvider<?, ?>> tableProviders, String scope) {
		List<Set<String>> typesList = new ArrayList<Set<String>>();
		for (TableProvider<?, ?> tp : tableProviders) {
			String[] types = tp.getTypes(scope);
			for (int i = 0; i < types.length; i++) {
				String type = types[i];
				Set<String> typeSet = null;
				if (typesList.size() > i) {
					typeSet = typesList.get(i);
				}
				if (typeSet == null) {
					typeSet = new HashSet<String>();
				}
				typeSet.add(type);
				if (typesList.size() > i)
					typesList.set(i, typeSet);
				else
					typesList.add(typeSet);
			}
		}
		return typesList;
	}

	public List<TableColumn> createTableColumns(Table table, String scope, Collection<TableProvider<?, ?>> tableProviders) {
		List<String> columns = getColumnNames(tableProviders, scope);
		List<TableColumn> tableColumns = new ArrayList<TableColumn>(columns.size());
		for (String columnName : columns) {
			TableColumn tc = new TableColumn(table, SWT.NONE);
			tc.setText(columnName);
			tableColumns.add(tc);
		}
		return tableColumns;
	}

	public List<ColumnLabelProvider> createColumnLabelProviders(String scope, Collection<TableProvider<?, ?>> tableProviders) {
		List<Set<String>> typesInOrder = getTypes(tableProviders, scope);
		List<ColumnLabelProvider> columnLabelProviders = new ArrayList<ColumnLabelProvider>();
		for (Set<String> types : typesInOrder){
			ColumnLabelProvider clp = createColumnLabelProvider(tableProviders, scope, types);
			columnLabelProviders.add(clp);
		}
		return columnLabelProviders;
	}

	public List<TableColumnLabelProviderPair> createTableColumnLabelProviderPairs(Table table,
			String scope, Collection<TableProvider<?, ?>> tableProviders)
	{
		List<TableColumn> tableColumns = createTableColumns(table, scope, tableProviders);
		List<ColumnLabelProvider> columnLabelProviders = createColumnLabelProviders(scope, tableProviders);
		List<TableColumnLabelProviderPair> pairs = new ArrayList<TableColumnLabelProviderPair>(tableColumns.size());
		for (int i=0; i<tableColumns.size(); i++){
			TableColumnLabelProviderPair pair = new TableColumnLabelProviderPair(tableColumns.get(i), columnLabelProviders.get(i));
			pairs.add(pair);
		}
		return pairs;
	}

	public ColumnLabelProvider createColumnLabelProvider(Collection<TableProvider<?, ?>> tableProviders, String scope, Set<String> type) {
		TableProviderColumnLabelProvider columnLabelProvider = new TableProviderColumnLabelProvider(tableProviders, type, scope);
		return columnLabelProvider;
	}

}
