package org.nightlabs.eclipse.ui.pdfviewer.internal;

/**
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 * @author frederik loeser - frederik at nightlabs dot de
 */
public class WaitForRenderingException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public WaitForRenderingException() {
	}

	public WaitForRenderingException(String message) {
		super(message);
	}

	public WaitForRenderingException(Throwable cause) {
		super(cause);
	}

	public WaitForRenderingException(String message, Throwable cause) {
		super(message, cause);
	}
}
