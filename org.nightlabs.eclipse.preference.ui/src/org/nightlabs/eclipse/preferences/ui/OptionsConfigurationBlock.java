package org.nightlabs.eclipse.preferences.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.nightlabs.eclipse.preferences.ui.resource.Messages;

/**
 * An abstract options configuration block with support for tables
 * and some more convenience.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision: 2199 $ - $Date: 2008-02-08 14:14:15 +0100 (Fr, 08 Feb 2008) $
 */
public abstract class OptionsConfigurationBlock extends SimpleOptionsConfigurationBlock
{
	private Map<TableViewer, Key> tables;
	private Map<Control, List<Control>> multiControls;

	/**
	 * Create a new OptionsConfigurationBlock.
	 * @param statusListener The status listener
	 * @param project The project if this is created for a project property page
	 * @param allKeys The configuration keys
	 * @param container the workbench container
	 */
	public OptionsConfigurationBlock(IStatusChangeListener statusListener, IProject project, Key[] allKeys, IWorkbenchPreferenceContainer container)
	{
		super(statusListener, project, allKeys, container);
		tables = new HashMap<TableViewer, Key>();
		multiControls = new HashMap<Control, List<Control>>();
	}

//	private boolean scrollableContents = true;

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.preferences.ui.SimpleOptionsConfigurationBlock#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Composite parentToUse = parent;
//		ScrolledPageContent scrolled = null;
//		if(scrollableContents) {
//		scrolled= new ScrolledPageContent(parent, SWT.H_SCROLL | SWT.V_SCROLL);
//		scrolled.setExpandHorizontal(true);
//		scrolled.setExpandVertical(true);
//		parentToUse = scrolled;
//		}

		Composite subComposite = new Composite(parentToUse, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
//		layout.horizontalSpacing = 0;
//		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		subComposite.setLayout(layout);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = GridData.FILL;
		subComposite.setLayoutData(gd);

		doCreateContents(subComposite);

//		if(scrollableContents) {
//		scrolled.setContent(subComposite);
//		final Point size= subComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//		scrolled.setMinSize(size.x, size.y);
//		}
		return parentToUse;
	}

	protected abstract Control doCreateContents(Composite parent);

//	protected void noScrollableContents()
//	{
//	scrollableContents = false;
//	}

	protected static Control fillHorizontal(Control c)
	{
		GridData gd= (GridData) c.getLayoutData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		return c;
	}

	protected static Control fillVertical(Control c)
	{
		GridData gd= (GridData) c.getLayoutData();
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		return c;
	}

	protected static Control adaptWidthHint(Control c, int widthHint)
	{
		if(widthHint == -1)
			fillHorizontal(c);
		else if(widthHint > 0) {
			GridData gd = (GridData) c.getLayoutData();
			gd.widthHint = widthHint;
		} else {
			GridData gd = (GridData) c.getLayoutData();
			gd.widthHint = SWT.DEFAULT;
		}
		return c;
	}


	protected Composite addSpacer(Composite parent, int width, int height)
	{
		if(width <= 0)
			width = 1;
		if(height <= 0)
			height = 1;
		Composite spacer = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		spacer.setLayout(layout);
		GridData gd = new GridData();
		gd.heightHint = height;
		gd.widthHint = width;
		spacer.setLayoutData(gd);
		return spacer;
	}

	public static final int BUTTON_ADD = 1;
	public static final int BUTTON_EDIT = 2;
	public static final int BUTTON_REMOVE = 4;
	public static final int ALL_BUTTONS = BUTTON_ADD | BUTTON_EDIT | BUTTON_REMOVE;

	private TableActionListener defaultTableActionListener = new TableActionListener()
	{
		/* (non-Javadoc)
		 * @see org.nightlabs.eclipse.preferences.ui.TableActionListener#add(org.eclipse.jface.viewers.TableViewer, org.nightlabs.eclipse.preferences.ui.SimpleOptionsConfigurationBlock.Key, org.eclipse.swt.events.SelectionEvent)
		 */
		@SuppressWarnings("unchecked")
		public void add(TableViewer tableViewer, Key key, SelectionEvent event)
		{
			InputDialog dlg = new InputDialog(getShell(), Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addInputDialog.title"), Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addInputDialog.message"), "", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if(dlg.open() == Window.OK) {
				List<String> input = (List<String>)tableViewer.getInput();
				input.add(dlg.getValue());
				setValue(key, PreferencesUtil.serialize(input));
				int[] indices = tableViewer.getTable().getSelectionIndices();
				tableViewer.refresh();
				tableViewer.getTable().setSelection(indices);
			}
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.eclipse.preferences.ui.TableActionListener#edit(org.eclipse.jface.viewers.TableViewer, org.nightlabs.eclipse.preferences.ui.SimpleOptionsConfigurationBlock.Key, org.eclipse.swt.events.SelectionEvent)
		 */
		@SuppressWarnings("unchecked")
		public void edit(TableViewer tableViewer, Key key, SelectionEvent event)
		{
			int[] indices = tableViewer.getTable().getSelectionIndices();
			if(indices.length == 0)
				return;
			for (int i : indices) {
				String selectedElement = (String)tableViewer.getElementAt(i);
				InputDialog dlg = new InputDialog(getShell(), Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.editInputDialog.title"), Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.editInputDialog.message"), selectedElement, null); //$NON-NLS-1$ //$NON-NLS-2$
				if(dlg.open() == Window.OK) {
					List<String> input = (List<String>)tableViewer.getInput();
					input.set(i, dlg.getValue());
					setValue(key, PreferencesUtil.serialize(input));
					tableViewer.refresh();
					tableViewer.getTable().setSelection(indices);
				} else {
					break;
				}
			}
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.eclipse.preferences.ui.TableActionListener#remove(org.eclipse.jface.viewers.TableViewer, org.nightlabs.eclipse.preferences.ui.SimpleOptionsConfigurationBlock.Key, org.eclipse.swt.events.SelectionEvent)
		 */
		@SuppressWarnings("unchecked")
		public void remove(TableViewer tableViewer, Key key, SelectionEvent event)
		{
			List<String> input = (List<String>)tableViewer.getInput();
			int[] indices = tableViewer.getTable().getSelectionIndices();
			for (int i=indices.length-1; i>=0; i--) {
				input.remove(indices[i]);
			}
			setValue(key, PreferencesUtil.serialize(input));
			tableViewer.refresh();
			tableViewer.getTable().setSelection(indices[0]);
		}
	};

	/**
	 * Add a table composite with all buttons and default table action
	 * listener.
	 * @param parent The parent composite
	 * @param labelText The label text for the label to place above the
	 * 		table or <code>null</code> for not to use a label
	 * @param key The preference key
	 * @param widthHint The width hint for the table
	 * @param indent the table indentation in pixels
	 * @return The table viewer
	 */
	protected TableViewer addTable(Composite parent, String labelText, final Key key, int indent, int widthHint)
	{
		return addTable(parent, labelText, key, ALL_BUTTONS, defaultTableActionListener, indent, widthHint);
	}

	/**
	 * Add a table composite.
	 * @param parent The parent composite
	 * @param labelText The label text for the label to place above the
	 * 		table or <code>null</code> for not to use a label
	 * @param key The preference key
	 * @param buttons Which buttons to add. Bitwise combination of 
	 * 		{@link #BUTTON_ADD}, {@link #BUTTON_EDIT} and {@link #BUTTON_REMOVE}.
	 * @param tableActionListener A table action listener or <code>null</code>
	 * 		to use a default listener.
	 * @param widthHint The width hint for the table
	 * @param indent the table indentation in pixels
	 * @return The table viewer
	 */
	protected TableViewer addTable(Composite parent, String labelText, final Key key, int buttons, final TableActionListener tableActionListener, int indent, int widthHint)
	{
		GridData gd;

		if(labelText != null) {
			Label label = new Label(parent, SWT.WRAP);
			label.setText(labelText);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 3;
			label.setLayoutData(gd);
		}

		final TableViewer tableViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI);
		tableViewer.setContentProvider(new ArrayContentProvider());
		final List<String> input = PreferencesUtil.deserialize(getValue(key));
		tableViewer.setInput(input);

		Table table = tableViewer.getTable();

		gd = new GridData();
		table.setLayoutData(gd);

		if(buttons != 0)
			gd.horizontalSpan = 2;
		else
			gd.horizontalSpan = 3;
		if(indent > 0)
			gd.horizontalIndent = indent;

		adaptWidthHint(table, widthHint);

		if(buttons != 0) {
			multiControls.put(table, new ArrayList<Control>());
			Composite buttonBar = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginLeft = 0;
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			buttonBar.setLayout(layout);
			gd = new GridData();
			gd.verticalAlignment = SWT.BEGINNING;
			buttonBar.setLayoutData(gd);

			if((buttons & BUTTON_ADD) != 0) {
				Button addButton = new Button(buttonBar, SWT.PUSH);
				addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				addButton.setText(Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addButton.text")); //$NON-NLS-1$
				addButton.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e)
					{
						tableActionListener.add(tableViewer, key, e);
					}
					public void widgetSelected(SelectionEvent e)
					{
						tableActionListener.add(tableViewer, key, e);
					}
				});
				multiControls.get(table).add(addButton);
			}

			if((buttons & BUTTON_EDIT) != 0) {
				Button editButton = new Button(buttonBar, SWT.PUSH);
				editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				editButton.setText(Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.editButton.text")); //$NON-NLS-1$
				editButton.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e)
					{
						tableActionListener.edit(tableViewer, key, e);
					}
					public void widgetSelected(SelectionEvent e)
					{
						tableActionListener.edit(tableViewer, key, e);
					}
				});
				multiControls.get(table).add(editButton);
			}

			if((buttons & BUTTON_REMOVE) != 0) {
				Button removeButton = new Button(buttonBar, SWT.PUSH);
				removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				removeButton.setText(Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.removeButton.text")); //$NON-NLS-1$
				removeButton.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e)
					{
						tableActionListener.remove(tableViewer, key, e);
					}
					public void widgetSelected(SelectionEvent e)
					{
						tableActionListener.remove(tableViewer, key, e);
					}
				});
				multiControls.get(table).add(removeButton);
			}
		}

		tables.put(tableViewer, key);

		return tableViewer;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.preferences.ui.SimpleOptionsConfigurationBlock#dispose()
	 */
	@Override
	public void dispose()
	{
		tables.clear();
		multiControls.clear();
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.preferences.ui.SimpleOptionsConfigurationBlock#updateControls()
	 */
	@Override
	protected void updateControls()
	{
		for (Map.Entry<TableViewer, Key> entry : tables.entrySet())
			updateTable(entry.getKey(), entry.getValue());
		super.updateControls();
	}

	/**
	 * Update the table viewer.
	 * @param tableViewer The table viewer to update
	 * @param key The preference key
	 */
	protected void updateTable(TableViewer tableViewer, Key key)
	{
		tableViewer.getTable().deselectAll();
		tableViewer.setInput(PreferencesUtil.deserialize(getValue(key)));
		tableViewer.refresh();
	}

	/**
	 * @param widthHint {@inheritDoc} -1 to let the text box fill horizontally.
	 */
	@Override
	protected Text addTextField(Composite parent, String label, Key key, int indent, int widthHint)
	{
		Text text = super.addTextField(parent, label, key, indent, widthHint == -1 ? new PixelConverter(parent).convertWidthInCharsToPixels(30) : widthHint);
		adaptWidthHint(text, widthHint);
		return text;
	}

	/**
	 * Add a directory text field with a label and a browse button that
	 * is connected to the given preference key.
	 * @param parent The parent composite
	 * @param label The label text
	 * @param key The preference key
	 * @param indent The horizontal text field indentation
	 * @param widthHint The text field width hint. 0 not to use a width hint. -1 to let the text box fill horizontally.
	 * @return The text field
	 */
	protected Text addDirectoryField(Composite parent, String label, final Key key, int indent, int widthHint)
	{
		final Text text = addButtonTextField(parent, label, Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addDirectoryFieldButton.text"), key, indent, widthHint); //$NON-NLS-1$
		Button button = (Button)multiControls.get(text).get(0);
		button.addSelectionListener(new SelectionListener() {
			private void browse()
			{
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				dlg.setText(Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.directoryDialog.text")); //$NON-NLS-1$
				String path = dlg.open();
				if(path != null) {
					setValue(key, path);
					updateText(text);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e)
			{
				browse();
			}
			public void widgetSelected(SelectionEvent e)
			{
				browse();
			}
		});
		return text;
	}

	/**
	 * Add a workspace directory text field with a label and a browse button that
	 * is connected to the given preference key.
	 * @param parent The parent composite
	 * @param label The label text
	 * @param key The preference key
	 * @param indent The horizontal text field indentation
	 * @param widthHint The text field width hint. 0 not to use a width hint. -1 to let the text box fill horizontally.
	 * @return The text field
	 */
	protected Text addWorkspaceDirectoryField(Composite parent, String label, final Key key, int indent, int widthHint)
	{
		final Text text = addButtonTextField(parent, label, Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addWorkspaceDirectoryFieldButton.text"), key, indent, widthHint); //$NON-NLS-1$
		Button button = (Button)multiControls.get(text).get(0);
		button.addSelectionListener(new SelectionListener() {
			private void browse()
			{
				IContainer[] cc = WorkspaceResourceDialog.openFolderSelection(getShell(), Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addWorkspaceDirectoryDialog.title"), Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addWorkspaceDirectoryDialog.message"), false, null, null); //$NON-NLS-1$ //$NON-NLS-2$
				if(cc != null && cc.length > 0) {
					String path = cc[0].getFullPath().toOSString();
					setValue(key, path);
					updateText(text);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e)
			{
				browse();
			}
			public void widgetSelected(SelectionEvent e)
			{
				browse();
			}
		});
		return text;
	}

	/**
	 * Add a project directory text field with a label and a browse button that
	 * is connected to the given preference key.
	 * @param parent The parent composite
	 * @param label The label text
	 * @param key The preference key
	 * @param indent The horizontal text field indentation
	 * @param widthHint The text field width hint. 0 not to use a width hint. -1 to let the text box fill horizontally.
	 * @return The text field
	 */
	protected Text addProjectDirectoryField(final Composite parent, final String label, final Key key, final int indent, final int widthHint, final IProject project)
	{
		final Text text = addButtonTextField(parent, label, Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addProjectDirectoryFieldButton.text"), key, indent, widthHint); //$NON-NLS-1$
		Button button = (Button)multiControls.get(text).get(0);
		button.addSelectionListener(new SelectionListener() {
			private void browse()
			{
				ViewerFilter vf = new ViewerFilter() {
					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element)
					{
						if(element instanceof IProject) {
							IProject p = (IProject)element;
							if(p.equals(project))
								return true;
							else
								return false;
						}
						return true;
					}
				};
				IContainer[] cc = WorkspaceResourceDialog.openFolderSelection(
						getShell(), 
						Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addProjectDirectoryDialog.title"),  //$NON-NLS-1$
						Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addProjectDirectoryDialog.message"),  //$NON-NLS-1$
						false, 
						null, 
						Collections.singletonList(vf));
				if(cc != null && cc.length > 0) {
					String path = cc[0].getProjectRelativePath().toOSString();
					setValue(key, path);
					updateText(text);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e)
			{
				browse();
			}
			public void widgetSelected(SelectionEvent e)
			{
				browse();
			}
		});
		return text;
	}
	
	protected Text addButtonTextField(final Composite parent, final String label, final String buttonLabel, final Key key, final int indent, final int widthHint)
	{
		final Text text = addTextField(parent, label, key, indent, widthHint);
		((GridData)text.getLayoutData()).horizontalSpan = 1;
		Button browseButton = new Button(parent, SWT.PUSH);
		browseButton.setText(Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.addTextFieldButton.text")); //$NON-NLS-1$
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		
		List<Control> subControls = new ArrayList<Control>(1);
		subControls.add(browseButton);
		multiControls.put(text, subControls);
		
		return text;
	}
	
	/**
	 * @param widthHint The text field width hint. 0 not to use a 
	 * 		width hint. -1 to let the combo fill horizontally.
	 */
	protected Combo addComboBox(Composite parent, String label, Key key, String[] values, String[] valueLabels, int indent, int widthHint) {
		Combo combo = super.addComboBox(parent, label, key, values, valueLabels, indent);
		adaptWidthHint(combo, widthHint);
		return combo;
	}

	protected void setEnabled(Control c, boolean enabled)
	{
		List<Control> subControls = multiControls.get(c);
		if(subControls != null) {
			for(Control control : subControls) {
				if(control != null && !control.isDisposed())
					control.setEnabled(enabled);
			}
		}
		if(c != null && !c.isDisposed())
			c.setEnabled(enabled);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.preferences.ui.SimpleOptionsConfigurationBlock#findControl(org.nightlabs.eclipse.preferences.ui.SimpleOptionsConfigurationBlock.Key)
	 */
	@Override
	protected Control findControl(Key key)
	{
		Control c = super.findControl(key);
		if(c == null) {
			for(Map.Entry<TableViewer, Key> e : tables.entrySet())
				if(e.getValue().equals(key))
					return e.getKey().getTable();
		}
		return c;
	}
	
	/**
	 * Validate a directory. The directory is valid if 
	 * {@link File#isDirectory()} returns <code>true</code>.
	 * @param value The directory value to validate
	 */
	protected void validateDirectory(String value)
	{
		StatusInfo status = new StatusInfo();
		if(value == null || value.length() == 0 || !new File(value).isDirectory())
			status.setError(Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.errorMessage.mustBeExistingDirectory")); //$NON-NLS-1$
		fContext.statusChanged(status);
	}

	/**
	 * Validate an integer.
	 * @param value The possible integer value
	 */
	protected void validateInteger(String value)
	{
		StatusInfo status = new StatusInfo();
		if(value == null || value.length() == 0 || !value.matches("\\+|-?\\d+")) //$NON-NLS-1$
			status.setError(Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.errorMessage.mustBeNumber")); //$NON-NLS-1$
		fContext.statusChanged(status);
	}

	/**
	 * Validate a positive integer.
	 * @param value The possible integer value
	 */
	protected void validatePositiveInteger(String value)
	{
		StatusInfo status = new StatusInfo();
		if(value == null || value.length() == 0 || !value.matches("\\+?\\d+")) //$NON-NLS-1$
			status.setError(Messages.getString("org.nightlabs.eclipse.preference.ui.OptionsConfigurationBlock.errorMessage.mustBePositiveNumber")); //$NON-NLS-1$
		fContext.statusChanged(status);
	}
	
}
