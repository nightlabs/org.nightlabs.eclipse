package org.nightlabs.history.ui;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistoryChangedEvent {

	private EditorHistory history;

	public EditorHistoryChangedEvent(EditorHistory history) {
		this.history = history;
	}

	public EditorHistory getHistory() {
		return history;
	}
}
