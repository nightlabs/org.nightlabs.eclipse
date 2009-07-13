package org.nightlabs.history.ui;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Interface for describing a history entry for an editor in the {@link EditorHistory}.
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 * TODO: add IMemento to store editor information + use IEditorDescriptor instead of editor id.
 *
 */
public interface IEditorHistoryItem
{
	/**
	 * Returns the id of the {@link IEditorPart}.
	 * @return the id of the {@link IEditorPart}.
	 */
	String getEditorId();

	/**
	 * Returns the id of the perspective for the corresponding editor.
	 * @return the id of the perspective for the corresponding editor
	 */
	String getPerspectiveId();

	/**
	 * Returns the {@link IEditorInput} for the editor.
	 * @return the {@link IEditorInput} for the editor.
	 */
	IEditorInput getEditorInput();
}
