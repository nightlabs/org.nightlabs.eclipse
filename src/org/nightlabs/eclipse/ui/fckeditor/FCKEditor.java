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
	private String widgetBackgroundColor;
	private String titleBackgroundColor;
	private String titleBackgroundGradientColor;
	
	/**
	 * Create a new FCKEditor instance.
	 */
	public FCKEditor() 
	{
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

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		httpd.removeEditor(this);
		httpd = null;
		super.dispose();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public IFCKEditorInput getEditorInput() {
		return (IFCKEditorInput)super.getEditorInput();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		browser.execute("var myform = document.getElementById('form_"+getFCKEditorId()+"'); myform.submit();");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// not supported yet
	}

	/**
	 * Get the HTML color representation string for an SWT system 
	 * color id.
	 * @param swtColorId The SWT system color.
	 * @return The HTML color representation in the form 
	 * 		<code>"#xxxxxx"</code> where every 'x' represents a hex 
	 * 		digit.
	 */
	protected String getHtmlColor(int swtColorId)
	{
		Color color = getSite().getShell().getDisplay().getSystemColor(swtColorId);
		if(color == null)
			throw new IllegalArgumentException("Unknown SWT color: "+swtColorId);
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
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
		
		setPartName(input.getName());
		
		widgetBackgroundColor = getHtmlColor(SWT.COLOR_WIDGET_BACKGROUND);
		titleBackgroundColor = getHtmlColor(SWT.COLOR_TITLE_BACKGROUND);
		titleBackgroundGradientColor = getHtmlColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#setDirty(boolean)
	 */
	@Override
	public void setDirty(boolean dirty) {
		if(this.dirty != dirty) {
			this.dirty = dirty;
			getSite().getShell().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run()
				{
					firePropertyChange(PROP_DIRTY);
				}
			});
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
	
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getWidgetBackgroundColor()
	 */
	@Override
	public String getWidgetBackgroundColor()
	{
		return widgetBackgroundColor;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getBaseUrl()
	 */
	@Override
	public String getBaseUrl() {
		return httpd.getUrl(this);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getFCKEditorId()
	 */
	@Override
	public String getFCKEditorId()
	{
		return "FCKeditor_"+getBaseUrl();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getTitleBackgroundColor()
	 */
	@Override
	public String getTitleBackgroundColor()
	{
		return titleBackgroundColor;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getTitleBackgroundGradientColor()
	 */
	@Override
	public String getTitleBackgroundGradientColor()
	{
		return titleBackgroundGradientColor;
	}

	private boolean executeFCKCommand(String command)
	{
		return browser.execute("var oEditor = FCKeditorAPI.GetInstance('"+getFCKEditorId()+"'); oEditor.Commands.GetCommand('"+command+"').Execute();");
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#print()
	 */
	@Override
	public void print()
	{
		executeFCKCommand("Print");
	}
}
