package org.nightlabs.eclipse.ui.fckeditor.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorContentFile;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;
import org.nightlabs.eclipse.ui.fckeditor.file.FileList;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FileListAction implements IWorkbenchWindowActionDelegate
{
	IWorkbenchWindow window;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	@Override
	public void dispose()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}


	/**
	 * Transfer data between streams.
	 * @param in The input stream
	 * @param out The output stream
	 * @param inputOffset How many bytes to skip before transferring
	 * @param inputLen How many bytes to transfer. -1 = all
	 * @return The number of bytes transferred
	 * @throws IOException if an error occurs.
	 */
	public static long transferStreamData(java.io.InputStream in, java.io.OutputStream out, long inputOffset, long inputLen)
	throws java.io.IOException
	{
		int bytesRead;
		int transferred = 0;
		byte[] buf = new byte[4096];

		//skip offset
		if(inputOffset > 0)
			if(in.skip(inputOffset) != inputOffset)
				throw new IOException("Input skip failed (offset "+inputOffset+")");

		while (true) {
			if(inputLen >= 0)
				bytesRead = in.read(buf, 0, (int)Math.min(buf.length, inputLen-transferred));
			else
				bytesRead = in.read(buf);

			if (bytesRead <= 0)
				break;

			out.write(buf, 0, bytesRead);

			transferred += bytesRead;

			if(inputLen >= 0 && transferred >= inputLen)
				break;
		}
		out.flush();
		return transferred;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action)
	{
		try {
			final List<IFCKEditorContentFile> files = new ArrayList<IFCKEditorContentFile>();

			IFCKEditorContentFile file = new FCKEditorContentFile();
			file.setContentType("image/jpeg");
			file.setName("My Image");
			byte[] contents = getFileContents("/icons/test/vladstudio_skiing_1600x1200.jpg");
			file.setData(contents);
			files.add(file);

			file = new FCKEditorContentFile();
			file.setContentType("application/pdf");
			file.setName("superpdfblabla.pdf");
			contents = getFileContents("/icons/test/whitepaper-pdfprimer.pdf");
			file.setData(contents);
			files.add(file);

			file = new FCKEditorContentFile();
			file.setContentType("application/unknown");
			file.setName("Irgendwas");
			contents = "Bla bla bla".getBytes();
			file.setData(contents);
			files.add(file);

			file = new FCKEditorContentFile();
			file.setContentType("text/html");
			file.setName("Irgendwas");
			contents = "<html><body><h1>Hallo!</h1></body></html>".getBytes();
			file.setData(contents);
			files.add(file);

			Dialog dialog = new Dialog(window.getShell()) {
//				@Override
//				protected void configureShell(Shell newShell)
//				{
//					setShellStyle(getShellStyle() | SWT.RESIZE);
//					super.configureShell(newShell);
//				}
				@Override
				protected Control createDialogArea(Composite parent)
				{
					Composite c = (Composite)super.createDialogArea(parent);
					FileList fileList = new FileList(c, SWT.BORDER, files);
					GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
					fileList.setLayoutData(gridData);
					return c;
				}
			};
			dialog.open();
		} catch(Exception e) {
			e.printStackTrace();
			MessageDialog.openError(window.getShell(), "Error", e.toString());
		}
	}

	private byte[] getFileContents(String bundleFilePath) throws IOException
	{
		byte[] contents;
		URL resource = Activator.getDefault().getBundle().getResource(bundleFilePath);
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = resource.openStream();
			out = new ByteArrayOutputStream();
			transferStreamData(in, out, 0, -1);
			contents = out.toByteArray();
		} finally {
			if(in != null)
				in.close();
			if(out != null)
				out.close();
		}
		return contents;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
	}
}

