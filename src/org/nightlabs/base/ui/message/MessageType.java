package org.nightlabs.base.ui.message;

import org.eclipse.jface.dialogs.IMessageProvider;

/**
 * This enum represents the message types that may be set {@link IMessageDisplayer}.
 * <p>Note:<br>
 * 	The enum values are ordered in such a way that the ordinal number corresponds to the int values of the
 * {@link IMessageProvider} interface.
 * </p>
 *
 * @author Marius Heinzmann <!-- marius[at]nightlabs[dot]de -->
 */
public enum MessageType
{
	NONE,
	INFORMATION,
	WARNING,
	ERROR;

	@Override
	public String toString()
	{
		return super.toString() + " : IMessageProvider type:" + ordinal();
	}
}
