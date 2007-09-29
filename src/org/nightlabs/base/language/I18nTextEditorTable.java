package org.nightlabs.base.language;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.language.I18nTextEditor.EditMode;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.table.TableContentProvider;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.language.LanguageCf;
import org.nightlabs.util.Util;

import sun.rmi.runtime.NewThreadAction;

/**
 * Editor Table Composite for {@link I18nText}s. This will provide (or use) a
 * language chooser and make an i18n text editable table. The editor table will
 * either operate directly on the {@link I18nText} passed in the
 * {@link #setI18nText(I18nText)} or work with an own buffer, so editing will
 * not affect the original {@link I18nText}. This behaviour can be controlled
 * by
 * {@link #setEditMode(org.nightlabs.base.language.yo.I18nTextEditorTable.EditMode)}.
 * 
 * Use {@link I18nText#copyFrom(I18nText)} with {@link #getI18nText()} as
 * parameter to reflect the changes in your {@link I18nText}. You can also call
 * {@link #copyToOriginal()} to let that be done for you.
 * 
 * @author Chairat Kongarayawetchakun - Chairat at nightlabs dot de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class I18nTextEditorTable extends XComposite implements II18nTextEditor
{
	private static final String COLUMN_FLAG_NAME     = "Flag"; //$NON-NLS-1$
	private static final String COLUMN_LANGUAGE_NAME = "Language"; //$NON-NLS-1$
	private static final String COLUMN_VALUE_NAME    = "Value"; //$NON-NLS-1$

	private static final int COLUMN_FLAG_INDEX = 0;
	private static final int COLUMN_LANGUAGE_INDEX = 1;
	private static final int COLUMN_VALUE_INDEX = 2;

	private static final String[] COLUMN_NAMES = { COLUMN_FLAG_NAME, COLUMN_LANGUAGE_NAME, COLUMN_VALUE_NAME };

	private I18nText original;
	private I18nText work;
	private I18nTextBuffer buffer = null;

	private Table table;
	private TableViewer tableViewer;
	
	public I18nTextEditorTable(Composite parent) {
		this(parent, null);
	}

	public I18nTextEditorTable(Composite parent, String title) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 1;
		this.setLayout(gridLayout);

		GridData gd = new GridData(GridData.FILL_BOTH);
		
		if (title != null)
			new Label(this, SWT.NONE).setText(title);

		// Initial TableViewer
		int tableStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
		| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		table = new Table(this, tableStyle);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gd);

		tableViewer = new TableViewer(table);

		// set Columns
		tableViewer.setColumnProperties(COLUMN_NAMES);

		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.base.language.I18nTextEditorTable.columnFlag.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.base.language.I18nTextEditorTable.columnLanguage.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.base.language.I18nTextEditorTable.columnText.text")); //$NON-NLS-1$

		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnPixelData(24, false));
		tableLayout.addColumnData(new ColumnWeightData(20));
		tableLayout.addColumnData(new ColumnWeightData(80));
		table.setLayout(tableLayout);

		// Assign the cell editors to the viewer
		tableViewer.setCellEditors(new CellEditor[] { null, null, new TextCellEditor(table) });
		tableViewer.setCellModifier(new I18nTextEditorTableCellModifier());

		// Set the cell modifier for the viewer
		tableViewer.setContentProvider(new I18nTextEditorTableContentProvider()); // Marco: removed the parameters - a ContentProvider never gets its content immutable this way - it gets it via the callback-method inputChanged(...) 
		tableViewer.setLabelProvider(new I18nTextLabelProvider());
	}

	/** ************************************************************************** */
	/**
	 * List of modifyListeners. They will only be triggered if the text is
	 * actually modified by the user, not when the edit changes its text.
	 */
	private ListenerList modifyListeners = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void addModifyListener(ModifyListener l) {
		if (modifyListeners == null)
			modifyListeners = new ListenerList();

		modifyListeners.add(l);
	}

	public void copyToOriginal() {
		if (original != null && work != original)
			work.copyTo(original);
	}

	private EditMode editMode;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#getEditMode()
	 */
	public EditMode getEditMode() {
		return editMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#getI18nText()
	 */
	public I18nText getI18nText() {
		storeText();
		return work;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#getOriginal()
	 */
	public I18nText getOriginal() {
		storeText(); // if we work in buffered mode, this method doesn't make
		// much sense, but we can call it without harm.
		return original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#refresh()
	 */
	public void refresh() {
		loadText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#removeModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void removeModifyListener(ModifyListener l) {
		if (modifyListeners == null)
			return;

		modifyListeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#reset()
	 */
	public void reset() {
		setI18nText(null);
	}

	/**
	 * If the {@link EditMode} is {@link EditMode#DIRECT}, this editor will
	 * work directly with the {@link I18nText} that has been passed to
	 * {@link #setI18nText(I18nText)}. That means, every modification is
	 * directly forwarded to the original {@link I18nText} object (on
	 * focus lost or when calling {@link #getI18nText()}). This behaviour is
	 * useful when you create a new object (e.g. in a wizard page). In this case,
	 * it's not necessary to call {@link #copyToOriginal()}.
	 * <p>
	 * If you edit an existing object, it is (in most cases) not desired to modify it
	 * directly, but instead to transfer all data only from the UI to the object, if
	 * the user explicitely applies his changes (in order to transfer them to the server
	 * as well). Therefore, you can use the {@link EditMode#BUFFERED}, which will
	 * cause an internal {@link I18nTextBuffer} to be created. 
	 * </p>
	 *
	 * @param editMode The new {@link EditMode}.
	 */
	protected void setEditMode(EditMode editMode) {
		this.editMode = editMode;

		switch (editMode) {
		case DIRECT:
			work = original;
			break;
		case BUFFERED:
			if (buffer == null)
				buffer = new I18nTextBuffer();

			buffer.clear();
			work = buffer;
			break;
		default:
			throw new IllegalArgumentException("Unknown editMode: " + editMode); //$NON-NLS-1$
		}

		if (work != original && work != null && original != null)
			work.copyFrom(original);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#setEditable(boolean)
	 */
	public void setEditable(boolean editable) {
//		text.setEditable(editable);
	}

	protected void _setI18nText(I18nText i18nText, EditMode editMode)
	{
		original = i18nText;

		if (editMode != null)
			setEditMode(editMode);
		else {
			if (original instanceof I18nTextBuffer)
				setEditMode(EditMode.DIRECT);
			else
				setEditMode(EditMode.BUFFERED);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#setI18nText(org.nightlabs.i18n.I18nText)
	 */
	public void setI18nText(I18nText i18nText) {
		setI18nText(i18nText, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nightlabs.base.language.II18nTextEditor#setI18nText(org.nightlabs.i18n.I18nText,
	 *      org.nightlabs.base.language.I18nTextEditor.EditMode)
	 */
	public void setI18nText(I18nText i18nText, EditMode editMode) {
		storeText();
		_setI18nText(i18nText, editMode);
		loadText();
	}

	/**
	 * This method stores the currently edited text into the "backend" {@link I18nText}
	 * object. This method is automatically called whenever the <tt>I18nTextEditor</tt>
	 * is changed by the user. This means, the I18nText is kept synchronous with the
	 * displayed and edited text. 
	 * <p>
	 * This method has been changed from public to private.
	 */
	private void storeText()
	{
	}

	/**
	 * Loads the text out of the buffer and displays it in the text field.
	 * ModifyListeners registered will not be triggered when this happens.
	 */
	private void loadText()
	{
		tableViewer.setInput(work);

//		loadingText = true;
//		try {
//		String txt = null;
//
//		if (work != null) {
//		textLanguage = languageChooser.getLanguage();
//		txt = work.getText(textLanguage.getLanguageID());
//		}
//		if (txt == null) txt = "";
//		text.setText(txt);
//		orgText = txt;
//		} finally {
//		loadingText = false;
//		}
	}

	private class I18nTextEditorTableCellModifier
	implements ICellModifier
	{
		public boolean canModify(Object element, String property)
		{
			return COLUMN_VALUE_NAME.equals(property);
		}

		@SuppressWarnings("unchecked") //$NON-NLS-1$
		public Object getValue(Object element, String property)
		{
			return ((Map.Entry<String, String>)element).getValue();
		}

		@SuppressWarnings("unchecked") //$NON-NLS-1$
		public void modify(Object element, String property, Object value)
		{
			if (COLUMN_VALUE_NAME.equals(property)) {
				TableItem tableItem = (TableItem) element;
				Map.Entry<String, String> mapEntry = (Entry<String, String>) tableItem.getData();
				String languageID = mapEntry.getKey();
				String oldText = mapEntry.getValue();
				String newText = (String) value;
				if (Util.equals(oldText, newText))
					return;

				work.setText(languageID, newText);
				tableItem.setText(COLUMN_VALUE_INDEX, newText);
				mapEntry.setValue(newText);
				tableViewer.update(element, new String[] { property });

				if (modifyListeners != null) {
					Object[] listeners = modifyListeners.getListeners();
					Event e = new Event();
					e.widget = I18nTextEditorTable.this;
					e.item = e.widget;
					e.display = I18nTextEditorTable.this.getDisplay();
					e.text = newText;
					ModifyEvent event = new ModifyEvent(e);
					for (Object l : listeners)
						((ModifyListener)l).modifyText(event);
				}

				fireModificationFinished();
			}
		}
	}

	/////////////////////////////////////////////////
	private class I18nTextLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {

		@SuppressWarnings("unchecked") //$NON-NLS-1$
		public String getColumnText(Object element, int columnIndex) {
			Map.Entry<String, String> item = (Map.Entry<String, String>)element;
			switch (columnIndex) {
				case COLUMN_FLAG_INDEX:
					return null; //$NON-NLS-1$
				case COLUMN_LANGUAGE_INDEX:
					return LanguageManager.sharedInstance().getLanguage(item.getKey(), true).getName().getText();
				case COLUMN_VALUE_INDEX:
					return item.getValue();
				default :
					return ""; 	 //$NON-NLS-1$
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		@SuppressWarnings("unchecked") //$NON-NLS-1$
		public Image getColumnImage(Object element, int columnIndex) {
			Map.Entry<String, String> item = (Map.Entry<String, String>)element;
			switch (columnIndex) {
				case COLUMN_FLAG_INDEX:
					return LanguageManager.sharedInstance().getFlag16x16Image(item.getKey());
				case COLUMN_LANGUAGE_INDEX:
				case COLUMN_VALUE_INDEX:
				default :
					return null; 	
			}
		}
	}

	private static class I18nTextEditorTableContentProvider extends TableContentProvider
	{
		private I18nText i18nText = null;

		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput instanceof I18nText)
				this.i18nText = (I18nText) newInput;
			else
				this.i18nText = null;
		}

		// Return the i18nText as an array of Objects
		@SuppressWarnings("unchecked") //$NON-NLS-1$
		@Override
		public Object[] getElements(Object parent) {
			if (i18nText == null)
				return new Object[0];

			Collection<LanguageCf> languageCfs = LanguageManager.sharedInstance().getLanguages();
			Map<String, String> es = new HashMap<String, String>(languageCfs.size());
			for (Map.Entry<String, String> me : i18nText.getTexts()) {
				String languageID = me.getKey();
				if(LanguageManager.sharedInstance().getLanguage(languageID, false) != null){ 
					String text = me.getValue();
					es.put(languageID, text);
				}//if
			}

			for (LanguageCf languageCf : languageCfs) {
				String languageID = languageCf.getLanguageID();
				if (!es.containsKey(languageID))
					es.put(languageID, ""); //$NON-NLS-1$
			}

			return es.entrySet().toArray();
		}
	}
	
	/**
	 * the {@link ListenerList} which holds added {@link ModificationFinishedListener}s
	 */
	private ListenerList modificationFinishedListeners = new ListenerList();
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#addModificationFinishedListener(org.nightlabs.base.language.ModificationFinishedListener)
	 */
	public void addModificationFinishedListener(ModificationFinishedListener listener) {
		modificationFinishedListeners.add(listener);		
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.language.II18nTextEditor#removeModificationFinishedListener(org.nightlabs.base.language.ModificationFinishedListener)
	 */
	public void removeModificationFinishedListener(ModificationFinishedListener listener) {
		modificationFinishedListeners.remove(listener);
	}
	
	private void fireModificationFinished() 
	{
		ModificationFinishedEvent event = new ModificationFinishedEvent(this);
		for (int i=0; i<modificationFinishedListeners.size(); i++) {
			ModificationFinishedListener listener = (ModificationFinishedListener) modificationFinishedListeners.getListeners()[i];
			listener.modificationFinished(event);
		}
	}	
}
