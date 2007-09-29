/**
 * 
 */
package org.nightlabs.base.composite;


/**
 * Interface for Containers (e.g. Dialogs or Composites) which
 * are capable of displaying messages
 *  
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 * 
 */
public interface IMessageContainer
//extends IMessageProvider
{
	/**
	 * Sets the message for this IMessageContainer with an indication of what type of
	 * message it is.
	 * <p>
	 * The valid message types are one of <code>IMessageProvider.NONE</code>,
	 * <code>IMessageProvider.INFORMATION</code>,<code>IMessageProvider.WARNING</code>, or
	 * <code>IMessageProvider.ERROR</code>.
	 * </p>
	 * 
	 * @param newMessage
	 *            the message, or <code>null</code> to clear the message
	 * @param newType
	 *            the message type
	 */	
	void setMessage(String newMessage, int newType);
}
