package org.nightlabs.eclipse.ui.pdfviewer.extension.action.zoom;

import org.eclipse.jface.dialogs.MessageDialog;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerAction;

public class ZoomAction
extends PdfViewerAction
{
	@Override
	public void run() {
		MessageDialog.openInformation(RCPUtil.getActiveShell(), "Zoom", "This should be a dialog giving zoom options.");
	}

	@Override
	public boolean isEnabled() {
//		return true;
		return getPdfViewerActionRegistry().getPdfViewer().getPdfDocument() != null;
	}
}
