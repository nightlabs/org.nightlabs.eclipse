package org.nightlabs.base.ui.exceptionhandler;

import java.awt.image.BufferedImage;


/**
 * Creates an ExceptionHandlerParam which is used by the 
 * implementations of {@link IExceptionHandler}.
 * 
 * @author Fitas Amine <fitas[AT]nightlabs[DOT]de>
 */
public class ExceptionHandlerParam {

	private Thread thread = null;
	private Throwable thrownException=null;
	private Throwable triggerException=null;
	private BufferedImage errorScreenShot = null;

	/**
	 * Creates an ExceptionHandlerParam.
	 * 
	 * @param thread The thread the exception was thrown on. (May be null)
	 * @param thrownException The Exception actually thrown.
	 * @param triggerException The Exception that caused the caller
	 *                         to pick this particular handler so this always should be
	 *                         the Exception type the handler was registered on.                         
	 */	
	public ExceptionHandlerParam(Thread thread, Throwable thrownException, Throwable triggerException)
	{
		this.thread = thread;
		this.thrownException = thrownException;
		this.triggerException = triggerException;
	}

	/**
	 * Creates an ExceptionHandlerParam.
	 * 
	 * @param thrownException The Exception actually thrown.
	 * @param triggerException The Exception that caused the caller
	 *                         to pick this particular handler so this always should be
	 *                         the Exception type the handler was registered on.                         
	 */
	public ExceptionHandlerParam(Throwable thrownException, Throwable triggerException)
	{
		this(null, thrownException, triggerException);
	}

	/**
	 * Returns the thread.
	 * @return the thread
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * Sets the thread.
	 * @param thread the thread to set
	 */
	public void setThread(Thread thread) {
		this.thread = thread;
	}

	/**
	 * Returns the thrownException.
	 * @return the thrownException
	 */
	public Throwable getThrownException() {
		return thrownException;
	}

	/**
	 * Sets the thrownException.
	 * @param thrownException the thrownException to set
	 */
	public void setThrownException(Throwable thrownException) {
		this.thrownException = thrownException;
	}

	/**
	 * Returns the triggerException.
	 * @return the triggerException
	 */
	public Throwable getTriggerException() {
		return triggerException;
	}

	/**
	 * Sets the triggerException.
	 * @param triggerException the triggerException to set
	 */
	public void setTriggerException(Throwable triggerException) {
		this.triggerException = triggerException;
	}

	/**
	 * Returns the errorScreenShot.
	 * @return the errorScreenShot
	 */
	public BufferedImage getErrorScreenShot() {
		return errorScreenShot;
	}

	/**
	 * Sets the errorScreenShot.
	 * @param errorScreenShot the errorScreenShot to set
	 */
	public void setErrorScreenShot(BufferedImage errorScreenShot) {
		this.errorScreenShot = errorScreenShot;
	}
	
}
