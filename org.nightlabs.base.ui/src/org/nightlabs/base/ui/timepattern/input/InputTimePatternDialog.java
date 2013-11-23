package org.nightlabs.base.ui.timepattern.input;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.message.IMessageDisplayer;
import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.timepattern.InputTimePattern;

/**
 * Dialog to build an {@link InputTimePattern}. It will show a list of
 * {@link PredefinedInputTimePatternProvider}s and an option for custom patterns.
 * <p>
 * Use the static methods {@link #open(Shell, InputTimePattern)} and
 * {@link #open(Shell, InputTimePattern, PredefinedInputTimePatternProvider...)} for convenient
 * usage.
 * </p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * 
 */
public class InputTimePatternDialog extends ResizableTitleAreaDialog {

	/**
	 * Provider for {@link InputTimePattern} referencing the beginning of last month.
	 */
	public static class BeginningOfLastMonth implements PredefinedInputTimePatternProvider {
		@Override
		public InputTimePattern createInputTimePattern() {
			return new InputTimePattern("Y|M-1|1|0|0|0");
		}
		@Override
		public String getName() {
			return "The beginning of the last month";
		}
	}
	
	/**
	 * Provider for {@link InputTimePattern} referencing the end of last month.
	 */
	public static class EndOfLastMonth implements PredefinedInputTimePatternProvider {
		@Override
		public InputTimePattern createInputTimePattern() {
			return new InputTimePattern("Y|M-1|L|L|L|L");
		}
		@Override
		public String getName() {
			return "The end of last month";
		}
	}
	
	/**
	 * Provider for {@link InputTimePattern} referencing the beginning of the last day.
	 */
	public static class BeginningOfLastDay implements PredefinedInputTimePatternProvider {
		@Override
		public InputTimePattern createInputTimePattern() {
			return new InputTimePattern("Y|M|D-1|0|0|0");
		}
		@Override
		public String getName() {
			return "The beginning of last day";
		}
	}
	
	/**
	 * Provider for {@link InputTimePattern} referencing the end of the last day.
	 */
	public static class EndOfLastDay implements PredefinedInputTimePatternProvider {
		@Override
		public InputTimePattern createInputTimePattern() {
			return new InputTimePattern("Y|M|D-1|L|L|L");
		}
		@Override
		public String getName() {
			return "The end of last day";
		}
	}
	
	/** {@link PredefinedInputTimePatternProvider} this dialog was constructed with */
	private final PredefinedInputTimePatternProvider[] inputPatternProviders;
	/** Radio buttons created for the {@link PredefinedInputTimePatternProvider}s */
	private List<Button> providerButtons;
	/** Button for enabling a custom creation */
	private Button customPatternButton;
	/** {@link InputTimePatternComposite} displaying the current pattern */
	private InputTimePatternComposite inputTimePatternComposite;

	/** {@link InputTimePatternComposite} this dialog was constructed with */
	private final InputTimePattern inputTimePattern;
	/** {@link InputTimePatternComposite} that is the result of opening this dialog set only in {@link #okPressed()} */
	private InputTimePattern resultTimePattern;
	
	/**
	 * {@link SelectionListener} that will update the time pattern with the selected
	 * {@link PredefinedInputTimePatternProvider}
	 */
	private SelectionListener providerButtonSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (((Button) e.widget).getSelection()) {
				Object data = e.widget.getData();
				if (data instanceof PredefinedInputTimePatternProvider) {
					PredefinedInputTimePatternProvider provider = (PredefinedInputTimePatternProvider) data;
					InputTimePattern pattern = provider.createInputTimePattern();
					inputTimePatternComposite.setInputTimePattern(pattern);
				}
			}
		}
	};

	/**
	 * Create a new {@link InputTimePatternDialog} that will initially display the given
	 * {@link InputTimePattern} and present a list of the given
	 * {@link PredefinedInputTimePatternProvider}s to the user to choose from.
	 * 
	 * @param shell The parent shell.
	 * @param inputTimePattern The {@link InputTimePattern} that should be initially displayed. Can
	 *            be <code>null</code> and defaults to a <code>new InputTimePattern()</code>.
	 * @param inputTimePatternProviders The list of {@link PredefinedInputTimePatternProvider} the
	 *            user can choose from to create a predefined {@link InputTimePattern}.
	 */
	public InputTimePatternDialog(Shell shell, InputTimePattern inputTimePattern, PredefinedInputTimePatternProvider... inputTimePatternProviders) {
		super(shell, null);
		this.inputPatternProviders = inputTimePatternProviders;
		this.inputTimePattern = inputTimePattern != null ? inputTimePattern : new InputTimePattern();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Creates all controls of this dialog and adds selection-listeners and validator. 
	 * </p>
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Build an input time pattern");
		final String defaultMessage = "Select either predefined patterns or create a custom one";
		setMessage(defaultMessage);
		
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		
		Label l = new Label(wrapper, SWT.WRAP);
		l.setText("The following predefined patterns are available. Based on the input-time they will reference");
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// The provider-buttons all use the same selection-listener that will access the data of the
		// buttons to obtain the selected provider
		providerButtons = new LinkedList<Button>();
		XComposite radioGroup = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		for (PredefinedInputTimePatternProvider provider : inputPatternProviders) {
			Button providerButton = new Button(radioGroup, SWT.RADIO);
			providerButton.setData(provider);
			providerButton.setText(provider.getName());
			providerButton.addSelectionListener(providerButtonSelectionListener);
			providerButtons.add(providerButton);
		}
		
		Label sep = new Label(radioGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		customPatternButton = new Button(radioGroup, SWT.RADIO);
		customPatternButton.setText("Create a custom pattern");
		customPatternButton.setSelection(true);
		
		inputTimePatternComposite = new InputTimePatternComposite(wrapper, SWT.NONE);
		inputTimePatternComposite.setInputTimePattern(inputTimePattern);
		// The customParameterButton will be auto-selected when the pattern is changed
		inputTimePatternComposite.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				// When the Pattern is modified we switch to custom
				customPatternButton.setSelection(true);
				for (Button providerButton : providerButtons) {
					providerButton.setSelection(false);
				}
			}
		});
		
		// Display the validation messages in the dialog and update the enablement of the OK button
		inputTimePatternComposite.setValidationMessageDisplayer(new IMessageDisplayer() {
			@Override
			public void setMessage(String message, int type) {
				if (type == IMessageProvider.ERROR) {
					InputTimePatternDialog.this.setErrorMessage(message);
				} else {
					setErrorMessage(null);
					InputTimePatternDialog.this.setMessage(defaultMessage);
				}
				getButton(IDialogConstants.OK_ID).setEnabled(type != IMessageProvider.ERROR);
			}
			@Override
			public void setMessage(String message, MessageType type) {
				setMessage(message, type.ordinal());
			}
		});
		
		return wrapper;
	}

	/**
	 * @return The result of this dialog, a new {@link InputTimePattern}. Note, that this will
	 *         return <code>null</code> when the dialog is canceled or while it is still open.
	 */
	public InputTimePattern getResultTimePattern() {
		return resultTimePattern;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets the {@link #getResultTimePattern()}.
	 * </p>
	 */
	@Override
	protected void okPressed() {
		resultTimePattern = inputTimePatternComposite.getInputTimePattern();
		super.okPressed();
	}

	/**
	 * Opens an {@link InputTimePatternDialog} and returns its result. The dialog will initially
	 * display the given {@link InputTimePattern} and present a list of the given
	 * {@link PredefinedInputTimePatternProvider}s to the user to choose from.
	 * 
	 * @param shell The parent shell.
	 * @param inputTimePattern The {@link InputTimePattern} that should be initially displayed. Can
	 *            be <code>null</code> and defaults to a <code>new InputTimePattern()</code>.
	 * @param inputTimePatternProviders The list of {@link PredefinedInputTimePatternProvider} the
	 *            user can choose from to create a predefined {@link InputTimePattern}.
	 * @return The dialog result, a new {@link InputTimePattern}. This will be <code>null</code>
	 *         when the user canceled the dialog or when for some reason the result should be
	 *         invalid.
	 */
	public static InputTimePattern open(Shell shell, InputTimePattern inputTimePattern, PredefinedInputTimePatternProvider... inputTimePatternProviders) {
		InputTimePatternDialog dlg = new InputTimePatternDialog(shell, inputTimePattern, inputTimePatternProviders);
		if (dlg.open() == Window.OK) {
			return dlg.getResultTimePattern();
		}
		return null;
	}

	/**
	 * Opens an {@link InputTimePatternDialog} and returns its result. The dialog will initially
	 * display the given {@link InputTimePattern} and present the default list of
	 * {@link PredefinedInputTimePatternProvider}s to the user to choose from.
	 * 
	 * @param shell The parent shell.
	 * @param inputTimePattern The {@link InputTimePattern} that should be initially displayed. Can
	 *            be <code>null</code> and defaults to a <code>new InputTimePattern()</code>.
	 * @return The dialog result, a new {@link InputTimePattern}. This will be <code>null</code>
	 *         when the user canceled the dialog or when for some reason the result should be
	 *         invalid.
	 */
	public static InputTimePattern open(Shell shell, InputTimePattern inputTimePattern) {
		return open(shell, inputTimePattern, 
				new BeginningOfLastDay(),
				new EndOfLastDay(),
				new BeginningOfLastMonth(),
				new EndOfLastMonth());
	}

	public static void main (String [] args) {
		Display display = new Display ();
		
		InputTimePattern pattern = open(null, new InputTimePattern("Y-1|M|D|h|m|s"));
		System.out.println(pattern.getDefinitionString());
		display.dispose();
		
		while (!display.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}

}
