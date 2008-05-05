package org.nightlabs.eclipse.ui.fckeditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorCSSProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorConfigFileProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorEditDocumentProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorFileProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorHTTPD;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorSaveDocumentProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorSkinFileProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.MarkDirtyProvider;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditor extends EditorPart implements IFCKEditor {

	private boolean dirty;
	private Browser browser;
	private FCKEditorHTTPD httpd;
	
	public FCKEditor() {
		FCKEditorHTTPD httpd = FCKEditorHTTPD.sharedInstance();
		httpd.addEditor(this);
		httpd.addFileProvider(this, new FCKEditorFileProvider(this));
		httpd.addFileProvider(this, new FCKEditorSkinFileProvider(this));
		httpd.addFileProvider(this, new FCKEditorEditDocumentProvider(this));
		httpd.addFileProvider(this, new FCKEditorSaveDocumentProvider(this));
		httpd.addFileProvider(this, new FCKEditorConfigFileProvider(this));
		httpd.addFileProvider(this, new FCKEditorCSSProvider(this));
		httpd.addFileProvider(this, new MarkDirtyProvider(this));
		this.httpd = httpd;
		System.out.println("Editor URL: "+getBaseUrl());
	}
	
	@Override
	public void dispose() {
		httpd.removeEditor(this);
		httpd = null;
		super.dispose();
	}
	
	@Override
	public IFCKEditorInput getEditorInput() {
		return (IFCKEditorInput)super.getEditorInput();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) {
		if(!(input instanceof IFCKEditorInput))
			throw new IllegalArgumentException("Invalid FCKeditor input");
		
		setSite(site);
		setInput(input);
		
		Color color = site.getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		widgetBackgroundColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
		System.out.println("Color: "+widgetBackgroundColor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public void markDirty(boolean dirty) {
		if(this.dirty != dirty) {
			this.dirty = dirty;
			System.out.println("FIRE");
			firePropertyChange(PROP_DIRTY);
			firePropertyChange(PROP_INPUT);
			firePropertyChange(PROP_TITLE);
		}
		//firePartPropertyChanged(PROP_DIRTY, String.valueOf(!dirty), String.valueOf(dirty));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		FillLayout layout = new FillLayout();
		parent.setLayout(layout);
		browser = new Browser(parent, SWT.NONE);
		browser.setUrl(httpd.getUrl(this)+"/edit.html");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}
	
	private String widgetBackgroundColor;
	
	public String getWidgetBackgroundColor()
	{
		return widgetBackgroundColor;
	}

	@Override
	public String getBaseUrl() {
		return httpd.getUrl(this);
	}

}
