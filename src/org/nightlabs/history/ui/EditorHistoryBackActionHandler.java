package org.nightlabs.history.ui;


/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistoryBackActionHandler extends EditorHistoryActionHandler {

	/**
	 * @param action
	 */
	public EditorHistoryBackActionHandler() {
		super(EditorHistory.sharedInstance().getBackAction());
	}

}
