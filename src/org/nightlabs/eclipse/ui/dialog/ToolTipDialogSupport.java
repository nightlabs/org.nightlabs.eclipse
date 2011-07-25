package org.nightlabs.eclipse.ui.dialog;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.compatibility.CompatibleSWT;

/**
 * Support for opening and closing a {@link Dialog} automatically as a tool-tip for certain
 * {@link Control}s. When the user hovers over one of the registered controls, the dialog
 * will be opened. As soon as the user moves the mouse, the dialog will automatically be
 * closed.
 * <p>
 * Note, that all methods in this class have to be called on the SWT UI thread!
 * </p>
 * <p>
 * In order to use this class, you typically create an instance of {@link ToolTipDialogSupport}
 * as field in your Dialog as follows:
 * <pre>
 *	public class MyTooltipDialog extends Dialog
 *	{
 *		private ToolTipDialogSupport toolTipDialogSupport = new ToolTipDialogSupport(this);
 *
 *		public ImageDataFieldPreviewTooltipDialog(Shell parentShell) {
 *			super(parentShell);
 *
 *			// If you want to control enabling or disabling dependent on the related
 *			// controls, you might want to start in disabled state.
 *			//toolTipDialogSupport.setEnabled(false);
 *		}
 *
 *		// It's a good idea to expose the ToolTipDialogSupport - e.g. to allow enabling &
 *		// disabling from the outside.
 *		public ToolTipDialogSupport getToolTipDialogSupport() {
 *			return toolTipDialogSupport;
 *		}
 *
 *		// Normally, you want no OK/Cancel button in a tooltip. Thus, we suppress the button bar.
 *		protected Control createButtonBar(Composite parent) {
 *			return null;
 *		}
 *
 *		// To place the dialog wherever the mouse currently is, we override configureShell(...).
 *		// Alternatively, we could individually override getInitialLocation() and getInitialSize().
 *		protected void configureShell(Shell newShell) {
 *			super.configureShell(newShell);
 *
 *			Point cursorLocation = newShell.getDisplay().getCursorLocation();
 *			Rectangle bounds = newShell.getBounds();
 *			bounds.x = cursorLocation.x;
 *			bounds.y = cursorLocation.y;
 *			bounds.width = newShell.getDisplay().getBounds().width / 2;
 *			bounds.height = newShell.getDisplay().getBounds().height / 2;
 *			newShell.setBounds(bounds);
 *			newShell.setText("My nice tooltip dialog");
 *		}
 *
 *		// Finally, put some meaningful information into the dialog.
 *		protected Control createDialogArea(Composite parent) {
 *			page = (Composite) super.createDialogArea(parent);
 *
 *			// ...
 *
 *			return page;
 *		}
 * }
 * </pre>
 * </p>
 * <p>
 * In your UI that contains the controls to trigger the tool-tip dialog, you use code like this:
 * <pre>
 *	public class MyComposite extends Composite
 *	{
 *		private Label whateverLabel;
 *		private Text whateverText;
 *		private MyTooltipDialog myTooltipDialog;
 *
 *		public MyComposite(Composite parent, int style) {
 *			// Create whateverLabel and whateverText with correct layout information as usual.
 *			// ...
 *
 *			// Create the tooltip dialog.
 *			myTooltipDialog = new MyTooltipDialog(shell);
 *
 *			// Register the controls that should get our custom tooltip.
 *			myTooltipDialog.getToolTipDialogSupport().register(whateverLabel);
 *			myTooltipDialog.getToolTipDialogSupport().register(whateverText);
 *		}
 *	}
 * </pre>
 * </p>
 *
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class ToolTipDialogSupport
{
	private static final Logger logger = Logger.getLogger(ToolTipDialogSupport.class);

	private Display display;
	private Shell shell;
	private Dialog dialog;
	private Set<Control> controls = new HashSet<Control>();
	private boolean enabled = true;
	private boolean dialogOpen = false;

	/**
	 * Create an instance of {@link ToolTipDialogSupport} managing (i.e. automatically opening and closing)
	 * the given <code>dialog</code>.
	 *
	 * @param dialog the dialog that shall be used as tooltip.
	 */
	public ToolTipDialogSupport(Dialog dialog) {
		this.dialog = dialog;

		if (dialog == null)
			throw new IllegalArgumentException("dialog must not be null!");
	}

	private Timer mouseSurveillanceTimer = new Timer("ToolTipDialogSupport@"+ Integer.toHexString(System.identityHashCode(this)) + ".mouseSurveillanceTimer" , true);

	private CloseDialogMouseSurveillanceTimerTask closeDialogMouseSurveillanceTimerTask;
	private class CloseDialogMouseSurveillanceTimerTask extends TimerTask
	{
		private PointerInfo pointerInfo = null;

		@Override
		public void run() {
			PointerInfo newPointerInfo = MouseInfo.getPointerInfo();
			if (pointerInfo == null) {
				pointerInfo = newPointerInfo;
				return;
			}

			if (
					enabled && // We close, if it's not enabled (anymore).
					pointerInfo.getDevice().equals(newPointerInfo.getDevice()) &&
					pointerInfo.getLocation().equals(newPointerInfo.getLocation())
			)
				return; // No movement made => Nothing to do.

			if (display == null)
				return;

			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					dialog.close();
				}
			});
		}
	};

	/**
	 * Check whether the current thread is the SWT UI thread. Throws an {@link IllegalStateException} otherwise.
	 */
	protected void assertUIThread()
	{
		if (display != null) {
			if (Display.getCurrent() != display)
				throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread. It is either executed on a non-UI thread or on the wrong UI thread.");
		}
		else if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread.");
	}

	private void registerCloseDialogMouseSurveillanceTimerTask()
	{
		if (closeDialogMouseSurveillanceTimerTask != null)
			closeDialogMouseSurveillanceTimerTask.cancel();

		closeDialogMouseSurveillanceTimerTask = new CloseDialogMouseSurveillanceTimerTask();
		mouseSurveillanceTimer.schedule(closeDialogMouseSurveillanceTimerTask, 0, 300);
	}

	private void unregisterCloseDialogMouseSurveillanceTimerTask()
	{
		if (closeDialogMouseSurveillanceTimerTask != null) {
			closeDialogMouseSurveillanceTimerTask.cancel();
			closeDialogMouseSurveillanceTimerTask = null;
		}
	}

	/**
	 * Get the dialog that is controlled by this <code>ToolTipDialogSupport</code>.
	 * @return the dialog.
	 */
	public Dialog getDialog() {
		return dialog;
	}

	/**
	 * Add the custom dialog managed by this <code>ToolTipDialogSupport</code>
	 * as tool-tip to the given <code>control</code>. Instead of the ordinary
	 * tool-tip, the special dialog will be opened.
	 * <p>
	 * Calling this method twice (i.e. again for a control that was already
	 * registered) causes an {@link IllegalArgumentException}.
	 * </p>
	 * <p>
	 * It is normally not necessary to {@link #unregister(Control)} the control
	 * since cleanup is done automatically when a control is disposed
	 * (via a {@link DisposeListener}).
	 * </p>
	 *
	 * @param control a control that should use the custom dialog as tool-tip.
	 * @see #unregister(Control)
	 */
	public void register(Control control)
	{
		assertUIThread();

		if (control == null)
			throw new IllegalArgumentException("control must not be null!");

		if (!controls.add(control))
			throw new IllegalArgumentException("This control has already been registered! Cannot register twice: " + control);

		if (shell != null && shell != control.getShell())
			throw new IllegalArgumentException("Shell mismatch! All controls must have the same shell! Cannot add control: " + control);

		if (display == null) {
			shell = control.getShell();
			display = shell.getDisplay();
		}

		control.addDisposeListener(controlDisposeListener);
		CompatibleSWT.addMouseTrackListener(control, controlMouseTrackListener);
	}

	private MouseTrackListener controlMouseTrackListener = new MouseTrackAdapter() {
		@Override
		public void mouseHover(MouseEvent e) {
			assertUIThread();

			if (logger.isTraceEnabled())
				logger.trace("controlMouseTrackListener.mouseHover: Position: (" + e.x + '|' + e.y + ')');

			if (dialogOpen) { // should not happen, but safer is better.
				logger.warn("controlMouseTrackListener.mouseHover: Dialog is already open! This should never happen!");

				return;
			}

			if (!enabled) {
				if (logger.isDebugEnabled())
					logger.debug("controlMouseTrackListener.mouseHover: Not enabled! Will not open dialog.");

				return;
			}

			if (logger.isDebugEnabled())
				logger.debug("controlMouseTrackListener.mouseHover: Opening dialog.");

			dialog.setBlockOnOpen(true);
			dialogOpen = true;
			try {
				registerCloseDialogMouseSurveillanceTimerTask();
				try {
					dialog.open();
				} finally {
					unregisterCloseDialogMouseSurveillanceTimerTask();
				}
			} finally {
				dialogOpen = false;

				if (logger.isDebugEnabled())
					logger.debug("OpenDelayedTimerTask.asyncExec.run: Dialog has been closed.");
			}
		}
	};

	private DisposeListener controlDisposeListener = new DisposeListener() {
		@Override
		public void widgetDisposed(DisposeEvent event) {
			unregister((Control)event.widget);
		}
	};

	/**
	 * Unregister the custom tool-tip dialog from the control. Normally, you don't
	 * need to do this, because disposing a control automatically unregisters it.
	 * <p>
	 * Calling this method
	 * with a control that was not {@link #register(Control) registered} before, will
	 * cause an {@link IllegalArgumentException}.
	 * </p>
	 *
	 * @param control the control that should not anymore use the custom dialog as tool-tip.
	 * @see #register(Control)
	 */
	public void unregister(Control control)
	{
		assertUIThread();

		if (!controls.remove(control))
			throw new IllegalArgumentException("This control has not been registered before! Cannot unregister: " + control);

		control.removeDisposeListener(controlDisposeListener);
		CompatibleSWT.removeMouseTrackListener(control, controlMouseTrackListener);
	}

	/**
	 * Disable or enable the tool-tip dialog. When your tool-tip shows information that is optional,
	 * you might want to disable the dialog when data is not available.
	 * <p>
	 * Default value is <code>true</code>.
	 * </p>
	 *
	 * @param enabled the new enabled-state.
	 * @see #isEnabled()
	 */
	public void setEnabled(boolean enabled) {
		assertUIThread();

		this.enabled = enabled;
	}
	/**
	 * Get the current enabled-state of the tool-tip dialog. A new instance of
	 * {@link ToolTipDialogSupport} is enabled by default.
	 *
	 * @return the current enabled-state.
	 */
	public boolean isEnabled() {
		assertUIThread();

		return enabled;
	}
}
