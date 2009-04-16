package org.nightlabs.base.ui.entity.editor.overview;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class OverviewPage extends EntityEditorPageWithProgress {

	public static final String PAGE_ID = OverviewPage.class.getName();
	
	private OverviewSection overviewSection;
	
	/**
	 * @param editor
	 */
	public OverviewPage(FormEditor editor) {
		super(editor, PAGE_ID, "Overview");
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
		overviewSection = new OverviewSection(this, parent, getEditor());
		getManagedForm().addPart(overviewSection);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return "Overview";
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		managedForm.getToolkit().decorateFormHeading(managedForm.getForm().getForm());
	}
	
	protected boolean includeFixForVerticalScrolling() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#handleControllerObjectModified(org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent)
	 */
	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) 
	{
		if (!getPartControl().isDisposed()) {
			getPartControl().getDisplay().asyncExec(new Runnable(){
				@Override
				public void run() {					
					overviewSection.refresh();	
				}
			});			
		}
	}
	
	@Override
	public void setActive(boolean active) {
		if (active) {
			// mark stale when getting active, to make the overview section refresh, 
			// so that (potential) changes are reflected 
			overviewSection.markStale();
		}
		super.setActive(active);
	}
}
