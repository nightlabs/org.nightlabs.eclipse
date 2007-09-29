/**
 * 
 */
package org.nightlabs.base.print.action;

import java.awt.print.PrinterException;
import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.nightlabs.base.print.PrinterInterfaceManager;
import org.nightlabs.base.print.PrinterUseCase;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.print.DocumentPrinter;
import org.nightlabs.print.PrinterInterface;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @deprecated should be deleted by alex 
 */
public class PrintDocumentAction implements IWorkbenchWindowActionDelegate {

	/**
	 * 
	 */
	public PrintDocumentAction() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow arg0) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction arg0) {
		FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell());
		String fileName = fileDialog.open();
		if (fileName != null) {
			try {
				PrinterInterface printer = PrinterInterfaceManager.sharedInstance().getConfiguredPrinterInterface(
						PrinterInterfaceManager.INTERFACE_FACTORY_DOCUMENT,
						PrinterUseCase.DEFAULT_USE_CASE_ID
					);
				if (printer instanceof DocumentPrinter) {
					((DocumentPrinter)printer).printDocument(new File(fileName));
				}
			} catch (PrinterException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction arg0, ISelection arg1) {
	}

}
