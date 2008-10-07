package org.nightlabs.eclipse.ui.pdfviewer.extension.action.save;

import org.eclipse.jface.dialogs.MessageDialog;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerAction;

public class SaveAsAction
extends PdfViewerAction
{
	@Override
	public void run() {
		MessageDialog.openInformation(RCPUtil.getActiveShell(), "Save as", "This should be a dialog for saving the PDF to a local file.");
	}

}
