package org.nightlabs.eclipse.ui.fckeditor;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.eclipse.ui.fckeditor.util.IOUtil;
import org.nightlabs.htmlcontent.IFCKEditorContent;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorContentViewer extends Browser
{
	public FCKEditorContentViewer(Composite parent, int style)
	{
		super(parent, style);
	}

	public void setContent(IFCKEditorContent content)
	{
		try {
			String html = content.getHtml();
			// rewrite links

			// save needed files

			File htmlTmp = File.createTempFile("jfire-html", ".html"); //$NON-NLS-1$ //$NON-NLS-2$
			IOUtil.writeTextFile(htmlTmp, html);

			setUrl(htmlTmp.toURI().toString());
		} catch(IOException e) {
			throw new RuntimeException("Error creating html tmp files", e); //$NON-NLS-1$
		}
	}
}
