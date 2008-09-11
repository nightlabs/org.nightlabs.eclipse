package org.nightlabs.eclipse.ui.pdfviewer.editor;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.PdfFileLoader;

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

	private PDFFile pdfFile;

	public PdfViewerEditorInput(File file)
	{
		this(file.getName(), file.getAbsolutePath(), null, file);
	}

	public PdfViewerEditorInput(String name, String toolTipText, ImageDescriptor imageDescriptor, File file)
	{
		if (file == null)
			throw new IllegalArgumentException("file must not be null!");

		this.name = name;
		this.toolTipText = toolTipText;
		this.imageDescriptor = imageDescriptor;
		this.file = file;
	}

	public PdfViewerEditorInput(String name, String toolTipText, ImageDescriptor imageDescriptor, byte[] byteArray) {
		if (byteArray == null)
			throw new IllegalArgumentException("byteArray must not be null!");

		this.name = name;
		this.toolTipText = toolTipText;
		this.imageDescriptor = imageDescriptor;
		this.byteArray = byteArray;
	}

	public PDFFile getPDFFile()
	throws IOException
	{
		if (pdfFile == null)
			pdfFile = createPDFFile();

		return pdfFile;
	}

	private PDFFile createPDFFile()
	throws IOException
	{
		if (byteArray != null)
			return PdfFileLoader.loadPdf(byteArray);

		if (file != null)
			return PdfFileLoader.loadPdf(file);

		throw new IllegalStateException("Have no data!");
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

	@Override
	public Object getAdapter(Class arg0) {
		// no adapter supported, at the moment
		return null;
	}

}
