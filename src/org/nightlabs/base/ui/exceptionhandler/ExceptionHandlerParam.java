package org.nightlabs.base.ui.exceptionhandler;

import java.awt.image.BufferedImage;


/**
 * @author Fitas Amine <fitas[AT]nightlabs[DOT]de>
 */


public class ExceptionHandlerParam {

	/**
	 * @param thread The thread the exception was thrown on.
	 * @param thrownException The Exception actually thrown.
	 * @param triggerException The Exception that caused the caller
	 *                         to pick this particular handler so this always should be
	 *                         the Exception type the handler was registered on.	
	 *                          
	 *                          */

	private Thread thread = null;
	private Throwable thrownException=null;
	private Throwable triggerException=null;
	private BufferedImage ErrorScreenShot = null;

	public ExceptionHandlerParam(Thread thread, Throwable thrownException, Throwable triggerException)
	{
		this.thread = thread;
		this.thrownException = thrownException;
		this.triggerException = triggerException;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public Throwable getThrownException() {
		return thrownException;
	}

	public void setThrownException(Throwable thrownException) {
		this.thrownException = thrownException;
	}

	public Throwable getTriggerException() {
		return triggerException;
	}

	public void setTriggerException(Throwable triggerException) {
		this.triggerException = triggerException;
	}

	public BufferedImage getErrorScreenShot() {
		return ErrorScreenShot;
	}

	public void setErrorScreenShot(BufferedImage errorScreenShot) {
		ErrorScreenShot = errorScreenShot;
	}

}
