/**
 * 
 */
package org.nightlabs.base.ui.form;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;

/**
 * This implementation of {@link IFormPage} can be used to create a form page
 * based on a {@link Composite}. Sometimes it might be necessary to use this
 * instead of {@link FormPage} because inside a Form there are some layout issues
 * causing Composites (especially Tables) to grow too wide and thus the
 * occurrence of unnecessary scrollbars.  
 * <p>
 * This page will create an {@link XComposite} wrapper for the content. 
 * A {@link NightlabsFormsToolkit} will be set for the wrapper, so child
 * composites will appear like in a Form.<br/> 
 * Implement {@link #createComposite(Composite)} to create the content of this page.
 * </p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class CompositeFormPage extends EditorPart implements IFormPage {

	private IManagedForm managedForm;
	private XComposite wrapper;
	private Composite composite;
	private FormEditor formEditor;
	private String id;
	private int index;
	
	/**
	 * A constructor that creates the page and initializes it with the editor.
	 * 
	 * @param editor
	 *            the parent editor
	 * @param id
	 *            the unique identifier
	 * @param title
	 *            the page title
	 */
	public CompositeFormPage(FormEditor editor, String id, String title) {
		this(id, title);
		initialize(editor);
	}
	/**
	 * The constructor. The parent editor need to be passed in the
	 * <code>initialize</code> method if this constructor is used.
	 * 
	 * @param id
	 *            a unique page identifier
	 * @param title
	 *            a user-friendly page title
	 */
	public CompositeFormPage(String id, String title) {
		this.id = id;
		setPartName(title);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (managedForm != null)
			managedForm.commit(true);
	}

	/**
	 * Noop, as {@link #isSaveAsAllowed()} returns <code>false</code>.
	 */
	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	/**
	 * Delegates to the {@link ManagedForm} of this page.
	 */
	@Override
	public boolean isDirty() {
		return managedForm != null ? managedForm.isDirty() : false;
	}

	/**
	 * This implementation returns <code>false</code>.
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TOTAL_WRAPPER, LayoutDataMode.NONE);
		NightlabsFormsToolkit toolkit = null;
		if (formEditor.getToolkit() instanceof NightlabsFormsToolkit)
			toolkit = (NightlabsFormsToolkit) formEditor.getToolkit();
		else
			toolkit = new NightlabsFormsToolkit(wrapper.getDisplay());
		wrapper.setToolkit(toolkit);
		composite = createComposite(wrapper);
		if (composite != null && formEditor != null) {
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			wrapper.adaptToToolkit();
			managedForm = new CompositeManagedForm(composite, toolkit);
		}
	}
	
	/**
	 * Implement this method to create the content of this page.
	 * 
	 * @param parent The parent for the new content.
	 * @return The newly created content.
	 */
	protected abstract Composite createComposite(Composite parent);

	/**
	 * Returns the {@link Composite} created for this page.
	 * 
	 * @return The {@link Composite} created for this page.
	 */
	public Composite getComposite() {
		return composite;
	}
	
	/**
	 * Delegates to the {@link Composite} of this page.
	 */
	@Override
	public void setFocus() {
		if (composite != null && !composite.isDisposed()) {
			composite.setFocus();
		}
	}

	/**
	 * This implementation returns <code>true</code>.
	 */
	@Override
	public boolean canLeaveThePage() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#getEditor()
	 */
	@Override
	public FormEditor getEditor() {
		return formEditor;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public IManagedForm getManagedForm() {
		return managedForm;
	}

	@Override
	public Control getPartControl() {
		return wrapper;
	}

	@Override
	public void initialize(FormEditor editor) {
		this.formEditor = editor;
	}

	@Override
	public boolean isActive() {
		return this.equals(formEditor.getActivePageInstance());
	}

	@Override
	public boolean isEditor() {
		return false;
	}

	/**
	 * Attempts to select and reveal the given object by passing the request to
	 * the managed form.
	 * 
	 * @param object
	 *            the object to select and reveal in the page if possible.
	 * @return <code>true</code> if the page has been successfully selected
	 *         and revealed by one of the managed form parts, <code>false</code>
	 *         otherwise.
	 */
	@Override
	public boolean selectReveal(Object object) {
		if (managedForm != null)
			return managedForm.setInput(object);
		return false;
	}
	
	/**
	 * Refreshes the {@link ManagedForm}.
	 * 
	 * @see org.eclipse.ui.forms.editor.IFormPage#setActive(boolean)
	 */
	@Override
	public void setActive(boolean active) {
		if (active) {
			managedForm.refresh();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.IFormPage#setIndex(int)
	 */
	@Override
	public void setIndex(int index) {
		this.index = index;
	}

}
