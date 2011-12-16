package org.nightlabs.base.ui.table.column.config;

import java.util.List;

import org.eclipse.swt.widgets.Table;

public interface ITableColumnConfigurationAdapter {

	String getTableID();

	Table getTable();

	List<String> getColumnIDs();

}
