package org.nightlabs.base.ui.form;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;

/**
 * Abstract base class for all form pages. This class does NOT change the behaviour not the handling
 * of the FormPage in any way. It is created to have the possibility of centralised changes for all
 * FormPages. <br/>
 * It currently implements a fix for 
 * <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=215997#c4">Table resizing problems in
 * FormPages</a>. 
 * 
 * <p>This page will only implement a fix for horizontally growing tables, but may be configured via
 * {@link #includeFixForVerticalScrolling()} to also implement a fix for vertically growing ones.
 * This will unfortunalely result in no vertical scroll bars at all!</p> 
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public abstract class AbstractBaseFormPage
	extends FormPage
{
	private PageForm managedForm;
	
	private static class PageForm
		extends ManagedForm
	{
		public PageForm(FormPage page, ScrolledForm form) {
			super(page.getEditor().getToolkit(), form);
			setContainer(page);
		}
		
		public FormPage getPage() {
			return (FormPage)getContainer();
		}
		@Override
		public void dirtyStateChanged() {
			getPage().getEditor().editorDirtyStateChanged();
		}
		@Override
		public void staleStateChanged() {
			if (getPage().isActive())
				refresh();
		}
	}
	
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
	public AbstractBaseFormPage(FormEditor editor, String id, String title)
	{
		super(editor, id, title);
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
	public AbstractBaseFormPage(String id, String title)
	{
		super(id, title);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		FormToolkit toolkit = getEditor().getToolkit(); 
		ScrolledForm form = new ScrolledForm(parent, SWT.V_SCROLL	| SWT.H_SCROLL | toolkit.getOrientation())
		{
			private Composite wrappedBody;
			
			@Override
			public Composite getBody()
			{
				if (wrappedBody == null)
				{
					Composite realBody = super.getBody();
					realBody.setLayout(XComposite.getLayout(LayoutMode.TOTAL_WRAPPER));
					wrappedBody = new Composite(realBody, SWT.NONE);
					GridData gd = new GridData(GridData.FILL_BOTH);
					// WORKAROUND: this is a workaround for growing tables in FromPages.
					// more information about this can be found at: https://bugs.eclipse.org/bugs/show_bug.cgi?id=215997#c4
					gd.widthHint = 1;
					if (AbstractBaseFormPage.this.includeFixForVerticalScrolling())
					{
						gd.heightHint = 1;						
					}
					wrappedBody.setLayoutData(gd);
				}
				
				return wrappedBody;
			}
		};
		
		// copy from FormToolkit.createForm;
		form.setExpandHorizontal(true);
		form.setExpandVertical(true);
		FormColors colors = toolkit.getColors();
		form.setBackground(colors.getBackground());
		form.setForeground(colors.getColor(IFormColors.TITLE));
		form.setFont(JFaceResources.getHeaderFont());	
		// end of copy from FormToolkit.createForm;
		
		managedForm = new PageForm(this, form);
		BusyIndicator.showWhile(parent.getDisplay(), new Runnable() {
			public void run() {
				createFormContent(managedForm);
			}
		});
	}

	/**
	 * Indicates whether the fix for vertically growing pages should be applied. As a side-effect,
	 * this fix will prohibit vertical scroll bars and, hence should only be used if really necessary.
	 * @return <code>true</code> if vertically growing tables shall be prevented (and no vertical
	 * 	scrollbars shall be shown), <code>false</code> otherwise. 
	 */
	protected boolean includeFixForVerticalScrolling()
	{
		return false;
	}

	@Override
	protected abstract void createFormContent(IManagedForm managedForm);
	
	@Override
	public IManagedForm getManagedForm()
	{
		return managedForm;
	}
	
	@Override
	public void setActive(boolean active)
	{
		if (active) {
			// We are switching to this page - refresh it
			// if needed.
			managedForm.refresh();
		}
	}
	
	@Override
	public Control getPartControl()
	{
		return managedForm != null ? managedForm.getForm() : null;
	}
	
	@Override
	public void dispose()
	{
		if (managedForm != null)
		{
			managedForm.dispose();			
		}
	}
	
	@Override
	public void setFocus()
	{
		if (managedForm != null)
		{
			managedForm.setFocus();
		}
	}
	
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		if (managedForm != null)
		{
			managedForm.commit(true);
		}
	}
	
	@Override
	public boolean isDirty()
	{
		return managedForm != null ? managedForm.isDirty() : false;
	}
	
	@Override
	public boolean selectReveal(Object object)
	{
		if (managedForm != null)
		{
			return managedForm.setInput(object);			
		}
		
		return false;
	}
	
}
