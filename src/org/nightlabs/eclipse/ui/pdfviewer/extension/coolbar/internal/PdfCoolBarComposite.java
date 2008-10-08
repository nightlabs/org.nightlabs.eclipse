package org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar.internal;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerActionRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar.PdfCoolBar;

public class PdfCoolBarComposite extends Composite
{
	private PdfCoolBar pdfCoolBar;
	private PdfViewerActionRegistry pdfViewerActionRegistry;
	private CoolBar coolBar;
	private CoolBarManager coolBarManager;

	public PdfCoolBarComposite(Composite parent, int style, PdfCoolBar pdfCoolBar) {
		super(parent, style);
		this.setLayout(new FillLayout());

		this.pdfCoolBar = pdfCoolBar;
		this.pdfViewerActionRegistry = this.pdfCoolBar.getPdfViewerActionRegistry();

		coolBar = new CoolBar(this, SWT.NONE);
		coolBarManager = new CoolBarManager(coolBar);
		pdfViewerActionRegistry.contributeToCoolBar(coolBarManager);
		coolBar.setVisible(true);
		coolBarManager.update(true);
	}

//	public void refresh() {
//		pdfViewerActionRegistry.calculateEnabled();
////		coolBarManager.update(true);
////		coolBar.update();
//	}
}
