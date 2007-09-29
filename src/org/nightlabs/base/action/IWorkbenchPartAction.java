/**
 * 
 */
package org.nightlabs.base.action;

import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Daniel Mazurek <!-- daniel [AT] nightlabs [DOT] de -->
 */
public interface IWorkbenchPartAction 
extends IUpdateActionOrContributionItem 
{
	/**
	 * sets the active workbench part
	 * @param part the IWorkbenchPart to set
	 */
	void setActivePart(IWorkbenchPart part);
}
