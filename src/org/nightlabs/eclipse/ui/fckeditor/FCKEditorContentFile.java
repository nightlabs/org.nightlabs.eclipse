package org.nightlabs.eclipse.ui.fckeditor;

public class FCKEditorContentFile implements IFCKEditorContentFile
{
	private byte[] data;
	private String contentType;
	private String name;

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#getData()
	 */
	@Override
	public byte[] getData()
	{
		return data;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#setData(byte[])
	 */
	@Override
	public void setData(byte[] data)
	{
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#getContentType()
	 */
	@Override
	public String getContentType()
	{
		return contentType;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#setName(java.lang.String)
	 */
	@Override
	public void setName(String name)
	{
		this.name = name;
	}
}