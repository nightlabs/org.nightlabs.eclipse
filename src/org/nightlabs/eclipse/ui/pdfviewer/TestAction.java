package org.nightlabs.eclipse.ui.pdfviewer;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.io.FileEditorInput;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.pdfviewer.editor.PdfViewerEditor;

import com.sun.pdfview.PDFViewer;


public class TestAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void dispose() {
		// TODO Auto-generated method stubd
	}

	private IWorkbenchWindow window;

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(IAction action) {	
		
		FileDialog dialog = new FileDialog(window.getShell());
		String filePath = dialog.open();
		
		if (filePath == null)
			return;

		File file = new File(filePath);
		FileEditorInput fileEditorInput = new FileEditorInput(file);
		
		try {
			System.out.println("opening editor...");
			RCPUtil.openEditor(fileEditorInput, PdfViewerEditor.ID);
		}
		catch (PartInitException partInitException) {
			throw new RuntimeException(partInitException);
		}
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		new PDFViewer(true);
	}

}
