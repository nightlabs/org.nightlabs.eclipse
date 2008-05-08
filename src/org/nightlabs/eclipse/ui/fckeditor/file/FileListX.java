package org.nightlabs.eclipse.ui.fckeditor.file;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FileListX extends Composite
{
	ImageProvider imageProvider = new ImageProvider(getShell().getDisplay());

	private static final String[] VALUE_SET = new String[] { "xxx", "yyy",
	"zzz" };

	private static final String NAME_PROPERTY = "name";

	private static final String VALUE_PROPERTY = "value";

	private TableViewer viewer;
	private IFCKEditorContentFile[] files;

	public FileListX(Composite parent, int style, List<IFCKEditorContentFile> files)
	{
		super(parent, style);
		this.files = files.toArray(new IFCKEditorContentFile[files.size()]);
		buildControls();
	}

	protected void buildControls() {
		FillLayout compositeLayout = new FillLayout();
		setLayout(compositeLayout);

		final Table table = new Table(this, SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		TableColumn column2 = new TableColumn(table, SWT.NONE);

		final TableEditor editor = new TableEditor(table);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 128;
		editor.minimumHeight = 128;
		// editing the second column
		final int EDITABLECOLUMN = 1;

		for (IFCKEditorContentFile file : files) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {"xxx", file.getName()});

			// The control that will be the editor must be a child of the Table
			Label newEditor = new Label(table, SWT.NONE);
			newEditor.setImage(imageProvider.getImage(file));
//			newEditor.setText(item.getText(EDITABLECOLUMN));
//			newEditor.addModifyListener(new ModifyListener() {
//				public void modifyText(ModifyEvent e) {
//					Text text = (Text)editor.getEditor();
//					editor.getItem().setText(EDITABLECOLUMN, text.getText());
//				}
//			});
//			newEditor.selectAll();
//			newEditor.setFocus();
			editor.setEditor(newEditor, item, EDITABLECOLUMN);
		}

//		for (int i = 0; i < 10; i++) {
//			TableItem item = new TableItem(table, SWT.NONE);
//			item.setText(new String[] {"item " + i, "edit this value"});
//
//			// The control that will be the editor must be a child of the Table
//			Text newEditor = new Text(table, SWT.NONE);
//			newEditor.setText(item.getText(EDITABLECOLUMN));
//			newEditor.addModifyListener(new ModifyListener() {
//				public void modifyText(ModifyEvent e) {
//					Text text = (Text)editor.getEditor();
//					editor.getItem().setText(EDITABLECOLUMN, text.getText());
//				}
//			});
//			newEditor.selectAll();
//			newEditor.setFocus();
//			editor.setEditor(newEditor, item, EDITABLECOLUMN);
//
//		}
		column1.pack();
		column2.pack();


//		table.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				// Clean up any previous editor control
//				Control oldEditor = editor.getEditor();
//				if (oldEditor != null) oldEditor.dispose();
//
//				// Identify the selected row
//				TableItem item = (TableItem)e.item;
//				if (item == null) return;
//
//				// The control that will be the editor must be a child of the Table
//				Text newEditor = new Text(table, SWT.NONE);
//				newEditor.setText(item.getText(EDITABLECOLUMN));
//				newEditor.addModifyListener(new ModifyListener() {
//					public void modifyText(ModifyEvent e) {
//						Text text = (Text)editor.getEditor();
//						editor.getItem().setText(EDITABLECOLUMN, text.getText());
//					}
//				});
//				newEditor.selectAll();
//				newEditor.setFocus();
//				editor.setEditor(newEditor, item, EDITABLECOLUMN);
//			}
//		});

//		final Table table = new Table(this, SWT.FULL_SELECTION);
//		viewer = buildAndLayoutTable(table);
//
//		attachContentProvider(viewer);
//		attachLabelProvider(viewer);
//		attachCellEditors(viewer, table);
//
//		viewer.setInput(files);
	}

	private void attachLabelProvider(TableViewer viewer) {
		viewer.setLabelProvider(new ITableLabelProvider() {
			public Image getColumnImage(Object element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return imageProvider.getImage((IFCKEditorContentFile)element);
				default:
					return null;
				}
			}

			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return "";
				case 1:
					return ""; //((IFCKEditorContentFile)element).getName();
				case 2:
					return ((IFCKEditorContentFile)element).getContentType();
				default:
					return "Invalid column: " + columnIndex;
				}
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener lpl) {
			}
		});
	}

	private void attachContentProvider(TableViewer viewer) {
		viewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});
	}

	private TableViewer buildAndLayoutTable(final Table table) {
		TableViewer tableViewer = new TableViewer(table);

		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1, 64, true));
		layout.addColumnData(new ColumnWeightData(33, 75, true));
		layout.addColumnData(new ColumnWeightData(33, 75, true));
		table.setLayout(layout);

		TableColumn imageColumn = new TableColumn(table, SWT.BEGINNING);
		imageColumn.setText("Image");
		TableColumn nameColumn = new TableColumn(table, SWT.BEGINNING);
		nameColumn.setText("Name");
		TableColumn valColumn = new TableColumn(table, SWT.CENTER);
		valColumn.setText("Value");
		table.setHeaderVisible(true);
		return tableViewer;
	}

	private void attachCellEditors(final TableViewer viewer, Composite parent) {

		viewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return true;
			}

			public Object getValue(Object element, String property) {
				System.out.println("getValue: element: "+element+" property: "+property);
				return new RGB(100, 50, 0);
//				if (NAME_PROPERTY.equals(property))
//					return ((EditableTableItem) element).name;
//				else
//					return ((EditableTableItem) element).value;
			}

			public void modify(Object element, String property, Object value) {
				System.out.println("Modify");
//				TableItem tableItem = (TableItem) element;
//				EditableTableItem data = (EditableTableItem) tableItem
//				.getData();
//				if (NAME_PROPERTY.equals(property))
//					data.name = value.toString();
//				else
//					data.value = (Integer) value;
//
//				viewer.refresh(data);
			}
		});

		viewer.setCellEditors(new CellEditor[] {
				new MyCellEditor(),
				new ColorCellEditor(parent)
		});

		viewer
		.setColumnProperties(new String[] { NAME_PROPERTY,
				VALUE_PROPERTY });
	}

}

class MyCellEditor extends CellEditor
{
	private Label name;
	private Label type;
	private Label size;

	@Override
	protected Control createControl(Composite parent)
	{
		Composite right = new Composite(parent, SWT.NONE);
		right.setBackground(right.getParent().getBackground());
		right.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		right.setLayoutData(gridData);

		name = createEntry(right, "Name:", "");
		type = createEntry(right, "Type:", "");
		size = createEntry(right, "Size:", ""); // String.format("%d bytes", file.getData().length)
		createEntry(right, "Noch was:", "Bla bla bla bla");
		return right;
	}

	private Label createEntry(Composite parent, String label, String value)
	{
		Label labelLabel = new Label(parent, SWT.NONE);
		labelLabel.setBackground(parent.getParent().getBackground());
		labelLabel.setText(label);
		Label valueLabel = new Label(parent, SWT.NONE);
		valueLabel.setBackground(parent.getParent().getBackground());
		valueLabel.setText(value);
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		valueLabel.setLayoutData(gridData);
		return valueLabel;
	}

	@Override
	protected Object doGetValue()
	{
		return null;
	}

	@Override
	protected void doSetFocus()
	{
	}

	@Override
	protected void doSetValue(Object value)
	{
		System.out.println("Set value: "+value);
	}
}

class EditableTableItem {
	public String name;

	public Integer value;

	public EditableTableItem(String n, Integer v) {
		name = n;
		value = v;
	}
}