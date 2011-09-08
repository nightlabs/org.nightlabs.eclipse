/**
 * 
 */
package org.nightlabs.base.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A small class that observes FocusIn-events and set the shell default-button
 * to a given button if a child-widget of a given control is focussed.
 * 
 * @author abieber
 */
public class DefaultButtonFilter {

	private Button button;
	private Control parentControl;
	
	private Button defButtonBackup;
	private boolean defButtonBackupMade = false;
	
	private Listener filter = new Listener() {
		@Override
		public void handleEvent(Event event) {
			if ((event.widget instanceof Control) && isChild((Control) event.widget)) {
				if (!defButtonBackupMade) {
					defButtonBackup = getDefButton();
					defButtonBackupMade = true;
				}
				setDefButton(DefaultButtonFilter.this.button);
			} else {
				if (defButtonBackupMade) {
					setDefButton(defButtonBackup);
					defButtonBackupMade = false;
				}
			}
		}

		private Button getDefButton() {
			return DefaultButtonFilter.this.button.getShell().getDefaultButton();
		}
		
		private void setDefButton(Button button) {
			DefaultButtonFilter.this.button.getShell().setDefaultButton(button);
		}
	};
	
	
	/**
	 * Create a new new {@link DefaultButtonFilter}. It will dispose off itself,
	 * when the given button is destroyed. The given button will be set to its
	 * shell default-button whenever a child-control of the given parentControl
	 * is focussed.
	 */
	public DefaultButtonFilter(Button button, Control parentControl) {
		this.button = button;
		this.parentControl = parentControl;
		final Display display = button.getDisplay();
		display.addFilter(SWT.FocusIn, filter);
		button.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				display.removeFilter(SWT.FocusIn, filter);
			}
		});
	}
	
	private boolean isChild(Control control) {
		Control parent = control;
		while (parent != null) {
			if (parent == parentControl)
				return true;
			parent = parent.getParent();
		}
		return false;
	}
}
