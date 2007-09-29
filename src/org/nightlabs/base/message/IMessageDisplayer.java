package org.nightlabs.base.message;

/**
 * 
 * @author Marius Heinzmann -- Marius[at]NightLabs[dot]de
 *
 */
public interface IMessageDisplayer
{
//	public enum MessageType {
//		NONE, INFO, WARNING, ERROR
//	}
	
	/**
	 * sets the message to display
	 * 
	 * @param message the message to display
	 * @param style the style of the message
	 * The valid message styles are one of <code>IMessageProvider.NONE</code>,
	 * <code>IMessageProvider.INFORMATION</code>,<code>IMessageProvider.WARNING</code>, or
	 * <code>IMessageProvider.ERROR</code>.
	 */
	public void setMessage(String message, int type);

}
