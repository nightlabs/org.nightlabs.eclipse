// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file.image;

public class UsupportedImageException extends Exception
{
	private static final long serialVersionUID = 1L;

	public UsupportedImageException()
	{
		super();
	}

	public UsupportedImageException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public UsupportedImageException(String message)
	{
		super(message);
	}

	public UsupportedImageException(Throwable cause)
	{
		super(cause);
	}
}