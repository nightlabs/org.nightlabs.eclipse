/**
 * 
 */
package org.nightlabs.base.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;


/**
 * This interface can be implemented addtionally to {@link IAction} or
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
