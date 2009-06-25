package org.nightlabs.tableprovider.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableProviderTable<Element>
extends AbstractTableComposite<Element>
{
	private TableProviderBuilder tableProviderBuilder;
	private String elementClass;
	private String scope;
	private List<TableColumnLabelProviderPair> tableColumnLabelProviderPairs;

	public TableProviderTable(Composite parent, int style, String elementClass, String scope) {
		super(parent, style, false, DEFAULT_STYLE_MULTI_BORDER);
		this.elementClass = elementClass;
		this.scope = scope;
		tableProviderBuilder = new TableProviderBuilder();
		initTable();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		tableColumnLabelProviderPairs = tableProviderBuilder.createTableColumnLabelProviderPairs(table, elementClass, scope);
		TableLayout tableLayout = new TableLayout();
		configureTableLayout(table, tableLayout);
	}

	protected void configureTableLayout(Table table, TableLayout tableLayout) {
		for (TableColumnLabelProviderPair pair : tableColumnLabelProviderPairs) {
			ColumnWeightData columnData = new ColumnWeightData(1);
			tableLayout.addColumnData(columnData);
		}
		table.setLayout(tableLayout);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		for (TableColumnLabelProviderPair pair : tableColumnLabelProviderPairs) {
			ColumnLabelProvider columnLabelProvider = pair.getColumnLabelProvider();
			TableViewerColumn tvc = new TableViewerColumn(tableViewer, pair.getTableColumn());
			tvc.setLabelProvider(columnLabelProvider);
		}
	}

	public void setElementIDs(Collection elementIDs, String scope, ProgressMonitor monitor) {
		Collection<TableProvider<?, ?>> tableProviders = tableProviderBuilder.getTableProviders(elementClass);
		monitor.beginTask("Loading Elements", tableProviders.size() * 100);
		Map elementID2Element = new HashMap();
		for (TableProvider<?, ?> tp : tableProviders) {
			Map<?, ?> elements = tp.getObjects(elementIDs, scope, new SubProgressMonitor(monitor, 100));
			for (Map.Entry<?, ?> entry : elements.entrySet()) {
				elementID2Element.put(entry.getKey(), entry.getValue());
			}
		}
		setInput(elementID2Element.values());
	}
}
