// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file.image;


public class UnsupportedImageException extends Exception
{
	private static final long serialVersionUID = 1L;

	public UnsupportedImageException()
	{
		super();
	}

	public UnsupportedImageException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public UnsupportedImageException(String message)
	{
		super(message);
	}

	public UnsupportedImageException(Throwable cause)
	{
		super(cause);
	}
}