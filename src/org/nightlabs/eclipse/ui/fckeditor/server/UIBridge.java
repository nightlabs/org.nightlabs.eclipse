package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class UIBridge extends AbstractFileProvider {

	public UIBridge(IFCKEditor editor) {
		super(editor);
	}

	private static InputStream getStreamForString(String contents)
	{
		try {
			return new ByteArrayInputStream(contents.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// should never happen
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getFileContents(java.lang.String, java.util.Properties)
	 */
	@Override
	public InputStream getFileContents(String subUri, Properties parms) {
		if("/uibridge/setdirty.xml".equals(subUri)) {
			getEditor().setDirty(true);
			String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<dirty>true</dirty>\n";
			return getStreamForString(contents);
		}
		else if("/uibridge/insertimage.xml".equals(subUri)) {
			System.out.println("IMAGE1");
			final Shell shell = getEditor().getSite().getShell();
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run()
				{
					System.out.println("IMAGE2");
					MessageDialog.openInformation(shell, "Yeah!", "Select an image within RCP...");
					System.out.println("IMAGE3");
				}
			});
			System.out.println("IMAGE4");
			String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<image>true</image>\n";
			return getStreamForString(contents);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath() {
		return "/uibridge/";
	}

}
