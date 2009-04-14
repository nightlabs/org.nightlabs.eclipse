package org.nightlabs.base.ui.entity.editor.overview;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class OverviewPage extends EntityEditorPageWithProgress {

	public static final String PAGE_ID = OverviewPage.class.getName();

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
		OverviewSection overviewSection = new OverviewSection(this, parent, getEditor());
		getManagedForm().addPart(overviewSection);
//		FormEditor formEditor = getEditor();
//		if (formEditor instanceof EntityEditor) {
//			EntityEditor entityEditor = (EntityEditor) formEditor;
//			for (IFormPage page : entityEditor.getPages()) {
//				DefaultOverviewPageSection section = new DefaultOverviewPageSection(this, parent, 
//					RestorableSectionPart.DEFAULT_SECTION_STYLE, page.getTitle(), entityEditor, page.getId());
//				getManagedForm().addPart(section);
//			}
//		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return "Overview";
	}

}
