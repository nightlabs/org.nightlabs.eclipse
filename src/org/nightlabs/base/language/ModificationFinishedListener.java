/**
 * 
 */
package org.nightlabs.base.language;

/**
 * Listener object for listen for the finish of the modification of an {@link II18nTextEditor}
 * 
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public interface ModificationFinishedListener 
{
	/**
	 * is called when the modification of {@link II18nTextEditor} is finished
	 * @param event the {@link ModificationFinishedEvent} which knows the {@link II18nTextEditor} which modification has been finished
	 */
	public void modificationFinished(ModificationFinishedEvent event);
}
