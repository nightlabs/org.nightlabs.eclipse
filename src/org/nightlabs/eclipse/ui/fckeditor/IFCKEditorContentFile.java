package org.nightlabs.eclipse.ui.fckeditor;

public interface IFCKEditorContentFile
{

	public abstract byte[] getData();

	public abstract void setData(byte[] data);

	public abstract String getContentType();

	public abstract void setContentType(String contentType);

	public abstract String getName();

	public abstract void setName(String name);

}