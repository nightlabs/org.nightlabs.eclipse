package org.nightlabs.base.ui.table.filter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.table.TableLabelProvider;

/**
 * Provides support for column filtering of an existing {@link TableViewer} by
 * adding context menu entries to each {@link TableColumn} of a table which
 * allows the filtering of the {@link TableViewer} in a spreedsheet common manner.
 * 
 * Note: This class only works with an SWT implementation higher than 3.5, 
 * although it also compiles with lower versions.
 * 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TableColumnFilterSupport
implements ITableColumnFilterSupport
{
	public static final String PROPERTY_FILTER_CHANGED = "Filter";
	
	private TableViewer tableViewer;
	private List<ITableColumnFilter> tableColumnFilters;
	private TableViewerColumnFilter viewerFilter;
	private Menu filterMenu;
	private PropertyChangeSupport pcs;
	private Menu oldMenu = null;
	
	public TableColumnFilterSupport() {
		pcs = new PropertyChangeSupport(this);
	}
	
	public TableColumnFilterSupport(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
		pcs = new PropertyChangeSupport(this);
		createColumnFilters(tableViewer);
	}
	
	public void createColumnFilters(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
		if (viewerFilter != null)
			viewerFilter.clear();
		// TODO manage handling of TableColumn images via property change events
		for (TableColumn column : tableViewer.getTable().getColumns()) {
			Image columnFilterImage = getColumnFilterImage();
			if (columnFilterImage != null && columnFilterImage.equals(column.getImage())) {
				column.setImage(null);
			}
		}
		createTableColumnFilter(tableViewer);
		createFilterMenu(tableViewer.getTable());		
	}
	
	protected void createTableColumnFilter(TableViewer tableViewer) {
		tableColumnFilters = createTableColumnFilters(tableViewer.getTable(), getTableLableProvider(tableViewer));
		viewerFilter = new TableViewerColumnFilter(tableColumnFilters);
		tableViewer.addFilter(viewerFilter);
	}

	protected ITableLabelProvider getTableLableProvider(TableViewer tableViewer) 
	{
		if (tableViewer.getLabelProvider() instanceof ITableLabelProvider)
			return (ITableLabelProvider) tableViewer.getLabelProvider();
		else if (tableViewer.getLabelProvider() instanceof LabelProvider) {
			return new TableLabelProviderWrapper((LabelProvider) tableViewer.getLabelProvider());
		}
		else {
			throw new UnsupportedOperationException("Labelprovider must be implement "+ITableLabelProvider.class.getName());
		}
	}
	
	protected void createFilterMenu(final Table table) {
		if (filterMenu == null) {
			filterMenu = new Menu(table.getShell(), SWT.POP_UP);	
		}
		table.addListener(SWT.MenuDetect, new Listener() 
		{
			public void handleEvent(Event event) {
				if (filterMenu != null && !filterMenu.equals(table.getMenu())) {
					oldMenu = table.getMenu();
				}
				TableColumn column = findColumn(table, new Point(event.x, event.y));
				if (column != null) {
					filterMenu.dispose();
					filterMenu = new Menu(table.getShell(), SWT.POP_UP);
					int index = table.indexOf(column);
					ITableColumnFilter filter = null;
					if (tableColumnFilters.size() > index) {
						filter = tableColumnFilters.get(index);
					}
					createMenuItem(filterMenu, column, filter, index);
					table.setMenu(filterMenu);							
				}
				else {
					if (oldMenu != null && !oldMenu.equals(filterMenu)) {
						table.setMenu(oldMenu);	
					} else {
						table.setMenu(null);
					}
				}
			}
		});
		
		/* IMPORTANT: Dispose the menus (only the current menu, set with setMenu(), will be automatically disposed) */
		table.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				if (filterMenu != null)
					filterMenu.dispose();
			}
		});
	}	
	
	protected void createMenuItem(Menu parent, final TableColumn column, final ITableColumnFilter filter, final int columnIndex) {
		final MenuItem menuItem = new MenuItem(parent, SWT.NONE);
		menuItem.setText("Filter "+column.getText());
		menuItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				menuSelected(column, filter, columnIndex);
			}
		});
	}	

	protected void menuSelected(TableColumn column, ITableColumnFilter filter, int columnIndex) {
		ITableColumnFilterActionHandler actionHandler = filter.createActionHandler(column.getParent());
		if (actionHandler.editFilter()) {
			filter = actionHandler.getTableColumnFilter();
			viewerFilter.setFilter(columnIndex, filter);
			tableViewer.refresh();
			if (!filter.isEmpty()) {
				column.setImage(getColumnFilterImage());
			}
			else {
				column.setImage(null);
			}
			pcs.firePropertyChange(PROPERTY_FILTER_CHANGED, null, filter);
		}
	}
	
	protected Image getColumnFilterImage() 
	{
		if (NLBasePlugin.getDefault() != null) {
			return SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
					TableColumnFilterSupport.class, "filterblue", "12x12", ImageFormat.png);			
		}
		return null;
	}
	
	public static final TableColumn findColumn(Table t, Point pt) 
	{
		Point p = t.getDisplay().map(null, t, new Point(pt.x, pt.y));
		final ScrollBar hb = t.getHorizontalBar();
		final int offs = hb != null ? hb.getSelection() : 0;
		final Rectangle area = t.getClientArea();
		boolean header = area.y <= p.y && p.y < (area.y + t.getHeaderHeight());
		final int x0 = area.x + offs;
		final int xp = pt.x + offs;
		if (header && xp >= x0) {
			final int idx[] = t.getColumnOrder();
			int x = 0;
			for (int i = 0; i < idx.length; ++i) {
				final TableColumn col = t.getColumn(idx[i]);
				final int xNext = x + col.getWidth();
				if (xp >= x && xp < xNext) {
					return col;
				}
				x = xNext;
			}
		}
		return null;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
	static class TableLabelProviderWrapper extends TableLabelProvider 
	{
		private LabelProvider labelProvider;
		public TableLabelProviderWrapper(LabelProvider labelProvider) {
			this.labelProvider = labelProvider;
		}
		@Override
		public String getColumnText(Object element, int columnIndex) {
			return labelProvider.getText(element);
		}
	}
	
	/**
	 * 
	 * @param columnIndex the column index for which an ITableColumnFilterCreator should be returned.
	 * By default an {@link TableColumnFilterCreator} is returned.
	 * In case subclasses want to return an custom {@link ITableColumnFilterCreator} the must override.
	 *  
	 * @return the {@link ITableColumnFilterCreator} for the given columnIndex
	 */
	protected ITableColumnFilterCreator createTableColumnFilterCreator(int columnIndex) {
		return new TableColumnFilterCreator(tableViewer.getTable(), getTableLableProvider(tableViewer));
	}

	/**
	 * Creates a {@link List} of {@link ITableColumnFilter}s for each {@link TableColumn} of the given table.
	 * 
	 * @param t the Table to create the {@link ITableColumnFilter}s for.
	 * @param labelProvider the LabelProvider which is necessary for creating the {@link ITableColumnFilter}
	 * @return a {@link List} of {@link ITableColumnFilter}s where the index of the list corresponds to the columnIndex 
	 */
	protected List<ITableColumnFilter> createTableColumnFilters(Table t, ITableLabelProvider labelProvider) 
	{
		List<ITableColumnFilter> tableColumnFilters = new ArrayList<ITableColumnFilter>();
		final int idx[] = t.getColumnOrder();
		for (int i = 0; i < idx.length; ++i) {
			int columnIndex = idx[i];
			ITableColumnFilterCreator tableColumnFilterCreator = createTableColumnFilterCreator(columnIndex);
			ITableColumnFilter tableColumnFilter = tableColumnFilterCreator.createTableColumnFilter(columnIndex);
			tableColumnFilters.add(tableColumnFilter);
		}
		return tableColumnFilters;
	}
	
//	public static List<ITableColumnFilter> createTableColumnFilter(Table table, ITableLabelProvider labelProvider) {
//		List<ITableColumnFilter> tableColumnFilters = new ArrayList<ITableColumnFilter>();
//		Map<Integer, Set<String>> columnIndex2Values = new HashMap<Integer, Set<String>>();
//		for (TableItem item : table.getItems()) {
//			for (int i=0; i<table.getColumnCount(); i++) {
//				Set<String> textValues = columnIndex2Values.get(i);
//				if (textValues == null) {
//					textValues = new HashSet<String>();
//					columnIndex2Values.put(i, textValues);
//				}
//				textValues.add(item.getText(i));
//			}
//		}
//		for (Map.Entry<Integer, Set<String>> entry : columnIndex2Values.entrySet()) {
//			TableColumnFilter tableColumnFilter = new TableColumnFilter(entry.getKey(), labelProvider, entry.getValue());
//			tableColumnFilter.setPossibleFilterValues(entry.getValue());
//			tableColumnFilters.add(tableColumnFilter);
//		}
//		return tableColumnFilters;
//	}
}
