package org.nightlabs.base.ui.validation;

import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.datastructure.Pair;

/**
 * Abstract base implementation of a {@link InputValidator} that enforces the given input object to be of the declared
 * type.
 *
 * @author Marius Heinzmann <!-- marius[at]nightlabs[dot]de -->
 * @param <T> The type of the input object.
 */
public abstract class AbstractValidator<T>
	implements InputValidator<T>
{
	private Class<T> inputClass;

	public AbstractValidator(Class<T> inputClass)
	{
		assert inputClass != null;
		this.inputClass = inputClass;
	}

	/**
	 * Enforces that the given input object is of the declared type.
	 *
	 * @see org.nightlabs.base.ui.validation.InputValidator#validateInput(java.lang.Object)
	 */
	@Override
	public Pair<MessageType, String> validateInput(T input)
	{
		assert(inputClass.isAssignableFrom(input.getClass()));
		return doValidateInput(input);
	}

	/**
	 * Does the validation as in {@link InputValidator#validateInput(Object)}.
	 * <p>Note: <br>
	 * 	The given input object definitely is of the declared type, due to enforcement (runtime-checks).
	 * </p>
	 *
	 * {@inheritDoc InputValidator#validateInput(Object)}
	 */
	public abstract Pair<MessageType, String> doValidateInput(T input);
}
