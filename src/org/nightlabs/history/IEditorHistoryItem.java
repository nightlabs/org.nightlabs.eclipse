package org.nightlabs.history;

import org.eclipse.ui.IEditorInput;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface IEditorHistoryItem
{
	String getEditorId();

	String getPerspectiveId();

	IEditorInput getEditorInput();
}
