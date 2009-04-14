package org.nightlabs.base.ui.entity.editor.overview;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class OverviewPageFactory implements IEntityEditorPageFactory {

	public OverviewPageFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory#createPage(org.eclipse.ui.forms.editor.FormEditor)
	 */
	@Override
	public IFormPage createPage(FormEditor formEditor) {
		return new OverviewPage(formEditor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory#createPageController(org.nightlabs.base.ui.entity.editor.EntityEditor)
	 */
	@Override
	public IEntityEditorPageController createPageController(EntityEditor editor) {
		return new OverviewPageController(editor);
	}

}
