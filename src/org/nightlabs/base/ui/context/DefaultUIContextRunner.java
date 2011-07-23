/**
 * 
 */
package org.nightlabs.base.ui.context;

import org.eclipse.swt.widgets.Display;

/**
 * A default implementation of {@link IUIContextRunner} that simply runs the
 * Runnable it receives.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 * 
 */
public class DefaultUIContextRunner implements IUIContextRunner {

	/**
	 * Create a new {@link DefaultUIContextRunner}. 
	 */
	public DefaultUIContextRunner() {
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Runs the Runnable without further actions.
	 * </p>
	 */
	@Override
	public void runInUIContext(Runnable runnable) {
		runnable.run();
	}
	
	@Override
	public Display getDisplay() {
		return Display.getDefault();
	}

}
