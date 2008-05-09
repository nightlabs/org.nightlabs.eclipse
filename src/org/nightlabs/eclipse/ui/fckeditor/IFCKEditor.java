package org.nightlabs.eclipse.ui.fckeditor;

import org.eclipse.ui.IEditorPart;
import org.nightlabs.eclipse.ui.fckeditor.file.IImageProvider;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface IFCKEditor extends IEditorPart {
	IFCKEditorInput getEditorInput();
	String getBaseUrl();
	String getWidgetBackgroundColor();
	String getTitleBackgroundColor();
	String getTitleBackgroundGradientColor();
	void setDirty(boolean dirty);
	String getFCKEditorId();
	
	void print();
//	void copy();
//	void paste();
	
	void setEnabled(boolean enabled);
	
	IImageProvider getImageProvider();
}
