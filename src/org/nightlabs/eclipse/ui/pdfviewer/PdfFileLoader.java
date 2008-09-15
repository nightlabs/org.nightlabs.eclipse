package org.nightlabs.eclipse.ui.pdfviewer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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
	public static PDFFile loadPdf(byte[] byteArray) throws IOException {
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
	public static PDFFile loadPdf(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Util.transferStreamData(in, out);
		out.close();

		ByteBuffer byteBuffer = ByteBuffer.wrap(out.toByteArray());
		return new PDFFile(byteBuffer);
	}

	/**
	 * Create a {@link PDFFile} from a {@link File}.
	 *
	 * @param file the {@link File} containing the PDF.
	 * @return a {@link PDFFile} with the contents from the given file.
	 * @throws IOException if the {@link PDFFile} failed to read the data.
	 */
	public static PDFFile loadPdf(File file) throws IOException
	{
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		try {
			FileChannel fileChannel = randomAccessFile.getChannel();
			try {
				ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
				return new PDFFile(byteBuffer);
			} finally {
				fileChannel.close();
			}
		} finally {
			randomAccessFile.close();
		}
	}
}
