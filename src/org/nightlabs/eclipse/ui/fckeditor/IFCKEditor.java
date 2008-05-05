package org.nightlabs.eclipse.ui.fckeditor;

import org.eclipse.ui.IEditorPart;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface IFCKEditor extends IEditorPart {
	public IFCKEditorInput getEditorInput();
	public String getBaseUrl();
	public String getWidgetBackgroundColor();
	public void markDirty(boolean dirty);
}
