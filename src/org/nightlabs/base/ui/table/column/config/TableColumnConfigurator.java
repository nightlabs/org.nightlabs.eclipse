package org.nightlabs.base.ui.table.column.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.base.ui.resource.Messages;

/**
 *
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class TableColumnConfigurator {

	private static Logger LOGGER = Logger.getLogger(TableColumnConfigurator.class);

	/** {@link IPreferenceStore} used to store properties. */
	private static final IPreferenceStore PREFERENCE_STORE = NLBasePlugin.getDefault().getPreferenceStore();

	/** Preference key under which column configuration will get persisted. */
	private static final String PREFERENCE_KEY = "tradeOverviewResultViewerColumnConfigurationState"; //$NON-NLS-1$

	/** Delimiter used for preference key. */
	private static final String PREFERENCE_KEY_DELIMITER = "_"; //$NON-NLS-1$

	/** Separates column configurations in the preference value. */
	private static final String PREFERENCE_VALUE_MAIN_DELIMITER = ";"; //$NON-NLS-1$

	/** Separates column ID and (visibility,width) tupel in a column configuration. */
	private static final String PREFERENCE_VALUE_SUB_DELIMITER_1 = "="; //$NON-NLS-1$

	/** Separates visibility and width values in a column configuration. */
	private static final String PREFERENCE_VALUE_SUB_DELIMITER_2 = ","; //$NON-NLS-1$

	/** Flag indicating whether the overview has been opened the first time or not. */
	private boolean initialisation = true;

	/** Flag indicating whether a local column width adaptation will get applied. */
	private boolean localWidthAdaptation;

	/** Keeps track of the moment in time the last external table column resize event occurred. */
	private long lastExternalCall;

	/** Flag used to indicate whether an external table column resize event occurred the first time. */
	private boolean performedExternalCall;

	/** Keeps track of the menu items available if the context menu is opened on a table entry. */
	private List<MenuItemWrapper> entrySpecificMenuItems;

	/** True in the case the context menu has not been opened yet. */
	private boolean pristine = true;

	/** Keeps track whether the header context menu has been recently opened or not. */
	private boolean headerSpecificMenuItemsSet;

	/** The column configuration adapter interface. */
	private ITableColumnConfigurationAdapter adapter;

	/** The Table composite. */
	private Table table;

	/** The column configuration model. */
	private TableColumnConfigurationModel model;

	/** Header context menu item for hiding the selected column. */
	private MenuItem itemHide = null;

	/** Header context menu item for opening column configuration dialog. */
	private MenuItem itemConfigure = null;

	/**
	 * Threshold used to indicate whether a table column resize event occurred is considered as being external or not.
	 * In the first case the width of the considered column is reset to the width stored in the model (if overview is
	 * opened at least the 2nd time) to avoid external settings. In the last case the model will get updated with the
	 * current width of the considered table column.
	 */
	private final long threshold = 1000;

	/**
	 * The constructor.
	 * @param adapter
	 */
	public TableColumnConfigurator(final ITableColumnConfigurationAdapter adapter) {
		this.adapter = adapter;
		this.table = adapter.getTable();

		model = new TableColumnConfigurationModel();
		entrySpecificMenuItems = new ArrayList<MenuItemWrapper>();

		table.addListener(SWT.MenuDetect, new TableMenuDetectListener());
		table.addDisposeListener(new TableDisposeListener());

		for (int i = 0; i < table.getColumns().length; i++)
			table.getColumn(i).addControlListener(new ColumnControlListener(i, adapter.getColumnIDs().get(i)));

		performColumnConfiguration();
	}

	/**
	 * Wrapper for menu items that constitute to the table entries' context menu. Note, this is a workaround that has
	 * a minor bug.
	 * TODO resolve workaround
	 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
	 */
	public class MenuItemWrapper {
		private String text;
		private Listener[] listeners;
		private boolean enabled;
		private Image image;
		private int style;

		public MenuItemWrapper(final String text, final Listener[] listeners, final boolean enabled, final Image image, final int style) {
			this.text = text;
			this.listeners = listeners;
			this.enabled = enabled;
			this.image = image;
			this.style = style;
		}

		public String getText() {
			return text;
		}
		public Listener[] getListeners() {
			return listeners;
		}
		public boolean isEnabled() {
			return enabled;
		}
		public Image getImage() {
			return image;
		}
		public int getStyle() {
			return style;
		}
	}

	/**
	 * Initialises the model and additionally configures columns in the case the overview is opened at least
	 * the 2nd time. When the overview is initially opened no settings will be retrieved from preference store.
	 */
	private void performColumnConfiguration() {
		final boolean init = initialiseModel();
		if (!init)
			configureColumns();		// configure columns according to persisted state read out from preference store
	}

	/**
	 * ControlListener registered for every table column. This listener will react to table column width changes dependent
	 * on the origin of the change. If a table column is resized manually by a mouse operation the model will get updated.
	 * If the change is originated by an "external" call it will restore the appropriate width from the model except when
	 * the overview is opened the first time.
	 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
	 */
	private class ColumnControlListener extends ControlAdapter {

		/** The index of the column this listener instance is registered for, i.e. the "real" index (not the visible one!). */
		private int idx;
		/** The ID of the column this listener instance is registered for. */
		private String columnID;

		/**
		 * The constructor.
		 * @param idx The real index (not the visible one!) of the column this listener instance is registered for.
		 * @param columnID The ID of the column this listener instance is registered for.
		 */
		public ColumnControlListener(final int idx, final String columnID) {
			this.idx = idx;
			this.columnID = columnID;
		}

		@Override
		public void controlResized(final ControlEvent e) {
			if (model.getColumnIDsHidden().contains(columnID) || localWidthAdaptation)
				// The column is either hidden or a local programmatical width adaptation is performed => do nothing
				return;
			if (initialisation) {
				if (!performedExternalCall || System.currentTimeMillis() - lastExternalCall < threshold) {
					// The overview is opened the first time. Just set time and flag, but do not adapt any column width here.
					performedExternalCall = true;
					lastExternalCall = System.currentTimeMillis();
				} else {
					// Table column columnID has been resized manually, so update the model.
					model.getColumnIDToColumnWidth().put(columnID, adapter.getTable().getColumn(idx).getWidth());
				}
			} else {
				if (!performedExternalCall || System.currentTimeMillis() - lastExternalCall < threshold) {
					// The overview is opened at least the 2nd time and settings have been read out from preference store.
					// This part will prevent that a column's width is set to a width other than the stored one by someone
					// else (e.g. via async calls).
					final int width = model.getColumnIDToColumnWidth().get(columnID);
					if (table.getColumn(idx).getWidth() != width) {
						localWidthAdaptation = true;
						table.getColumn(idx).setWidth(width);
						table.getColumn(idx).setResizable(true);
						localWidthAdaptation = false;
					}
					performedExternalCall = true;
					lastExternalCall = System.currentTimeMillis();
				} else {
					// Table column columnID has been resized manually, so update the model.
					model.getColumnIDToColumnWidth().put(columnID, adapter.getTable().getColumn(idx).getWidth());
				}
			}
		}
	}

	/**
	 * {@link DisposeListener} implementation registered for the table calling method to persist all table column
	 * configuration settings (order, visibility and width) in the preference store.
	 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
	 */
	private class TableDisposeListener implements DisposeListener {
		@Override
		public void widgetDisposed(final DisposeEvent event) {
			persistColumnConfiguration();
		}
	}

	/**
	 * Listener implementation registered for the table that will get notified when an event of type SWT.MenuDetect occurs.
	 * Dependent on whether the user selected the header or not available menu items will differ. In the first case there
	 * will be two entries, one for hiding the currently selected column and one for opening a dialog to configure special
	 * column settings like order and visibility. Note, at the moment a workaround is used to set these menu items (see
	 * {@link TableColumnConfigurator.MenuItemWrapper}).
	 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
	 */
	private class TableMenuDetectListener implements Listener {

		/** Empty constructor. */
		public TableMenuDetectListener() {}

		@Override
		public void handleEvent(final Event event) {
			final Menu menu = table.getMenu();

			if (pristine) {
				for (int i = 0; i < menu.getItemCount(); i++) {
					final MenuItem item = menu.getItem(i);
					entrySpecificMenuItems.add(new MenuItemWrapper(item.getText(), item.getListeners(SWT.Selection),
						item.getEnabled(), item.getImage(), item.getStyle()));
				}
				pristine = !pristine;
			}

			final Point point = Display.getCurrent().map(null, table, new Point(event.x, event.y));
			final Rectangle clientArea = table.getClientArea();
			final boolean header = clientArea.y <= point.y && point.y < (clientArea.y + table.getHeaderHeight());

			if (header) {
				// Remove all items and add header-specific ones.
				for (int i = 0; i < menu.getItemCount(); i++) {
					menu.getItem(i).dispose();
					i--;
				}
				itemHide = new MenuItem(menu, SWT.NONE);
				itemHide.setText(Messages.getString(
					"org.nightlabs.base.ui.table.column.config.TableColumnConfigurator.TableMenuDetectListener.item1.text")); //$NON-NLS-1$
				itemHide.setEnabled(model.getColumnIDsHidden().size() < model.getColumnIDsOrder().size() ? true : false);
				itemHide.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						final int j = findColumnIdxToHide(point);
						if (j > -1) {
							final String columnID = adapter.getColumnIDs().get(j);
							if (!model.getColumnIDsHidden().contains(columnID))	// should not be the case, but...
								model.getColumnIDsHidden().add(columnID);
							localWidthAdaptation = true;
							table.getColumn(j).setWidth(0);
							table.getColumn(j).setResizable(false);
							localWidthAdaptation = false;
							model.getColumnIDToVisibilityState().put(columnID, false);
						}
					}
				});

				itemConfigure = new MenuItem(menu, SWT.NONE);
				itemConfigure.setText(Messages.getString(
					"org.nightlabs.base.ui.table.column.config.TableColumnConfigurator.TableMenuDetectListener.item2.text")); //$NON-NLS-1$
				itemConfigure.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						final TableColumnConfigurationDialog dialog = new TableColumnConfigurationDialog(TableColumnConfigurator.this,
							table.getShell());
						if (dialog.open() == Window.OK) {
							// Adapt table columns visibility state and order according to settings performed (see model).
							configureColumns();
						}
					}
				});
				headerSpecificMenuItemsSet = true;
			} else {
				// Remove header-specific items if set and set entry-specific items again. If no header-specific items are set
				// nothing happens.
				// TODO dependent on the situation some menu items may not be enabled due to old state being read out
				if (headerSpecificMenuItemsSet) {
					headerSpecificMenuItemsSet = false;
					// Remove header-specific items...
					if (itemHide != null && itemConfigure != null) {
						for (int i = 0; i < menu.getItemCount(); i++) {
							final MenuItem item = menu.getItem(i);
							if (!itemHide.isDisposed() && item.getText() == itemHide.getText()) {
								item.dispose();
								i--;
								continue;
							}
							if (!itemConfigure.isDisposed() && item.getText() == itemConfigure.getText()) {
								item.dispose();
								i--;
							}
						}
					}
					// ...and set entry-specific items again.
					for (final MenuItemWrapper wrapper : entrySpecificMenuItems) {
						final MenuItem item = new MenuItem(menu, wrapper.getStyle());
						item.setEnabled(wrapper.isEnabled());
						item.setImage(wrapper.getImage());
						item.setText(wrapper.getText());
						for (final Listener listener : wrapper.getListeners())
							item.addListener(SWT.Selection, listener);
					}
				}
			}
		}
	}

	/**
	 * ControlListener shared by all hidden columns. As soon as a column gets visible again it will loose the
	 * connection to this listener instance. This listener is some kind of workaround to avoid that a hidden
	 * column's width is set to a width other than 0 by someone else (e.g. via async calls).
	 */
	private ControlListener hiddenColumnsControlListener = new ControlAdapter() {
		@Override
		public void controlResized(final ControlEvent e) {
			if (e.getSource() instanceof TableColumn) {
				final TableColumn column = (TableColumn) e.getSource();
				if (column.getWidth() != 0) {
					localWidthAdaptation = true;
					column.setWidth(0);
					localWidthAdaptation = false;
				}
			}
		}
	};

	/**
	 * Performs column configuration. This method is called when the overview is opened at least the 2nd time and in
	 * the case the settings performed in column configuration dialog shall be applied.
	 */
	private void configureColumns() {
		final int[] order = table.getColumnOrder();
		final int amountOfColumns = model.getColumnIDsOrder().size();
		for (int i = 0; i < amountOfColumns; i++) {		// runs over all visible and hidden columns
			final String columnID = model.getColumnIDsOrder().get(i);
			for (int j = 0; j < amountOfColumns; j++) {
				if (adapter.getColumnIDs().get(j).equals(columnID)) {
					order[i] = j;
					localWidthAdaptation = true;
					if (!model.getColumnIDToVisibilityState().get(columnID)) {
						// i) this column will be hidden now
						table.getColumn(j).addControlListener(hiddenColumnsControlListener);
						table.getColumn(j).setWidth(0);
						table.getColumn(j).setResizable(false);
						if (!model.getColumnIDsHidden().contains(columnID))			// should not be the case, but...
							model.getColumnIDsHidden().add(columnID);
					} else if (model.getColumnIDsHidden().contains(columnID)) {
						// ii) this column was hidden and will now be set visible again
						table.getColumn(j).removeControlListener(hiddenColumnsControlListener);
						table.getColumn(j).setWidth(100);		// TODO which value should be set here?
						table.getColumn(j).setResizable(true);	// TODO which value should be set here?
						model.getColumnIDsHidden().remove(columnID);
					} else {
						// iii) this column was neither hidden nor will it be hidden now
						table.getColumn(j).setWidth(model.getColumnIDToColumnWidth().get(columnID));	// set column width retrieved from model (after dialog settings have been applied) or preference store
					}
					localWidthAdaptation = false;
					break;
				}
			}
		}
		table.setColumnOrder(order);
	}

	/**
	 * Determines the real index of the column that will get hidden.
	 * @param pt Point with mapped coordinates.
	 * @return the real index of the column to be hidden
	 */
	private int findColumnIdxToHide(final Point pt) {
		TableItem dummyItem = null;
		if (table.getItemCount() == 0)
			dummyItem = new TableItem(table, SWT.NONE);

		if (table.getItemCount() > 0) {
			final TableItem item = table.getItem(0);
			for (int i = 0; i < table.getColumnCount(); i++) {
				final Rectangle itemBounds = item.getBounds(i);
				if (itemBounds.x <= pt.x && pt.x < itemBounds.x + itemBounds.width)
					return i;
			}
		}

		if (dummyItem != null)
			table.remove(0);

		return -1;
	}

	void updateColumnVisibilityStatesMap(final TableItem[] items) {
		for (int i = 0; i < items.length; i++)
			// TODO column texts must not be unique, so...
			for (final Map.Entry<String, String> entry : model.getColumnIDToColumnText().entrySet())
				if (entry.getValue().equals(items[i].getText())) {
					model.getColumnIDToVisibilityState().put(entry.getKey(), items[i].getChecked());
					break;
				}
	}

	/**
	 * Persists column configuration in the preference store.
	 */
	private void persistColumnConfiguration() {
		final String value = createColumnConfigurationStateString();
		PREFERENCE_STORE.putValue(buildUpPreferenceKey(PREFERENCE_KEY), value);

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("to be stored in preference store: " + value); //$NON-NLS-1$
	}

	/**
	 * Builds up a preference key using the table's ID and the given Strings as suffix successively. This is the key
	 * under which column configuration will get persisted in the preference store.
	 * @param suffixes The Strings to be appended to the key (separated by delimiter).
	 * @return the complete preference key
	 */
	private String buildUpPreferenceKey(final String... suffixes) {
		final StringBuilder sb = new StringBuilder();
		sb.append(adapter.getTableID());

		for (final String suffix : suffixes)
			sb.append(PREFERENCE_KEY_DELIMITER).append(suffix);

		return sb.toString();
	}

	/**
	 * Builds up a String for the current column configuration taking index, visibility state and width into account.
	 * This one will get persisted in the preference store.
	 * @return the built up configuration String.
	 */
	private String createColumnConfigurationStateString() {
		final StringBuilder sb = new StringBuilder();
		// e.g. 0=true,100;1=false,135;...
		for (final String columnID : model.getColumnIDsOrder()) {
			sb.append(columnID).append(PREFERENCE_VALUE_SUB_DELIMITER_1)	// column ID
				.append(model.getColumnIDToVisibilityState().get(columnID)).append(PREFERENCE_VALUE_SUB_DELIMITER_2)	// visibility state
				.append(model.getColumnIDToColumnWidth().get(columnID)).append(PREFERENCE_VALUE_MAIN_DELIMITER);	// column width
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	/**
	 * Initialises the model. If it is the first time the overview is opened, default values will get applied for certain
	 * properties. Otherwise the model will get initialised with the values read out from preference store.
	 * @return true in the case the overview has been opened the first time, false otherwise
	 */
	private boolean initialiseModel() {
		model.getColumnIDToVisibilityState().clear();
		model.getColumnIDsOrder().clear();
		model.getColumnIDsHidden().clear();
		model.getColumnIDToColumnWidth().clear();

		for (int i = 0; i < adapter.getColumnIDs().size(); i++) {
			final String columnID = adapter.getColumnIDs().get(i);
			model.getColumnIDToColumnText().put(columnID, table.getColumn(i).getText());	// fix mapping, independent of visibility/ordering! e.g. 0->ID,1->Customer,...
		}

		final String prefValue = PREFERENCE_STORE.getString(buildUpPreferenceKey(PREFERENCE_KEY));

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("read out from preference store:   " + prefValue); //$NON-NLS-1$

		if (!prefValue.equals("")) { //$NON-NLS-1$
			initialisation = false;
			final StringTokenizer st1 = new StringTokenizer(prefValue, PREFERENCE_VALUE_MAIN_DELIMITER);

			while (st1.hasMoreTokens()) {
				final String triple = st1.nextToken();	// (columnID,visibility,width)

				final StringTokenizer st2 = new StringTokenizer(triple, PREFERENCE_VALUE_SUB_DELIMITER_1);
				final String columnID = st2.nextToken();
				final String pair = st2.nextToken();	// (visibility,width)

				final StringTokenizer st3 = new StringTokenizer(pair, PREFERENCE_VALUE_SUB_DELIMITER_2);
				final boolean visible = Boolean.parseBoolean(st3.nextToken());
				final int width = Integer.parseInt(st3.nextToken());

				model.getColumnIDsOrder().add(columnID);
				model.getColumnIDToVisibilityState().put(columnID, visible);
				model.getColumnIDToColumnWidth().put(columnID, width);
				if (!visible)
					model.getColumnIDsHidden().add(columnID);
			}
			return false;

		} else {
			// The overview has been opened the first time so there is no persisted state available.
			for (int i = 0; i < table.getColumnCount(); i++) {
				final String columnID = adapter.getColumnIDs().get(i);
				model.getColumnIDToVisibilityState().put(columnID, true);	// all columns visible
				model.getColumnIDsOrder().add(columnID);	// initial order, e.g. '0', '1', '2', '3',...
				table.getColumn(i).pack();
			}
			for (int i = 0; i < table.getColumnCount(); i++) {
				final String columnID = adapter.getColumnIDs().get(i);
				model.getColumnIDToColumnWidth().put(columnID, table.getColumn(i).getWidth());
			}
			return true;
		}
	}

	public List<String> getColumnIDsAfterSorting() {
		return model.getColumnIDsOrder();
	}

	public Map<String, Boolean> getColumnIDToVisibilityState() {
		return model.getColumnIDToVisibilityState();
	}

	public Table getTable() {
		return table;
	}
}
