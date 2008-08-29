package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.nightlabs.util.IOUtil;

import com.sun.pdfview.PDFFile;

public class PdfFileLoader {
	
	private File file;
	PDFFile pdfDocument;
	
	public PdfFileLoader() {
		
	}
	
	public PdfFileLoader(File file) {
		this.file = file;
	}

	public void loadPdf(InputStream in) throws IOException {
		File f = File.createTempFile("pdfviewer", null);
		f.deleteOnExit();
		FileOutputStream out = new FileOutputStream(f);
		IOUtil.transferStreamData(in, out);
		out.close();
		loadPdf(f);
	}

	public PDFFile loadPdf(File file) throws IOException {
		try {
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
			FileChannel fileChannel = randomAccessFile.getChannel();
			ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			pdfDocument = new PDFFile(byteBuffer);
		}
		catch (IOException exception) {
			System.out.println(exception.getStackTrace());
		}
		return pdfDocument;
	}
	
	public PDFFile loadPdf() {
		try {
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
			FileChannel fileChannel = randomAccessFile.getChannel();
			ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			pdfDocument = new PDFFile(byteBuffer);
		}
		catch (IOException exception) {
			System.out.println(exception.getStackTrace());
		}
		return pdfDocument;
		
	}
	
	
}
