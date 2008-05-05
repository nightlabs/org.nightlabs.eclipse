package org.nightlabs.eclipse.ui.fckeditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorContent implements IFCKEditorContent 
{
	private String html;
	
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
}
