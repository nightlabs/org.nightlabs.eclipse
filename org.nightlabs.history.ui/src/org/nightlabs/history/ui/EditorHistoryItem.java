package org.nightlabs.history.ui;

import org.eclipse.ui.IEditorInput;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistoryItem implements IEditorHistoryItem {

	private String editorID;
	private IEditorInput editorInput;
	private String perspectiveID;

	/**
	 * @param editorID
	 * @param editorInput
	 * @param perspectiveID
	 */
	public EditorHistoryItem(String editorID, IEditorInput editorInput,
			String perspectiveID) {
		super();
		this.editorID = editorID;
		this.editorInput = editorInput;
		this.perspectiveID = perspectiveID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.ui.IEditorHistoryItem#getEditorId()
	 */
	@Override
	public String getEditorId() {
		return editorID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.ui.IEditorHistoryItem#getEditorInput()
	 */
	@Override
	public IEditorInput getEditorInput() {
		return editorInput;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.ui.IEditorHistoryItem#getPerspectiveId()
	 */
	@Override
	public String getPerspectiveId() {
		return perspectiveID;
	}

}
