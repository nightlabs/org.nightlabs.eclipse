/* ********************************************************************
 * NightLabs PDF Viewer - http://www.nightlabs.org/projects/pdfviewer *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.eclipse.ui.pdfviewer.extension.editor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader;
import org.nightlabs.util.IOUtil;
import org.nightlabs.util.Util;

import com.sun.pdfview.PDFFile;

/**
 * An editor input that can be used to open a {@link PdfViewerEditor}.
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerEditorInput
implements IEditorInput
{
	private String name;
	private String toolTipText;
	private ImageDescriptor imageDescriptor;
	private URL url;
	private File file;
	private byte[] byteArray;
//	private volatile PDFFile pdfFile;

	/**
	 * Create an instance of this editor-input with a file containing the PDF's data.
	 * <p>
	 * This constructor delegates to {@link #PdfViewerEditorInput(String, String, ImageDescriptor, File)}
	 * and passes the file's simple name (without directory) as <code>name</code> and the file's
	 * complete path and name as <code>toolTipText</code>.
	 * </p>
	 *
	 * @param file the file containing the PDF's data.
	 */
	public PdfViewerEditorInput(File file)
	{
		this(file.getName(), file.getAbsolutePath(), null, file);
	}

	/**
	 * Create an instance of this editor-input with a file containing the PDF's data.
	 *
	 * @param name the name of the editor (displayed in the editor's tab).
	 * @param toolTipText an optional tool tip of the editor (displayed when the mouse is held over the editor's tab).
	 * @param imageDescriptor an optional image to be displayed in the editor's tab (if <code>null</code>, the default icon of the editor is used instead).
	 * @param file the file containing the PDF's data.
	 */
	public PdfViewerEditorInput(String name, String toolTipText, ImageDescriptor imageDescriptor, File file)
	{
		if (file == null)
			throw new IllegalArgumentException("file must not be null!"); //$NON-NLS-1$

		this.name = name;
		this.toolTipText = toolTipText;
		this.imageDescriptor = imageDescriptor;
		this.file = file;
	}

	/**
	 * Create an instance of this editor-input with an URL pointing to the PDF's data.
	 * <p>
	 * This constructor delegates to {@link #PdfViewerEditorInput(String, String, ImageDescriptor, URL)}
	 * and passes the file's simple name (without protocol, host and directory) as <code>name</code> and the file's
	 * complete URL as <code>toolTipText</code>.
	 * </p>
	 *
	 * @param url the URL pointing to the PDF's data.
	 */
	public PdfViewerEditorInput(URL url)
	{
		this(
				new File(url.getPath()).getName(),
				url.toString(),
				null,
				url
		);
	}

	/**
	 * Create an instance of this editor-input with an URL pointing to the PDF's data.
	 *
	 * @param name the name of the editor (displayed in the editor's tab).
	 * @param toolTipText an optional tool tip of the editor (displayed when the mouse is held over the editor's tab).
	 * @param imageDescriptor an optional image to be displayed in the editor's tab (if <code>null</code>, the default icon of the editor is used instead).
	 * @param url the URL pointing to the PDF's data.
	 */
	public PdfViewerEditorInput(String name, String toolTipText, ImageDescriptor imageDescriptor, URL url)
	{
		if (url == null)
			throw new IllegalArgumentException("url must not be null!"); //$NON-NLS-1$

		this.name = name;
		this.toolTipText = toolTipText;
		this.imageDescriptor = imageDescriptor;
		this.url = url;
	}

	/**
	 * Create an instance of this editor-input with the PDF's raw data in the given byte-array. Note, that
	 * it is recommended not to use this constructor, because editor-inputs are held in a history by
	 * Eclipse and therefore this byte-array will never be garbage-collected (thus consuming memory
	 * even after the editor was closed).
	 *
	 * @param name the name of the editor (displayed in the editor's tab).
	 * @param toolTipText an optional tool tip of the editor (displayed when the mouse is held over the editor's tab).
	 * @param imageDescriptor an optional image to be displayed in the editor's tab (if <code>null</code>, the default icon of the editor is used instead).
	 * @param byteArray the PDF's raw data.
	 *
	 * @deprecated Because of eclipse's internal editor history, it's recommended to use one of the other constructors.
	 */
	@Deprecated
	public PdfViewerEditorInput(String name, String toolTipText, ImageDescriptor imageDescriptor, byte[] byteArray) {
		if (byteArray == null)
			throw new IllegalArgumentException("byteArray must not be null!"); //$NON-NLS-1$

		this.name = name;
		this.toolTipText = toolTipText;
		this.imageDescriptor = imageDescriptor;
		this.byteArray = byteArray;
	}

//	/**
//	 * Get the {@link PDFFile} or <code>null</code>, if none has been created before (i.e. {@link #createPDFFile(IProgressMonitor)} was
//	 * not yet called and a <code>PDFFile</code> instance was not passed as constructor argument either).
//	 *
//	 * @return the <code>PDFFile</code> which has been created by a previous call to {@link #createPDFFile(IProgressMonitor)} or <code>null</code>.
//	 */
//	public PDFFile getPDFFile()
//	{
//		return pdfFile;
//	}

	/**
	 * Create a new <code>PDFFile</code> instance. Calling this method a second time, again creates a new <code>PDFFile</code> instance.
	 * You should therefore keep the instance, once you created it.
	 *
	 * @param monitor the monitor for progress feedback.
	 * @return a new instance of <code>PDFFile</code>. This
	 * @throws IOException
	 */
	public PDFFile createPDFFile(IProgressMonitor monitor)
	throws IOException
	{
//		if (pdfFile != null) {
//			monitor.beginTask("PDF already loaded", 1);
//			monitor.worked(1);
//			monitor.done();
//			return pdfFile;
//		}

		PDFFile pdfFile;
		if (url != null) {
			InputStream in = url.openStream();
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				IOUtil.transferStreamData(in, out);
				out.close();
				pdfFile = PdfFileLoader.loadPdf(out.toByteArray(), monitor);
			} finally {
				in.close();
			}
		}
		else if (byteArray != null)
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
		return Util.equals(url, o.url) && Util.equals(file, o.file) && Util.equals(byteArray, o.byteArray);
	}

	@Override
	public int hashCode() {
		return Util.hashCode(url) + Util.hashCode(file) + (byteArray == null ? 0 : Arrays.hashCode(byteArray));
	}

	/**
	 * Get the URL that was used to create this EditorInput or <code>null</code>.
	 * If this is <code>null</code>, one of the methods {@link #getByteArray()} or
	 * {@link #getFile()} will return the PDF input data.
	 *
	 * @return the URL pointing to the raw PDF data or <code>null</code>.
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * Get the byte array that was used to create this EditorInput or <code>null</code>.
	 * If this is <code>null</code>, the method {@link #getFile()} or {@link #getUrl()}
	 * will return a file/url containing the data.
	 *
	 * @return the byte array containing the raw PDF data or <code>null</code>.
	 */
	public byte[] getByteArray() {
		return byteArray;
	}
	/**
	 * Get the file that was used to create this EditorInput or <code>null</code>.
	 * If this is <code>null</code>, the method {@link #getByteArray()} will return the
	 * PDF's raw data or the method {@link #getUrl()} will return an URL pointing to the
	 * input data.
	 *
	 * @return the file containing the raw PDF data or <code>null</code>.
	 */
	public File getFile() {
		return file;
	}
}
