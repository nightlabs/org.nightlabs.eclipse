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
package org.nightlabs.eclipse.ui.pdfviewer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.eclipse.ui.pdfviewer.internal.Util;
import org.nightlabs.eclipse.ui.pdfviewer.resource.Messages;


import com.sun.pdfview.PDFFile;

/**
 * Utility class to make reading a {@link PDFFile} from various sources easier.
 * It allows to load a PDF from a byte array, an input stream and a file - other
 * util methods might follow later.
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 * @author frederik loeser - frederik at nightlabs dot de
 */
public class PdfFileLoader
{
	private PdfFileLoader() { }

	/**
	 * Create a {@link PDFFile} from a byte array.
	 *
	 * @param byteArray the byte array containing the PDF.
	 * @return a {@link PDFFile} with the contents from the given <code>byteArray</code>.
	 * @throws IOException if the {@link PDFFile} failed to read the data.
	 */
	public static PDFFile loadPdf(byte[] byteArray, IProgressMonitor monitor) throws IOException {
		monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader.loadPdf.monitor.task.name"), 100); //$NON-NLS-1$
		try {
			ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
			monitor.worked(50);
			PDFFile pdfFile = new PDFFile(byteBuffer);
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
	public static PDFFile loadPdf(InputStream in, IProgressMonitor monitor) throws IOException
	{
		monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader.loadPdf.monitor.task.name"), 100); //$NON-NLS-1$
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Util.transferStreamData(in, out);
			out.close();

			monitor.worked(50);

			ByteBuffer byteBuffer = ByteBuffer.wrap(out.toByteArray());
			PDFFile pdfFile = new PDFFile(byteBuffer);

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
	public static PDFFile loadPdf(File file, IProgressMonitor monitor) throws IOException
	{
		monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader.loadPdf.monitor.task.name"), 100); //$NON-NLS-1$

		try {
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r"); //$NON-NLS-1$
			try {
				FileChannel fileChannel = randomAccessFile.getChannel();
				try {
					ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
					monitor.worked(50);
					PDFFile pdfFile = new PDFFile(byteBuffer);
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

	public static PDFFile loadPdf(URL url, IProgressMonitor monitor) throws IOException
	{
		InputStream in = url.openStream();
		try {
			return loadPdf(in, monitor);
		} finally {
			in.close();
		}
	}

}
