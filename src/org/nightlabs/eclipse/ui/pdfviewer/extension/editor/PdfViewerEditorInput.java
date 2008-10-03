package org.nightlabs.eclipse.ui.pdfviewer.extension.editor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader;
import org.nightlabs.util.Util;

import com.sun.pdfview.PDFFile;

/**
 * An editor input that can be used to open a {@link PdfViewerEditor}.
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerEditorInput
implements IEditorInput
{
	private String name;
	private String toolTipText;
	private ImageDescriptor imageDescriptor;
	private File file;
	private byte[] byteArray;
	private volatile PDFFile pdfFile;


	public PdfViewerEditorInput(File file)
	{
		this(file.getName(), file.getAbsolutePath(), null, file);
	}

	public PdfViewerEditorInput(String name, String toolTipText, ImageDescriptor imageDescriptor, File file)
	{
		if (file == null)
			throw new IllegalArgumentException("file must not be null!"); //$NON-NLS-1$

		this.name = name;
		this.toolTipText = toolTipText;
		this.imageDescriptor = imageDescriptor;
		this.file = file;
	}

	public PdfViewerEditorInput(String name, String toolTipText, ImageDescriptor imageDescriptor, PDFFile pdfFile)
	{
		if (pdfFile == null)
			throw new IllegalArgumentException("pdfFile must not be null!"); //$NON-NLS-1$

		this.name = name;
		this.toolTipText = toolTipText;
		this.imageDescriptor = imageDescriptor;
		this.pdfFile = pdfFile;
	}

	public PdfViewerEditorInput(String name, String toolTipText, ImageDescriptor imageDescriptor, byte[] byteArray) {
		if (byteArray == null)
			throw new IllegalArgumentException("byteArray must not be null!"); //$NON-NLS-1$

		this.name = name;
		this.toolTipText = toolTipText;
		this.imageDescriptor = imageDescriptor;
		this.byteArray = byteArray;
	}

	/**
	 * Get the {@link PDFFile} or <code>null</code>, if none has been created before (i.e. {@link #createPDFFile(IProgressMonitor)} was
	 * not yet called and a <code>PDFFile</code> instance was not passed as constructor argument either).
	 *
	 * @return the <code>PDFFile</code> which has been created by a previous call to {@link #createPDFFile(IProgressMonitor)} or <code>null</code>.
	 */
	public PDFFile getPDFFile()
	{
		return pdfFile;
	}

	/**
	 * Create a new <code>PDFFile</code> instance. Calling this method a second time has no effect. You should therefore
	 * use {@link #getPDFFile()} instead, after it was created once.
	 *
	 * @param monitor the monitor for progress feedback.
	 * @return a new instance of <code>PDFFile</code>. This
	 * @throws IOException
	 */
	public PDFFile createPDFFile(IProgressMonitor monitor)
	throws IOException
	{
		if (pdfFile != null) {
			monitor.beginTask("PDF already loaded", 1);
			monitor.worked(1);
			monitor.done();
			return pdfFile;
		}

		if (byteArray != null)
			pdfFile = PdfFileLoader.loadPdf(byteArray, monitor);
		else if (file != null)
			pdfFile = PdfFileLoader.loadPdf(file, monitor);
		else
			throw new IllegalStateException("Have no data!"); //$NON-NLS-1$

		return pdfFile;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IPersistableElement getPersistable() {
		// not restorable => return null
		return null;
	}

	@Override
	public String getToolTipText() {
		return toolTipText;
	}

	@SuppressWarnings("unchecked") // seems, IAdaptable does not have a class type set (not even <?>). //$NON-NLS-1$
	@Override
	public Object getAdapter(Class arg0) {
		// no adapter supported, at the moment
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj.getClass() != this.getClass()) return false;
		PdfViewerEditorInput o = (PdfViewerEditorInput) obj;
		return Util.equals(file, o.file) && Util.equals(byteArray, o.byteArray);
	}

	@Override
	public int hashCode() {
		return Util.hashCode(file) + (byteArray == null ? 0 : Arrays.hashCode(byteArray));
	}
}
