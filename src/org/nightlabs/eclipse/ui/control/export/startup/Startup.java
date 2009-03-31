package org.nightlabs.eclipse.ui.control.export.startup;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.eclipse.ui.control.export.FocusHistory;
import org.nightlabs.eclipse.ui.control.export.resource.Messages;

public class Startup
implements IStartup
{
	public static final String PLUGIN_ID = "org.nightlabs.jfire.issuetracking.ui"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(Startup.class);
	private Control selectedControl;

	private IWorkbench workbench;
	private Listener focusInListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			Widget widget = e.widget;
			if (logger.isDebugEnabled())
				logger.debug("focusInListener.run: widgetClass="+ (widget == null ? null : widget.getClass().getName()) +" widgetIdentity=" + Integer.toHexString(System.identityHashCode(widget)) + " widget=" + widget); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			FocusHistory.sharedInstance().addFocusedWidget(widget);
		}
	};

	@Override
	public void earlyStartup() {
		workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.asyncExec(new Runnable(){
			@Override
			public void run() {
				display.addFilter(SWT.FocusIn, focusInListener);
			}
		});
	}

	public Control getSelectedControl() {
		return selectedControl;
	}
}
