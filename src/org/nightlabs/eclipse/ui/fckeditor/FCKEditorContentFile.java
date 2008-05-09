package org.nightlabs.eclipse.ui.fckeditor;

import java.util.Date;

public class FCKEditorContentFile implements IFCKEditorContentFile
{
	private static long lastFileId = 0L;
	private long fileId;
	private byte[] data;
	private String contentType;
	private String name;
	private Date changeDT;

	private static synchronized long getNextFileId()
	{
		return lastFileId++;
	}
	
	/**
	 * Create a new FCKEditorContentFile instance.
	 */
	public FCKEditorContentFile()
	{
		this.changeDT = new Date();
		this.fileId = getNextFileId();
	}
	
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
		this.changeDT = new Date();
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
		this.changeDT = new Date();
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
		this.changeDT = new Date();
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#isImageFile()
	 */
	@Override
	public boolean isImageFile()
	{
		String contentType = getContentType();
		if(contentType == null)
			return false;
		return "image/jpeg".equals(contentType) || "image/png".equals(contentType) || "image/gif".equals(contentType);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#getChangeDT()
	 */
	@Override
	public Date getChangeDT()
	{
		return changeDT;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#getFileId()
	 */
	@Override
	public long getFileId()
	{
		return fileId;
	}
}