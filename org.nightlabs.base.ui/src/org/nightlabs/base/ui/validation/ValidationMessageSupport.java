package org.nightlabs.base.ui.validation;

import org.nightlabs.base.ui.message.IMessageDisplayer;
import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.datastructure.Pair;

/**
 * Simple class that uses an {@link IMessageDisplayer} to which it delegates validation messages of
 * an {@link InputValidator}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ValidationMessageSupport<T> {

	private InputValidator<T> validator;
	private IMessageDisplayer validationMessageDisplayer;

	/**
	 * Create a new {@link ValidationMessageSupport} for the given {@link InputValidator}. Note that
	 * you'll have to set an {@link IMessageDisplayer} in order to receive validation messages.
	 * 
	 * @param validator The validator that should be used.
	 */
	public ValidationMessageSupport(InputValidator<T> validator) {
		this.validator = validator;
	}

	/**
	 * Set the {@link IMessageDisplayer} this object will delegate validation messages to.
	 * 
	 * @param messageDisplayer The displayer to set.
	 */
	public void setValidationMessageDisplayer(IMessageDisplayer messageDisplayer) {
		this.validationMessageDisplayer = messageDisplayer;
	}
	
	/**
	 * @return The {@link IMessageDisplayer} currently set.
	 */
	public IMessageDisplayer getValidationMessageDisplayer() {
		return validationMessageDisplayer;
	}

	/**
	 * Validate the given input using the {@link InputValidator} of this object and delegate to the
	 * {@link IMessageDisplayer}.
	 * 
	 * @param input The input to validate.
	 * @return The validation result.
	 */
	public Pair<MessageType, String> validateInput(T input) {
		Pair<MessageType, String> validationResult = validator.validateInput(input);
		if (validationMessageDisplayer != null && validationResult != null)
			validationMessageDisplayer.setMessage(validationResult.getSecond(), validationResult.getFirst());
		return validationResult;
	}

}
