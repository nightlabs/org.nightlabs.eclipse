package org.nightlabs.history.ui;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class StartupListener implements IStartup {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		EditorHistory.sharedInstance().setWorkbench(workbench);
	}

}
