package org.nightlabs.base.ui.timepattern;

import org.nightlabs.timepattern.TimePatternSet;

/**
 * Listener that can be added to a {@link TimePatternSetEdit} and that will be notified whenever the
 * {@link TimePatternSet} that edit currently views/edits was changed either by the user or
 * programmatically.
 */
public interface TimePatternSetModifyListener {
	
	/**
	 * Called to notify the receiver that the {@link TimePatternSet} edited by the source of the
	 * given event changed.
	 * 
	 * @param event The event containing the source of the change (a {@link TimePatternSetEdit}.
	 */
	void timePatternSetModified(TimePatternSetModifyEvent event);
}
