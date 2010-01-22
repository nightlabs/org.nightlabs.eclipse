package org.nightlabs.base.ui.timepattern;

import java.util.Collection;

import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.timepattern.TimePatternSet;

/**
 * Interface for the editing of a {@link TimePatternSet}. Implementations will be able to create
 * widgets that can present a {@link TimePatternSet} to the user and will be able to perform the
 * actions defined in this interface.
 */
public interface TimePatternSetEdit {
	/**
	 * Set the {@link TimePatternSet}s that should be viewed/edited. Note that this instance will be
	 * changed directly.
	 * 
	 * @param timePatternSet The {@link TimePatternSet} to edit.
	 */
	void setTimePatternSet(TimePatternSet timePatternSet);

	/**
	 * Get the {@link TimePatternSet} edited by this Object.
	 * 
	 * @return The {@link TimePatternSet} edited by this Object.
	 */
	TimePatternSet getTimePatternSet();

	/**
	 * Create a new {@link TimePattern} in the {@link TimePatternSet} currently edited/viewed.
	 */
	void createTimePattern();

	/**
	 * Remove all {@link TimePattern}s that the user has selected from the currently edited/viewed
	 * {@link TimePatternSet}.
	 */
	void removeSelectedTimePatterns();

	/**
	 * Remove the given {@link TimePattern}s from the currently edited/viewed {@link TimePatternSet}.
	 * 
	 * @param timePatterns The {@link TimePattern}s to remove.
	 */
	void removeTimePatterns(Collection<TimePattern> timePatterns);

	/**
	 * Remove the given {@link TimePattern} from the currently edited/viewed {@link TimePatternSet}.
	 * 
	 * @param timePattern The {@link TimePattern} to remove.
	 */
	void removeTimePattern(TimePattern timePattern);

	/**
	 * Add a {@link TimePatternSetModifyListener} to this Object that will be notified every time the
	 * currently edited {@link TimePatternSet} was changed by this object.
	 * 
	 * @param listener The listener to add.
	 */
	void addTimePatternSetModifyListener(TimePatternSetModifyListener listener);
	
	/**
	 * Remove the given listener from the list of {@link TimePatternSetModifyListener}s of this object.
	 * 
	 * @param listener The listener to remove.
	 */
	void removeTimePatternSetModifyListener(TimePatternSetModifyListener listener);
}
