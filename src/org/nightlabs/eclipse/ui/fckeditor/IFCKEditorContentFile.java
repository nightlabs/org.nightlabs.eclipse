package org.nightlabs.eclipse.ui.fckeditor;

import java.util.Date;

public interface IFCKEditorContentFile
{
	long getFileId();

	byte[] getData();

	void setData(byte[] data);

	String getContentType();

	void setContentType(String contentType);

	String getName();

	void setName(String name);

	boolean isImageFile();

	Date getChangeDT();
}