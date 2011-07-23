/**
 *
 */
package org.nightlabs.jseditor.ui.rap;

import java.util.IdentityHashMap;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.jseditor.ui.DocumentEvent;
import org.nightlabs.jseditor.ui.IDocumentListener;
import org.nightlabs.jseditor.ui.IJSEditor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 *
 */
public class JSEditorRAP extends Composite implements IJSEditor {

	private Text text;

	/**
	 *
	 */
	public JSEditorRAP(Composite parent) {
		super(parent, SWT.NONE);
		text = new Text(this, SWT.BORDER | SWT.MULTI);
		setLayout(new GridLayout());
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private IdentityHashMap<IDocumentListener, ModifyListener> jsEditorDocumentListener2modifyListener = new IdentityHashMap<IDocumentListener, ModifyListener>();

	@Override
	public void addDocumentListener(final IDocumentListener documentListener)
	{
		if (documentListener == null)
			throw new IllegalArgumentException("documentListener == null");

		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				documentListener.documentChanged(new DocumentEvent(JSEditorRAP.this));
			}
		};

		jsEditorDocumentListener2modifyListener.put(documentListener, modifyListener);
		text.addModifyListener(modifyListener);
	}

	@Override
	public void removeDocumentListener(IDocumentListener documentListener)
	{
		ModifyListener modifyListener = jsEditorDocumentListener2modifyListener.get(documentListener);
		if (modifyListener == null)
			return;

		text.removeModifyListener(modifyListener);
		jsEditorDocumentListener2modifyListener.remove(documentListener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditor#addFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	@Override
	public void addFocusListener(FocusListener listener) {
		text.addFocusListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditor#addKeyListener(org.eclipse.swt.events.KeyListener)
	 */
	@Override
	public void addKeyListener(KeyListener listener) {
		text.addKeyListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditor#addMouseListener(org.eclipse.swt.events.MouseListener)
	 */
	@Override
	public void addMouseListener(MouseListener listener) {
		text.addMouseListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditor#getDocumentText()
	 */
	@Override
	public String getDocumentText() {
		return text.getText();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditor#removeFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	@Override
	public void removeFocusListener(FocusListener listener) {
		text.removeFocusListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditor#removeKeyListener(org.eclipse.swt.events.KeyListener)
	 */
	@Override
	public void removeKeyListener(KeyListener listener) {
		text.removeKeyListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditor#removeMouseListener(org.eclipse.swt.events.MouseListener)
	 */
	@Override
	public void removeMouseListener(MouseListener listener) {
		text.removeMouseListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditor#setDocumentText(java.lang.String)
	 */
	@Override
	public void setDocumentText(String text) {
		this.text.setText(text);
	}

	public Menu createContextMenu(MenuManager menuManager) {
		Menu menu = menuManager.createContextMenu(text);
		text.setMenu(menu);
		return menu;
	}
}
