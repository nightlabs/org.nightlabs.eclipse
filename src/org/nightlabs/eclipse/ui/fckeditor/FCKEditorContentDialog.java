package org.nightlabs.eclipse.ui.fckeditor;

import java.io.IOException;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.htmlcontent.IFCKEditorContent;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorContentDialog extends ResizableTitleAreaDialog
{
	private String url;
	private Browser browser;

	public FCKEditorContentDialog(Shell parentShell, ResourceBundle bundle)
	{
		super(parentShell, bundle);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		composite.setLayout(gl);
		Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browser = new Browser(composite, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if(url != null)
			browser.setUrl(url);
		Label separator2 = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL,
				true);
	}

	public void setContent(IFCKEditorContent content)
	{
		try {
			this.url = ContentViewerUtil.createStaticContent(content);
		} catch (IOException e) {
			throw new RuntimeException("Creating static browser content failed", e); //$NON-NLS-1$
		}
		if(browser != null)
			browser.setUrl(url);
	}
}
