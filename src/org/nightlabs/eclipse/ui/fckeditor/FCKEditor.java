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

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.nightlabs.eclipse.ui.fckeditor.file.IImageProvider;
import org.nightlabs.eclipse.ui.fckeditor.file.ImageProvider;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorCSSProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorConfigFileProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorEditDocumentProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorFileProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorHTTPD;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorSaveDocumentProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorSkinFileProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.FCKPluginFileProvider;
import org.nightlabs.eclipse.ui.fckeditor.server.UIBridge;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public class FCKEditor extends EventManager implements IFCKEditor
{
	private Shell shell;
	private IFCKEditorInput input;

	private boolean dirty;
	private Browser browser;
	private FCKEditorHTTPD httpd;
	private String widgetBackgroundColor;
	private String widgetSelectedColor;
	private String widgetHoverColor;
	private IImageProvider imageProvider;

	/**
	 * Create a new FCKEditor instance.
	 */
	public FCKEditor()
	{
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
//	 */
//	@Override
//	public IFCKEditorInput getEditorInput() {
//		return (IFCKEditorInput)super.getEditorInput();
//	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#commit()
	 */
	@Override
	public void commit()
	{
		browser.execute("var myform = document.getElementById('form_"+getFCKEditorId()+"'); myform.submit();"); //$NON-NLS-1$ //$NON-NLS-2$
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
//	 */
//	@Override
//	public void doSave(IProgressMonitor monitor) {
//		browser.execute("var myform = document.getElementById('form_"+getFCKEditorId()+"'); myform.submit();"); //$NON-NLS-1$ //$NON-NLS-2$
//	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
//	 */
//	@Override
//	public void doSaveAs() {
//		// not supported yet
//	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#doReallySave()
	 */
	@Override
	public void doReallySave()
	{
		// sub classes should implement this method
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
		return getHtmlColor(getShell().getDisplay().getSystemColor(swtColorId));
	}

	/**
	 * Get the HTML color representation string for an SWT system
	 * color.
	 * @param color The SWT system color.
	 * @return The HTML color representation in the form
	 * 		<code>"#xxxxxx"</code> where every 'x' represents a hex
	 * 		digit.
	 */
	protected String getHtmlColor(Color color)
	{
		if(color == null)
			throw new NullPointerException("color"); //$NON-NLS-1$
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()); //$NON-NLS-1$
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
//	 */
//	@Override
//	public void init(IEditorSite site, IEditorInput input) throws PartInitException
//	{
//		if(!(input instanceof IFCKEditorInput))
//			throw new PartInitException("Invalid FCKeditor input"); //$NON-NLS-1$
//
//		try {
//			httpd = FCKEditorHTTPD.sharedInstance();
//			httpd.addEditor(this);
//			httpd.addFileProvider(this, new FCKEditorFileProvider(this));
//			httpd.addFileProvider(this, new FCKEditorSkinFileProvider(this));
//			httpd.addFileProvider(this, new FCKEditorEditDocumentProvider(this));
//			httpd.addFileProvider(this, new FCKEditorSaveDocumentProvider(this));
//			httpd.addFileProvider(this, new FCKEditorConfigFileProvider(this));
//			httpd.addFileProvider(this, new FCKEditorCSSProvider(this));
//			httpd.addFileProvider(this, new FCKPluginFileProvider(this));
//			httpd.addFileProvider(this, new UIBridge(this));
//			//System.out.println("Editor URL: "+getBaseUrl());
//		} catch(Throwable e) {
//			throw new PartInitException(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.FCKEditor.httpdError"), e); //$NON-NLS-1$
//		}
//
//		setSite(site);
//		setInput(input);
//
//		setPartName(input.getName());
//
//		imageProvider = new ImageProvider(getSite().getShell().getDisplay());
//
//		FormColors formColors = new FormColors(getSite().getShell().getDisplay());
//		widgetBackgroundColor = getHtmlColor(SWT.COLOR_WIDGET_BACKGROUND);
////		titleBackgroundColor = getHtmlColor(SWT.COLOR_TITLE_BACKGROUND);
//		widgetSelectedColor = getHtmlColor(formColors.getColor(IFormColors.TB_BORDER));
////		titleBackgroundGradientColor = getHtmlColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
//		widgetHoverColor = getHtmlColor(formColors.getColor(IFormColors.TB_BG));
//	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.ui.part.EditorPart#isDirty()
//	 */
//	@Override
//	public boolean isDirty() {
//		return dirty;
//	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#setDirty(boolean)
	 */
	@Override
	public void setDirty(boolean dirty) {
		if(this.dirty != dirty) {
			this.dirty = dirty;
			getShell().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run()
				{
					firePropertyChange(PROP_DIRTY);
				}
			});
		}
		//firePartPropertyChanged(PROP_DIRTY, String.valueOf(!dirty), String.valueOf(dirty));
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
//	 */
//	@Override
//	public boolean isSaveAsAllowed() {
//		return false;
//	}

	public void init(Shell shell, IFCKEditorInput input)
	{
		this.shell = shell;
		this.input = input;

		try {
			httpd = FCKEditorHTTPD.sharedInstance();
			httpd.addEditor(this);
			httpd.addFileProvider(this, new FCKEditorFileProvider(this));
			httpd.addFileProvider(this, new FCKEditorSkinFileProvider(this));
			httpd.addFileProvider(this, new FCKEditorEditDocumentProvider(this));
			httpd.addFileProvider(this, new FCKEditorSaveDocumentProvider(this));
			httpd.addFileProvider(this, new FCKEditorConfigFileProvider(this));
			httpd.addFileProvider(this, new FCKEditorCSSProvider(this));
			httpd.addFileProvider(this, new FCKPluginFileProvider(this));
			httpd.addFileProvider(this, new UIBridge(this));
			//System.out.println("Editor URL: "+getBaseUrl());
		} catch(Throwable e) {
			// TODO: better an error dialog?
			throw new RuntimeException(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.FCKEditor.httpdError"), e); //$NON-NLS-1$
		}

		imageProvider = new ImageProvider(getShell().getDisplay());

		FormColors formColors = new FormColors(getShell().getDisplay());
		widgetBackgroundColor = getHtmlColor(SWT.COLOR_WIDGET_BACKGROUND);
//		titleBackgroundColor = getHtmlColor(SWT.COLOR_TITLE_BACKGROUND);
		widgetSelectedColor = getHtmlColor(formColors.getColor(IFormColors.TB_BORDER));
//		titleBackgroundGradientColor = getHtmlColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
		widgetHoverColor = getHtmlColor(formColors.getColor(IFormColors.TB_BG));
	}

	public void createControl(Composite parent)
	{
		browser = new Browser(parent, SWT.NONE);
		browser.setUrl(httpd.getUrl(this)+"/edit.html"); //$NON-NLS-1$
		browser.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0)
			{
				dispose();
			}
		});
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void dispose()
	{
		if(httpd != null) {
			httpd.removeEditor(FCKEditor.this);
			httpd = null;
		}
		if(imageProvider != null) {
			imageProvider.dispose();
			imageProvider = null;
		}
		clearListeners();
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
//	 */
//	@Override
//	public void setFocus() {
//	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getWidgetBackgroundColor()
	 */
	@Override
	public String getWidgetBackgroundColor()
	{
		return widgetBackgroundColor;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getWidgetSelectedColor()
	 */
	@Override
	public String getWidgetSelectedColor()
	{
		return widgetSelectedColor;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getWidgetHoverColor()
	 */
	@Override
	public String getWidgetHoverColor()
	{
		return widgetHoverColor;
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
		return "FCKeditor_"+getBaseUrl(); //$NON-NLS-1$
	}

	private boolean executeFCKCommand(String command)
	{
		return browser.execute("var oEditor = FCKeditorAPI.GetInstance('"+getFCKEditorId()+"'); oEditor.Commands.GetCommand('"+command+"').Execute();"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#print()
	 */
	@Override
	public void print()
	{
		executeFCKCommand("Print"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		browser.setEnabled(enabled);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getImageProvider()
	 */
	@Override
	public IImageProvider getImageProvider()
	{
		return imageProvider;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getShell()
	 */
	@Override
	public Shell getShell()
	{
		return shell;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.IFCKEditor#getEditorInput()
	 */
	@Override
	public IFCKEditorInput getEditorInput()
	{
		return input;
	}

    public void addPropertyListener(IPropertyListener l) {
        addListenerObject(l);
    }

    /**
     * Fires a property changed event.
     *
     * @param propertyId the id of the property that changed
     */
    protected void firePropertyChange(final int propertyId) {
        Object[] array = getListeners();
        for (int nX = 0; nX < array.length; nX++) {
            final IPropertyListener l = (IPropertyListener) array[nX];
            try {
                l.propertyChanged(this, propertyId);
            } catch (RuntimeException e) {
            	Activator.err("Error in property change listener", e);
            }
        }
    }

    public void removePropertyListener(IPropertyListener l) {
        removeListenerObject(l);
    }
}
