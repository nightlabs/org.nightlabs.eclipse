package org.nightlabs.eclipse.ui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.eclipse.ui.composite.XComposite;
import org.nightlabs.eclipse.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.eclipse.ui.composite.XComposite.LayoutMode;
import org.nightlabs.eclipse.ui.dialog.resource.Messages;

/**
 * A dialog that presents a textbox for a new password as well as another text box to confirm the new password.
 * Additionally, it shows a bar that indicates the strength of the entered password.
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class ChangePasswordDialog extends CenteredDialog
{
	private Text newPasswordText;
	private Text confirmPasswordText;
	private ProgressBar passwordStrengthBar;
	private Label messageLabel;
	private IInputValidator passwordValidator;
	private IPasswordMeter passwordMeter;
	private String confirmedPassword;
	
	public static interface IPasswordMeter {
		/**
		 * Returns an integer that rates the given password in the range 0 to {@link #getMaxPasswordMetric()}.
		 */
		int ratePassword(String password);
		
		/**
		 * Returns the maximum achievable password metric returned by {@link #ratePassword(String)}.
		 * @return the maximum achievable password metric returned by {@link #ratePassword(String)}.
		 */
		int getMaxPasswordMetric();
	}
	
	private ModifyListener passwordModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			if (e.getSource() == newPasswordText) {
				int passwordMetric = passwordMeter.ratePassword(newPasswordText.getText());
				passwordStrengthBar.setSelection(passwordMetric);
			}
			
			boolean okButtonEnabled = true;
			String message = ""; //$NON-NLS-1$
			
			String validationMessage = passwordValidator.isValid(newPasswordText.getText());
			if (validationMessage != null) {
				okButtonEnabled = false;
				message = validationMessage;
			} else if (!newPasswordText.getText().equals(confirmPasswordText.getText())) {
				okButtonEnabled = false;
				message = Messages.getString("org.nightlabs.base.ui.dialog.ChangePasswordDialog.message"); //$NON-NLS-1$
			}
			
			getButton(IDialogConstants.OK_ID).setEnabled(okButtonEnabled);
			messageLabel.setText(message);
		}
	};
	
	private static final IPasswordMeter defaultPasswordMeter = new IPasswordMeter() {
		@Override
		public int getMaxPasswordMetric() {
			return 11;
		}

		@Override
		public int ratePassword(String password) {
			int strength = 0;
			strength += Math.min(Math.round(password.length()/5f), 4);
			
			int specialChars = countSpecialCharOccurences(password);
			if (specialChars >= 4)
				strength += 3;
			else if (specialChars >= 2)
				strength += 2;
			else if (specialChars >= 1)
				strength += 1;
			
			int numberChars = countNumberOccurences(password);
			if (numberChars >= 4)
				strength += 3;
			else if (numberChars >= 2)
				strength += 2;
			else if (numberChars >= 1)
				strength += 1;
			
			int upperCaseLetters = countUppercaseLetterOccurences(password);
			if (upperCaseLetters >= 2)
				strength += 1;

			return strength;
		}
		
		private final char[] SPECIAL_CHARACTERS = "!§$%&/()=?`*'_:';,.-#+´\\}][{°^\"]<> ".toCharArray(); //$NON-NLS-1$
		private int countSpecialCharOccurences(String string) {
			return countOccurences(string, SPECIAL_CHARACTERS);
		}
		
		private final char[] NUMBERS = "01234567890".toCharArray(); //$NON-NLS-1$
		private int countNumberOccurences(String string) {
			return countOccurences(string, NUMBERS);
		}
		
		private final char[] UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(); //$NON-NLS-1$
		private int countUppercaseLetterOccurences(String string) {
			return countOccurences(string, UPPERCASE_LETTERS);
		}
	};
	
	/**
	 * Opens a password dialog and returns the password that was entered and confirmed by the user.
	 * @param parentShell The {@link Shell} to be used.
	 * @param passwordValidator A validator for the password.
	 * @param passwordMeter An {@link IPasswordMeter} that measures the strength of the entered password or <code>null</code> if the default
	 * 					meter should be used.
	 * @return the password that was entered and confirmed by the user.
	 */
	public static String openDialog(Shell parentShell, IInputValidator passwordValidator, IPasswordMeter passwordMeter) {
		ChangePasswordDialog dialog = new ChangePasswordDialog(parentShell, passwordValidator, passwordMeter);
		if (dialog.open() == Window.OK)
			return dialog.getConfirmedPassword();
		else
			return null;
	}
	
	public ChangePasswordDialog(Shell parentShell, IInputValidator passwordValidator, IPasswordMeter passwordMeter) {
		super(parentShell);
		
		if (passwordMeter == null)
			this.passwordMeter = defaultPasswordMeter;
		else
			this.passwordMeter = passwordMeter;
		
		if (passwordValidator == null)
			throw new IllegalArgumentException("passwordValidator must not be null!"); //$NON-NLS-1$
		this.passwordValidator = passwordValidator;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.base.ui.dialog.ChangePasswordDialog.shell.text")); //$NON-NLS-1$
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(350, 220);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		XComposite container = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA, 2);
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		Label label = new Label(container, SWT.NONE);
		label.setText(Messages.getString("org.nightlabs.base.ui.dialog.ChangePasswordDialog.label.newPassword")); //$NON-NLS-1$
		label.setLayoutData(gridData);

		label = new Label(container, SWT.NONE);
		label.setText(Messages.getString("org.nightlabs.base.ui.dialog.ChangePasswordDialog.label.password")); //$NON-NLS-1$
		newPasswordText = new Text(container, SWT.BORDER);
		newPasswordText.setEchoChar('*');
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, newPasswordText);
		
		label = new Label(container, SWT.NONE);
		label.setText(Messages.getString("org.nightlabs.base.ui.dialog.ChangePasswordDialog.label.confirmation")); //$NON-NLS-1$
		confirmPasswordText = new Text(container, SWT.BORDER);
		confirmPasswordText.setEchoChar('*');
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, confirmPasswordText);
		
		label = new Label(container, SWT.NONE);
		label.setText(Messages.getString("org.nightlabs.base.ui.dialog.ChangePasswordDialog.label.passwordStrength")); //$NON-NLS-1$
		passwordStrengthBar = new ProgressBar(container, SWT.NONE);
		passwordStrengthBar.setMinimum(0);
		passwordStrengthBar.setMaximum(passwordMeter.getMaxPasswordMetric());
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, passwordStrengthBar);
		
		messageLabel = new Label(container, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 10;
		messageLabel.setLayoutData(gridData);
		
		newPasswordText.addModifyListener(passwordModifyListener);
		confirmPasswordText.addModifyListener(passwordModifyListener);
		
		return container;
	}
	
	private static int countOccurences(String string, char[] chars) {
		int occ = 0;
		for (char s : chars) {
			for (char c : string.toCharArray())
				if (c == s)
					occ++;
		}
		return occ;
	}
	
	@Override
	protected void okPressed() {
		if (newPasswordText.getText().equals(confirmPasswordText.getText()) && passwordValidator.isValid(newPasswordText.getText()) == null)
			confirmedPassword = newPasswordText.getText();
		
		super.okPressed();
	}
	
	public String getConfirmedPassword() {
		return confirmedPassword;
	}
}
