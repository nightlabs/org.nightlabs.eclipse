package org.nightlabs.eclipse.ui.fckeditor;

import java.util.List;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface IFCKEditorContent {
	String getHtml();
	void setHtml(String html);
	List<IFCKEditorContentFile> getFiles();
	void addFile(IFCKEditorContentFile file);
}
