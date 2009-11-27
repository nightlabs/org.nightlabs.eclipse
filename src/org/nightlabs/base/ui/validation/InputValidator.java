package org.nightlabs.base.ui.validation;

import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.datastructure.Pair;

/**
 * Simple base interface for all kinds of validators.
 *
 * @author Marius Heinzmann <!-- marius[at]nightlabs[dot]de -->
 */
public interface InputValidator<T>
{
	/**
	 * Validates the given input and in case something is wrong may return the MessageType of the error message
	 * (Information, Warning, Error) as well as the message itself.
	 *
	 * @param input The input to validate.
	 * @return In case something is wrong may return the MessageType of the error message (Information, Warning, Error)
	 * 		as well as the message itself.
	 */
	Pair<MessageType, String> validateInput(T input);
}