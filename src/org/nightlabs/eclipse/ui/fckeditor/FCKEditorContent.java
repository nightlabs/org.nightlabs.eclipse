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

import java.util.LinkedList;
import java.util.List;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorContent implements IFCKEditorContent
{
	private String html;
	private List<IFCKEditorContentFile> files = new LinkedList<IFCKEditorContentFile>();

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContent#getHtml()
	 */
	@Override
	public String getHtml() {
		return html;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContent#setHtml(java.lang.String)
	 */
	@Override
	public void setHtml(String html) {
		this.html = html;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContent#getFiles()
	 */
	@Override
	public List<IFCKEditorContentFile> getFiles()
	{
		return files;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContent#addFile(org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile)
	 */
	@Override
	public void addFile(IFCKEditorContentFile file)
	{
		files.add(file);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContent#setFiles(java.util.List)
	 */
	@Override
	public void setFiles(List<IFCKEditorContentFile> files)
	{
		this.files = files;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContent#getFile(long)
	 */
	@Override
	public IFCKEditorContentFile getFile(long fileId)
	{
		for (IFCKEditorContentFile file : files)
			if(file.getFileId() == fileId)
				return file;
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContent#getFileFactory()
	 */
	@Override
	public IFCKEditorContentFileFactory getFileFactory()
	{
		return new IFCKEditorContentFileFactory() {
			@Override
			public IFCKEditorContentFile createContentFile()
			{
				return new FCKEditorContentFile();
			}
		};
	}
}
