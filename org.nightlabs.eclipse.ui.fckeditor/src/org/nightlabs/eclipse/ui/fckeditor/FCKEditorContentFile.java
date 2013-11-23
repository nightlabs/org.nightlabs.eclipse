/* ************************************************************************
 * org.nightlabs.eclipse.ui.fckeditor - Eclipse RCP FCKeditor Integration *
 * Copyright (C) 2008 NightLabs - http://NightLabs.org                    *
 *                                                                        *
 * This library is free software; you can redistribute it and/or          *
 * modify it under the terms of the GNU Lesser General Public             *
 * License as published by the Free Software Foundation; either           *
 * version 2.1 of the License, or (at your option) any later version.     *
 *                                                                        *
 * This library is distributed in the hope that it will be useful,        *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU      *
 * Lesser General Public License for more details.                        *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with this library; if not, write to the                  *
 *     Free Software Foundation, Inc.,                                    *
 *     51 Franklin St, Fifth Floor,                                       *
 *     Boston, MA  02110-1301  USA                                        *
 *                                                                        *
 * Or get it online:                                                      *
 *     http://www.gnu.org/copyleft/lesser.html                            *
 **************************************************************************/
package org.nightlabs.eclipse.ui.fckeditor;

import java.util.Date;

import org.nightlabs.htmlcontent.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public class FCKEditorContentFile implements IFCKEditorContentFile
{
	private static long lastFileId = 0L;
	private long fileId;
	private byte[] data;
	private String contentType;
	private String name;
	private String description;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return super.toString()+"[name="+name+",contentType="+contentType+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description)
	{
		this.description = description;
	}
}