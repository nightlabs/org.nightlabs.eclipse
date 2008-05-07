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
}
