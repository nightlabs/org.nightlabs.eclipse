package org.nightlabs.eclipse.ui.fckeditor;

public class I18NException extends Exception
{
	private static final long serialVersionUID = 1L;

	private String localizedMessage;

	/**
	 * Constructs a new exception with <code>null</code> as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public I18NException()
	{
	}

	/**
	 * Constructs a new exception with the specified detail message.  The
	 * cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.
	 * @param   message the detail message. The detail message is saved for
	 *          later retrieval by the {@link #getMessage()} method.
	 * @param   localizedMessage the detail message in the local language.
	 *          The detail message is saved for later retrieval by the
	 *          {@link #getLocalizedMessage()} method.
	 */
	public I18NException(String message, String localizedMessage)
	{
		super(message);
		this.localizedMessage = localizedMessage;
	}

	/**
	 * Constructs a new exception with the specified cause and a detail
	 * message of <tt>(cause==null ? null : cause.toString())</tt> (which
	 * typically contains the class and detail message of <tt>cause</tt>).
	 * This constructor is useful for exceptions that are little more than
	 * wrappers for other throwables (for example, {@link
	 * java.security.PrivilegedActionException}).
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A <tt>null</tt> value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 */
	public I18NException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Constructs a new exception with the specified detail message and
	 * cause.  <p>Note that the detail message associated with
	 * <code>cause</code> is <i>not</i> automatically incorporated in
	 * this exception's detail message.
	 * @param  message the detail message (which is saved for later retrieval
	 *         by the {@link #getMessage()} method).
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A <tt>null</tt> value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 */
	public I18NException(String message, String localizedMessage, Throwable cause)
	{
		super(message, cause);
		this.localizedMessage = localizedMessage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	@Override
	public String getLocalizedMessage()
	{
		return localizedMessage != null ? localizedMessage : super.getLocalizedMessage();
	}
}
