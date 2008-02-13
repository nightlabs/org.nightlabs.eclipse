/**
 * 
 */
package org.nightlabs.base.ui.action;


/**
 * This interface can be implemented additionally to {@link IAction} or
 * {@link IContributionItem} in order to support visibility/enabled-calculation.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Daniel Mazurek <!-- daniel [AT] nightlabs [DOT] de -->
 */
public interface IUpdateActionOrContributionItem
{
	/**
	 * returns if the action should be enabled
	 * @return if the action should be enabled
	 */
	boolean calculateEnabled();
	
	/**
	 * returns if the action should be visible
	 * @return if the action should be visible
	 */
	boolean calculateVisible();
}
