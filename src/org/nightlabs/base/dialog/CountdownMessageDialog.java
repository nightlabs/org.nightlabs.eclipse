package org.nightlabs.base.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.resource.Messages;

/**
 * A dialog for showing messages to the user that disappears
 * automatically after a given time in seconds with the default 
 * return value if no user interaction happens until that time.
 * 
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class CountdownMessageDialog extends MessageDialog
{
  /**
   * Create a message dialog. Note that the dialog will have no visual
   * representation (no widgets) until it is told to open.
   * <p>
   * The labels of the buttons to appear in the button bar are supplied in
   * this constructor as an array. The <code>open</code> method will return
   * the index of the label in this array corresponding to the button that was
   * pressed to close the dialog. If the dialog was dismissed without pressing
   * a button (ESC, etc.) then -1 is returned. Note that the <code>open</code>
   * method blocks.
   * </p>
   * 
   * @param parentShell
   *            the parent shell
   * @param dialogTitle
   *            the dialog title, or <code>null</code> if none
   * @param dialogTitleImage
   *            the dialog title image, or <code>null</code> if none
   * @param dialogMessage
   *            the dialog message
   * @param dialogImageType
   *            one of the following values:
   *            <ul>
   *            <li><code>MessageDialog.NONE</code> for a dialog with no
   *            image</li>
   *            <li><code>MessageDialog.ERROR</code> for a dialog with an
   *            error image</li>
   *            <li><code>MessageDialog.INFORMATION</code> for a dialog
   *            with an information image</li>
   *            <li><code>MessageDialog.QUESTION </code> for a dialog with a
   *            question image</li>
   *            <li><code>MessageDialog.WARNING</code> for a dialog with a
   *            warning image</li>
   *            </ul>
   * @param dialogButtonLabels
   *            an array of labels for the buttons in the button bar
   * @param defaultIndex
   *            the index in the button label array of the default button
   * @param disappearTimeInSeconds
   * 						the time until this dialog disappears automatically whith
   * 						returning the default buttons return value 
   */
	public CountdownMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, int disappearTimeInSeconds)
	{
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
		this.timeLeft = disappearTimeInSeconds;
	}
	
	protected static final int DEFAULT_DISAPPEAR_TIME = 10;
	
	private int timeLeft; 
	private Button defaultButton;
	private String originalButtonText;
	private boolean isOpen;
	
	private Runnable countdownRunnable = new Runnable() {
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			//System.out.println("X isOpen: "+isOpen+"  timeLeft: "+timeLeft);
			while(isOpen && timeLeft > 0) {
				//System.out.println("isOpen: "+isOpen+"  timeLeft: "+timeLeft);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				timeLeft--;
				Display.getDefault().asyncExec(timeSetterRunnable);
			}
			if(timeLeft <= 0 && isOpen) {
				Display.getDefault().asyncExec(closeDialogRunnable);
			}
		}
	};
	
	private Runnable timeSetterRunnable = new Runnable() {
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			if(defaultButton != null && !defaultButton.isDisposed() && defaultButton.isVisible()) {
				//System.out.println("Set: "+timeLeft);
				showTimeHint(timeLeft);
			}
		}
	};
	
	private Runnable closeDialogRunnable = new Runnable() {
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			//System.out.println("Closing...");
			close();
		}
	};
	

	protected void showTimeHint(int timeLeft)
	{
		defaultButton.setText(String.format(Messages.getString("org.nightlabs.base.dialog.CountdownMessageDialogdefaultButton.text"), originalButtonText, timeLeft)); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.MessageDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		defaultButton = getButton(getDefaultButtonIndex());
		originalButtonText = defaultButton.getText();
		showTimeHint(timeLeft);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#open()
	 */
	@Override
	public int open()
	{
		isOpen = true;
		try {
			new Thread(countdownRunnable).start();
			int ret = super.open();
			return ret;
		} finally {
			isOpen = false;
		}
	}
	
  /**
   * Convenience method to open a simple confirm (OK/Cancel) dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @param disappearTimeInSeconds
   * 						the time until this dialog disappears automatically with
   * 						returning <code>true</code>
   * @return <code>true</code> if the user presses the OK button,
   *         <code>false</code> otherwise
   */
  public static boolean openConfirm(Shell parent, String title, String message, int disappearTimeInSeconds) {
  	CountdownMessageDialog dialog = new CountdownMessageDialog(parent, title, null, // accept
              // the
              // default
              // window
              // icon
              message, QUESTION, new String[] { IDialogConstants.OK_LABEL,
                      IDialogConstants.CANCEL_LABEL }, 0, disappearTimeInSeconds); // OK is the
      // default
      return dialog.open() == 0;
  }

  /**
   * Convenience method to open a standard error dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @param disappearTimeInSeconds
   * 						the time until this dialog disappears automatically
   */
  public static void openError(Shell parent, String title, String message, int disappearTimeInSeconds) {
  	CountdownMessageDialog dialog = new CountdownMessageDialog(parent, title, null, // accept
              // the
              // default
              // window
              // icon
              message, ERROR, new String[] { IDialogConstants.OK_LABEL }, 0, disappearTimeInSeconds); // ok
      // is
      // the
      // default
      dialog.open();
      return;
  }

  /**
   * Convenience method to open a standard information dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @param disappearTimeInSeconds
   * 						the time until this dialog disappears automatically
   */
  public static void openInformation(Shell parent, String title,
          String message, int disappearTimeInSeconds) {
  	CountdownMessageDialog dialog = new CountdownMessageDialog(parent, title, null, // accept
              // the
              // default
              // window
              // icon
              message, INFORMATION,
              new String[] { IDialogConstants.OK_LABEL }, 0, disappearTimeInSeconds);
      // ok is the default
      dialog.open();
      return;
  }

  /**
   * Convenience method to open a simple Yes/No question dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @param disappearTimeInSeconds
   * 						the time until this dialog disappears automatically with
   * 						returning <code>true</code>
   * @return <code>true</code> if the user presses the OK button,
   *         <code>false</code> otherwise
   */
  public static boolean openQuestion(Shell parent, String title,
          String message, int disappearTimeInSeconds) {
  	CountdownMessageDialog dialog = new CountdownMessageDialog(parent, title, null, // accept
              // the
              // default
              // window
              // icon
              message, QUESTION, new String[] { IDialogConstants.YES_LABEL,
                      IDialogConstants.NO_LABEL }, 0, disappearTimeInSeconds); // yes is the default
      return dialog.open() == 0;
  }

  /**
   * Convenience method to open a standard warning dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @param disappearTimeInSeconds
   * 						the time until this dialog disappears automatically
   */
  public static void openWarning(Shell parent, String title, String message, int disappearTimeInSeconds) {
  	CountdownMessageDialog dialog = new CountdownMessageDialog(parent, title, null, // accept
              // the
              // default
              // window
              // icon
              message, WARNING, new String[] { IDialogConstants.OK_LABEL }, 0, disappearTimeInSeconds); // ok
      // is
      // the
      // default
      dialog.open();
      return;
  }


  /**
   * Convenience method to open a simple confirm (OK/Cancel) dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @return <code>true</code> if the user presses the OK button,
   *         <code>false</code> otherwise
   */
  public static boolean openConfirm(Shell parent, String title, String message) {
  	return openConfirm(parent, title, message, DEFAULT_DISAPPEAR_TIME);
  }

  /**
   * Convenience method to open a standard error dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   */
  public static void openError(Shell parent, String title, String message) {
  	openError(parent, title, message, DEFAULT_DISAPPEAR_TIME);
  }

  /**
   * Convenience method to open a standard information dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   */
  public static void openInformation(Shell parent, String title,
          String message) {
  	openInformation(parent, title, message, DEFAULT_DISAPPEAR_TIME);
  }

  /**
   * Convenience method to open a simple Yes/No question dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @return <code>true</code> if the user presses the OK button,
   *         <code>false</code> otherwise
   */
  public static boolean openQuestion(Shell parent, String title,
          String message) {
  	return openQuestion(parent, title, message, DEFAULT_DISAPPEAR_TIME);
  }

  /**
   * Convenience method to open a standard warning dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   */
  public static void openWarning(Shell parent, String title, String message) {
  	openWarning(parent, title, message, DEFAULT_DISAPPEAR_TIME);
  }
}