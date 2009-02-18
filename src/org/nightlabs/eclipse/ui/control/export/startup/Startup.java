package org.nightlabs.eclipse.ui.control.export.startup;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class Startup implements IStartup {

	private static final Logger logger = Logger.getLogger(Startup.class);
	private Control selectedControl;

	private IWorkbench workbench;
	@Override
	public void earlyStartup() {
		workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.asyncExec(new Runnable(){
			@Override
			public void run() {
				Shell shell = workbench.getDisplay().getActiveShell();
				shell.getDisplay().addFilter(SWT.FocusIn, new Listener() {
					@Override
					public void handleEvent(Event e) {
						logger.debug(this.getClass().getName() + ".handleEvent");
						selectedControl = display.getFocusControl();
						System.out.println(selectedControl);
					}
				});

				shell.getDisplay().addFilter(SWT.FocusOut, new Listener() {
					@Override
					public void handleEvent(Event e) {
						logger.debug(this.getClass().getName() + ".handleEvent");
						selectedControl = display.getFocusControl();
						System.out.println(selectedControl);
					}
				});
				//				shell.addFocusListener(new FocusListener() {
				//					@Override
				//					public void focusGained(FocusEvent e) {
				//						selectedControl = display.getFocusControl();
				//						System.out.println(selectedControl);						
				//					}
				//					@Override
				//					public void focusLost(FocusEvent arg0) {
				//						selectedControl = display.getFocusControl();
				//						System.out.println(selectedControl);
				//					}
				//				});
				}
			});

		//		final Shell shell = workbench.getActiveWorkbenchWindow().getShell();


		//		display.asyncExec(new Runnable(){
		//			@Override
		//			public void run() {
		//				shell.addFocusListener(new FocusListener() {
		//					@Override
		//					public void focusGained(FocusEvent e) {
		//						selectedControl = display.getFocusControl();
		//						System.out.println(selectedControl);						
		//					}
		//					@Override
		//					public void focusLost(FocusEvent arg0) {
		//						selectedControl = display.getFocusControl();
		//						System.out.println(selectedControl);
		//					}
		////					@Override
		////					public void handleEvent(Event e) {
		////						logger.debug(this.getClass().getName() + ".handleEvent");
		////						selectedControl = display.getFocusControl();
		////						System.out.println(selectedControl);
		////					}
		//				});
		////				display.addFilter(SWT.FocusOut, new Listener() {
		////					@Override
		////					public void handleEvent(Event e) {
		////						logger.debug(this.getClass().getName() + ".handleEvent");
		////						selectedControl = display.getFocusControl();
		////						System.out.println(selectedControl);
		////					}
		////				});
		//			}
		//		});
		}

	}
