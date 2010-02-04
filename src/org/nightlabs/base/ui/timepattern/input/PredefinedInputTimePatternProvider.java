package org.nightlabs.base.ui.timepattern.input;

import org.nightlabs.timepattern.InputTimePattern;

/**
 * Interface used to provide {@link InputTimePattern}s in the {@link InputTimePatternDialog}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface PredefinedInputTimePatternProvider {

	/**
	 * @return The name of this {@link PredefinedInputTimePatternProvider} as it should be displayed
	 *         to the user.
	 */
	String getName();

	/**
	 * @return An instance of the version of {@link InputTimePattern} this provider creates.
	 */
	InputTimePattern createInputTimePattern();

}
