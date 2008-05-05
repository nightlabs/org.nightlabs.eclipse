package org.nightlabs.eclipse.ui.fckeditor;

import org.eclipse.ui.IEditorInput;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface IFCKEditorInput extends IEditorInput {
	IFCKEditorContent getEditorContent();
}
