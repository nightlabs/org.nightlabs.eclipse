/**
 * 
 */
package org.nightlabs.base.ui.editor;

import org.eclipse.ui.forms.IFormPart;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IFormPartDirtyStateProxyListener {
	/**
	 * This is called when the proxies markDirty() method is called.
	 */
	void markDiry(IFormPart formPart);
	/**
	 * This is called when the proxies markUndirty() method is called.
	 */
	void markUndirty(IFormPart formPart);
}
