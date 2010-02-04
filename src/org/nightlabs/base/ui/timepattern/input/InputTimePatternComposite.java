/**
 * 
 */
package org.nightlabs.base.ui.timepattern.input;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.message.IMessageDisplayer;
import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.validation.AbstractValidator;
import org.nightlabs.base.ui.validation.ValidationMessageSupport;
import org.nightlabs.datastructure.Pair;
import org.nightlabs.timepattern.InputTimePattern;

/**
 * A Composite displaying a Text field for each {@link InputTimePattern} part.
 * <p>
 * </p>
 * The Composite will validate the {@link InputTimePattern} it displays and delegate validation
 * messages to an {@link IMessageDisplayer} (see.
 * {@link #setValidationMessageDisplayer(IMessageDisplayer)})
 * </p>
 * <p>
 * {@link ModifyListener}s can be added to the Composite in order to get notified of changes that
 * the user made to an {@link InputTimePattern}.
 * </p>
 *  
 * @author Alexander Bieber
 */
public class InputTimePatternComposite extends XComposite {

	private Text year;
	private Text month;
	private Text dayOfMonth;
	private Text hour;
	private Text minute;
	private Text second;
	
	/** Used to validate and notify validation messages */
	private ValidationMessageSupport<InputTimePattern> validationMessageSupport = new ValidationMessageSupport<InputTimePattern>(new AbstractValidator<InputTimePattern>(InputTimePattern.class) {
		@Override
		public Pair<MessageType, String> doValidateInput(InputTimePattern input) {
			if (input.validate() != null) {
				return new Pair<MessageType, String>(MessageType.ERROR, 
						String.format("InputTimePattern (%s) is invalid, use (Y|M|D|h|m|s).", 
								input.getDefinitionString()));
			} else {
				return new Pair<MessageType, String>(MessageType.NONE, "");
			}
			
		}
	});
	
	/** Will be true when {@link InputTimePattern} is set from outside */
	private boolean refreshing = false;
	/** Text ModifyListener added to all Texts created */
	private ModifyListener textModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent evt) {
			if (!refreshing) {
				validationMessageSupport.validateInput(createInputTimePattern());
				notifyModifyListeners(evt);
			}
		}
	};
	
	/** The modify listeners added to this Composite */
	private ListenerList modifyListeners = new ListenerList();
	
	/**
	 * Create a new {@link InputTimePatternComposite}
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use.
	 */
	public InputTimePatternComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.LEFT_RIGHT_WRAPPER);
		getGridLayout().numColumns = 6;
		Label l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.year")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.month")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.day")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.hour")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.minute")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new Label(this, SWT.CENTER);
		l.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.label.second")); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		year = new Text(this, getBorderStyle() | SWT.CENTER | SWT.CENTER);
		year.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		year.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.y")); //$NON-NLS-1$
		year.addModifyListener(textModifyListener);
		month = new Text(this, getBorderStyle() | SWT.CENTER);
		month.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		month.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.m")); //$NON-NLS-1$
		month.addModifyListener(textModifyListener);
		dayOfMonth = new Text(this, getBorderStyle() | SWT.CENTER);
		dayOfMonth.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dayOfMonth.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.d")); //$NON-NLS-1$
		dayOfMonth.addModifyListener(textModifyListener);
		hour = new Text(this, getBorderStyle() | SWT.CENTER);
		hour.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hour.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.h")); //$NON-NLS-1$
		hour.addModifyListener(textModifyListener);
		minute = new Text(this, getBorderStyle() | SWT.CENTER);
		minute.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		minute.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.min")); //$NON-NLS-1$
		minute.addModifyListener(textModifyListener);
		second = new Text(this, getBorderStyle() | SWT.CENTER);
		second.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		second.setText(Messages.getString("org.nightlabs.base.ui.timepattern.input.InputTimePatternComposite.text.s")); //$NON-NLS-1$
		second.addModifyListener(textModifyListener);
	}
	
	/**
	 * Set the {@link InputTimePattern} this Composite should display. This will trigger validation.
	 * 
	 * @param input The {@link InputTimePattern} to set.
	 */
	public void setInputTimePattern(InputTimePattern input) {
		refreshing = true;
		try {
			year.setText(input.getYear());
			month.setText(input.getMonth());
			dayOfMonth.setText(input.getDayOfMonth());
			hour.setText(input.getHour());
			minute.setText(input.getMinute());
			second.setText(input.getSecond());
			validationMessageSupport.validateInput(createInputTimePattern());
		} finally {
			refreshing = false;
		}
	}

	/**
	 * Use internally, creates an {@link InputTimePattern} from the current values in the UI.
	 *  
	 * @return A new {@link InputTimePattern} with the values from the UI.
	 */
	protected InputTimePattern createInputTimePattern() {
		return new InputTimePattern(
				year.getText().trim(),
				month.getText().trim(),
				dayOfMonth.getText().trim(),
				hour.getText().trim(),
				minute.getText().trim(),
				second.getText().trim()
			);
	}

	/**
	 * Returns the {@link InputTimePattern} with the values as currently defined in the UI of this
	 * {@link Composite}. Note that if the {@link InputTimePattern} can't be validated this will
	 * return <code>null</code>.
	 * 
	 * @return Either the {@link InputTimePattern} with the values from the UI, or <code>null</code>. 
	 */
	public InputTimePattern getInputTimePattern() {
		InputTimePattern pattern = createInputTimePattern(); 
		if (pattern.validate() == null) {
			return pattern;
		} else {
			return null;
		}
	}
	
	/**
	 * Add a {@link ModifyListener} to changes in the pattern.
	 * 
	 * @param modifyListener The listener to add.
	 */
	public void addModifyListener(ModifyListener modifyListener) {
		modifyListeners.add(modifyListener);
	}
	
	/**
	 * Remove a {@link ModifyListener}.
	 * 
	 * @param modifyListener The listener to remove.
	 */
	public void removeModifyListener(ModifyListener modifyListener) {
		modifyListeners.remove(modifyListener);
	}
	
	/**
	 * Notify all {@link ModifyListener}s
	 * @param evt The event.
	 */
	protected void notifyModifyListeners(ModifyEvent evt) {
		Object[] listeners = modifyListeners.getListeners();
		for (Object listner : listeners) {
			if (listner instanceof ModifyListener) {
				((ModifyListener) listner).modifyText(evt);
			}
		}
	}

	/**
	 * Set the {@link IMessageDisplayer} that displays the validation messages occuring when the
	 * {@link InputTimePattern} is changed by the user.
	 * 
	 * @param messageDisplayer The {@link IMessageDisplayer} to set.
	 */
	public void setValidationMessageDisplayer(IMessageDisplayer messageDisplayer) {
		validationMessageSupport.setValidationMessageDisplayer(messageDisplayer);
	}
	
}
