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


import com.sun.pdfview.PDFFile;

/**
 * Utility class to make reading a {@link PDFFile} from various sources easier.
 * It allows to load a PDF from a byte array, an input stream and a file - other
 * util methods might follow later.
 *
 * @author marco schulze - marco at nightlabs dot de
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
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		return new PDFFile(byteBuffer);
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
		monitor.beginTask("Loading PDF file", 100);
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
		monitor.beginTask("Loading PDF file", 100);

		try {
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
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
