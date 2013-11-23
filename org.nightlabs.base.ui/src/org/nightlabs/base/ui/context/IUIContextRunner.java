/**
 * 
 */
package org.nightlabs.base.ui.context;

import org.eclipse.swt.widgets.Display;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 *
 */
public interface IUIContextRunner {

	Display getDisplay();
	void runInUIContext(Runnable runnable);
}
