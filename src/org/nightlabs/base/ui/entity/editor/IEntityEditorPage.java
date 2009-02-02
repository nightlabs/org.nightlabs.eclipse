/**
 *
 */
package org.nightlabs.base.ui.entity.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public interface IEntityEditorPage
extends IFormPage
{
	/**
	 *
	 * @return the {@link IEntityEditorPageController} which is responsible for
	 * loading and saving the data for the editor page.
	 */
	public IEntityEditorPageController getPageController();

	/**
	 *
	 * @return the progress monitor for displaying the progress of loading and saving.
	 */
	public IProgressMonitor getProgressMonitor();
}
