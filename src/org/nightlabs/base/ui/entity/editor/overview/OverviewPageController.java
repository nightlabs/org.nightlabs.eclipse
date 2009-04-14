package org.nightlabs.base.ui.entity.editor.overview;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class OverviewPageController extends EntityEditorPageController {

	/**
	 * @param editor
	 */
	public OverviewPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public OverviewPageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.IEntityEditorPageController#doLoad(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	public void doLoad(ProgressMonitor monitor) {
		setLoaded(true); // must be done before fireModifyEvent!
		fireModifyEvent(null, null);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.IEntityEditorPageController#doSave(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	public boolean doSave(ProgressMonitor monitor) {
		return false;
	}

}
