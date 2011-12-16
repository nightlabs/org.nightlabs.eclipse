package org.nightlabs.base.ui.table.column.config;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;

/**
 *
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class TableColumnConfigurationDialog extends ResizableTitleAreaDialog {

	private TableColumnConfigurator configurator;
	private Table configTable;

	private Button buttonMoveUp;
	private Button buttonMoveDown;
	private Button buttonSelectAll;
	private Button buttonUnselectAll;

	public TableColumnConfigurationDialog(final TableColumnConfigurator configurator, final Shell parentShell) {
		super(parentShell, null);
		this.configurator = configurator;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		setTitle(Messages.getString(
			"org.nightlabs.base.ui.table.column.config.TableColumnConfigurationDialog.title")); //$NON-NLS-1$
		setMessage(Messages.getString(
			"org.nightlabs.base.ui.table.column.config.TableColumnConfigurationDialog.message")); //$NON-NLS-1$

		final Composite wrapper = new Composite(parent, SWT.NONE);
		final int numColumns = 3;
		wrapper.setLayout(new GridLayout(numColumns, false));

		final GridData gridData = new GridData(GridData.FILL_BOTH);
		wrapper.setLayoutData(gridData);

//		configurator.initialiseModel();

		createTable(wrapper);
		createButtons(wrapper, numColumns);

		return wrapper;
	}

	private void createTable(final Composite wrapper) {
		configTable = new Table(wrapper, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		configTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				if (event.detail == SWT.CHECK) {
					configurator.updateColumnVisibilityStatesMap(configTable.getItems());
					updateSelectionButtons();
				}
				if (event.detail != SWT.CHECK)
					updateButtonMoveUpDownStates();
			}
		});
		configTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				if (configTable.getSelectionIndex() > -1) {
					final TableItem item = configTable.getItem(configTable.getSelectionIndex());
					item.setChecked(!item.getChecked());
					configurator.updateColumnVisibilityStatesMap(configTable.getItems());
					updateSelectionButtons();
				}
			}
		});

		for (int i = 0; i < configurator.getColumnIDsAfterSorting().size(); i++) {
			final TableItem item = new TableItem(configTable, SWT.NONE);
			item.setText(configurator.getTable().getColumn(Integer.parseInt(configurator.getColumnIDsAfterSorting().get(i))).getText());
			final Object value = configurator.getColumnIDToVisibilityState().get(configurator.getColumnIDsAfterSorting().get(i));
			item.setChecked(value == null ? true : (Boolean) value);
		}
	}

	private void createButtons(final Composite wrapper, final int numColumns) {
		GridData gridData = new GridData();
		gridData.widthHint = 50;

		buttonMoveUp = new Button(wrapper, SWT.PUSH);
		buttonMoveUp.setEnabled(false);
		buttonMoveUp.setText(Messages.getString(
			"org.nightlabs.base.ui.table.column.config.TableColumnConfigurationDialog.buttonMoveUp.text")); //$NON-NLS-1$
		buttonMoveUp.setLayoutData(gridData);
		buttonMoveUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateColumnOrder(true);
				updateButtonMoveUpDownStates();
			}
		});

		buttonMoveDown = new Button(wrapper, SWT.PUSH);
		buttonMoveDown.setEnabled(false);
		buttonMoveDown.setText(Messages.getString(
			"org.nightlabs.base.ui.table.column.config.TableColumnConfigurationDialog.buttonMoveDown.text")); //$NON-NLS-1$
		buttonMoveDown.setLayoutData(gridData);
		buttonMoveDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateColumnOrder(false);
				updateButtonMoveUpDownStates();
			}
		});

		gridData = new GridData();
		gridData.widthHint = 80;
		gridData.horizontalSpan = numColumns;

		buttonSelectAll = new Button(wrapper, SWT.PUSH);
		buttonSelectAll.setText(Messages.getString(
			"org.nightlabs.base.ui.table.column.config.TableColumnConfigurationDialog.buttonSelectAll.text")); //$NON-NLS-1$
		buttonSelectAll.setLayoutData(gridData);
		buttonSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				for (int i = 0; i < configTable.getItemCount(); i++)
					configTable.getItem(i).setChecked(true);
				configurator.updateColumnVisibilityStatesMap(configTable.getItems());
				updateSelectionButtons();
			}
		});
		updateButtonSelectAllState();

		buttonUnselectAll = new Button(wrapper, SWT.PUSH);
		buttonUnselectAll.setText(Messages.getString(
			"org.nightlabs.base.ui.table.column.config.TableColumnConfigurationDialog.buttonUnselectAll.text")); //$NON-NLS-1$
		buttonUnselectAll.setLayoutData(gridData);
		buttonUnselectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				for (int i = 0; i < configTable.getItemCount(); i++)
					configTable.getItem(i).setChecked(false);
				configurator.updateColumnVisibilityStatesMap(configTable.getItems());
				updateSelectionButtons();
			}
		});
		updateButtonUnselectAllState();
	}

	private void updateColumnOrder(final boolean up) {
		final int sourceIdx = configTable.getSelectionIndex();
		final int targetIdx = up ? sourceIdx - 1 : sourceIdx + 1;

		final List<String> columnIDsAfterSorting = configurator.getColumnIDsAfterSorting();
		final String targetColumnID = columnIDsAfterSorting.get(targetIdx);
		columnIDsAfterSorting.set(targetIdx, columnIDsAfterSorting.get(sourceIdx));
		columnIDsAfterSorting.set(sourceIdx, targetColumnID);

		final TableItem targetItem = configTable.getItem(targetIdx);
		final TableItem sourceItem = configTable.getItem(sourceIdx);

		final String targetItemText = targetItem.getText();
		final Boolean targetItemChecked = targetItem.getChecked();

		targetItem.setText(sourceItem.getText());
		targetItem.setChecked(sourceItem.getChecked());
		sourceItem.setText(targetItemText);
		sourceItem.setChecked(targetItemChecked);

		configTable.setSelection(targetIdx);
	}

	private void updateButtonMoveUpDownStates() {
		buttonMoveUp.setEnabled(configTable.getSelectionIndex() > 0);
		buttonMoveDown.setEnabled(configTable.getSelectionIndex() < configTable.getItemCount() - 1);
	}

	private void updateSelectionButtons() {
		updateButtonSelectAllState();
		updateButtonUnselectAllState();
	}

	private void updateButtonSelectAllState() {
		boolean allChecked = true;
		for (final TableItem item : configTable.getItems()) {
			if (!item.getChecked()) {
				allChecked = false;
				break;
			}
		}
		buttonSelectAll.setEnabled(!allChecked);
	}

	private void updateButtonUnselectAllState() {
		boolean someChecked = false;
		for (final TableItem item : configTable.getItems()) {
			if (item.getChecked()) {
				someChecked = true;
				break;
			}
		}
		buttonUnselectAll.setEnabled(someChecked);
	}
}
