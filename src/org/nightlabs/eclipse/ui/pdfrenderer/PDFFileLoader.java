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
package org.nightlabs.eclipse.ui.pdfrenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.nightlabs.eclipse.ui.pdfrenderer.internal.Util;
import org.nightlabs.eclipse.ui.pdfrenderer.resource.Messages;

import com.sun.pdfview.PDFFile;

/**
 * Utility class to make reading a {@link PDFFile} from various sources easier.
 * It allows to load a PDF from a byte array, an input stream and a file - other
 * util methods might follow later.
 *
 * @version $Revision: 343 $ - $Date: 2008-10-10 00:42:14 +0200 (Fri, 10 Oct 2008) $
 * @author marco schulze - marco at nightlabs dot de
 * @author frederik loeser - frederik at nightlabs dot de
 */
public class PDFFileLoader
{
	private PDFFileLoader() { }

	/**
	 * Create a {@link PDFFile} from a byte array.
	 *
	 * @param byteArray the byte array containing the PDF.
	 * @return a {@link PDFFile} with the contents from the given <code>byteArray</code>.
	 * @throws IOException if the {@link PDFFile} failed to read the data.
	 */
	public static PDFFile loadPdf(final byte[] byteArray, final IPDFProgressMonitor monitor) throws IOException {
		monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader.loadPdf.monitor.task.name"), 100); //$NON-NLS-1$
		try {
			final ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
			monitor.worked(50);
			final PDFFile pdfFile = new PDFFile(byteBuffer);
			monitor.worked(50);
			return pdfFile;
		} finally {
			monitor.done();
		}
	}

	/**
	 * Create a {@link PDFFile} from an {@link InputStream}.
	 *
	 * @param in the {@link InputStream} containing the PDF. The InputStream is read completely, but <b>not</b> closed!
	 * @return a {@link PDFFile} with the contents from the given input stream.
	 * @throws IOException if the {@link PDFFile} failed to read the data.
	 */
	public static PDFFile loadPDF(final InputStream in, final IPDFProgressMonitor monitor) throws IOException
	{
		monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader.loadPdf.monitor.task.name"), 100); //$NON-NLS-1$
		try {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			Util.transferStreamData(in, out);
			out.close();

			monitor.worked(50);

			final ByteBuffer byteBuffer = ByteBuffer.wrap(out.toByteArray());
			final PDFFile pdfFile = new PDFFile(byteBuffer);

			monitor.worked(50);

			return pdfFile;
		} finally {
			monitor.done();
		}
	}

	/**
	 * Create a {@link PDFFile} from a {@link File}.
	 *
	 * @param file the {@link File} containing the PDF.
	 * @return a {@link PDFFile} with the contents from the given file.
	 * @throws IOException if the {@link PDFFile} failed to read the data.
	 */
	public static PDFFile loadPDF(final File file, final IPDFProgressMonitor monitor) throws IOException
	{
		monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader.loadPdf.monitor.task.name"), 100); //$NON-NLS-1$

		try {
			final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r"); //$NON-NLS-1$
			try {
				final FileChannel fileChannel = randomAccessFile.getChannel();
				try {
					final ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
					monitor.worked(50);
					final PDFFile pdfFile = new PDFFile(byteBuffer);
					monitor.worked(50);
					return pdfFile;
				} finally {
					fileChannel.close();
				}
			} finally {
				randomAccessFile.close();
			}
		} finally {
			monitor.done();
		}
	}

	/**
	 * Create a PDFFile from an URL.
	 *
	 * @param url The url to load the PDF from.
	 * @param monitor The monitor used to report progress.
	 * @return a {@link PDFFile} with the contents from the given URL.
	 * @throws IOException if the {@link PDFFile} failed to read the data.
	 */
	public static PDFFile loadPDF(final URL url, final IPDFProgressMonitor monitor) throws IOException
	{
		final InputStream in = url.openStream();
		try {
			return loadPDF(in, monitor);
		} finally {
			in.close();
		}
	}

}
