// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.Wizard;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ContentFileWizard extends Wizard implements IContentFileWizard
{
	private File sourceFile;
	private String mimeType;
	private ContentFileBasePage basePage;

	/**
	 * Create a new ContentImageFileWizard instance.
	 */
	public ContentFileWizard()
	{
		basePage = new ContentFileBasePage();
		addPage(basePage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		basePage.performFinish(new NullProgressMonitor());
		return true;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard#getData()
	 */
	@Override
	public byte[] getData() throws IOException
	{
		FileInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new FileInputStream(sourceFile);
			out = new ByteArrayOutputStream((int)sourceFile.length());
			byte[] buf = new byte[4096];
			while(true) {
				int bytesRead = in.read(buf);
				if (bytesRead <= 0)
					break;
				out.write(buf, 0, bytesRead);
			}
		} finally {
			try {
				if(in != null)
					in.close();
			} catch (IOException e) {}
		}
		return out.toByteArray();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return basePage.getUserFileDescription();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard#getMimeType()
	 */
	@Override
	public String getMimeType()
	{
		return mimeType;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard#getName()
	 */
	@Override
	public String getName()
	{
		return basePage.getUserFileName();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard#setSourceFile(java.io.File, java.lang.String)
	 */
	@Override
	public void setSourceFile(File file, String mimeType)
	{
		this.sourceFile = file;
		this.mimeType = mimeType;
		basePage.setSourceFile(file);
	}
}
