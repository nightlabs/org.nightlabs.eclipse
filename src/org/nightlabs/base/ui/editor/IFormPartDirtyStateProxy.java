/**
 * 
 */
package org.nightlabs.base.ui.editor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IFormPartDirtyStateProxy {
	/**
	 * Add a listener to the dirty state of this proxy.
	 * @param listner The listener to add.
	 */
	void addFormPartDirtyStateProxyListener(IFormPartDirtyStateProxyListener listener);	
	/**
	 * Remove the given listener.
	 * @param listner The listener to remove.
	 */
	void removeFormPartDirtyStateProxyListener(IFormPartDirtyStateProxyListener listener);
}
