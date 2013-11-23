package org.nightlabs.eclipse.ui.fckeditor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Little excerpt of org.nightlabs.util.IOUtil to prevent dependency on org.nightlabs.base for just these few lines.
 *
 * @author marco
 *
 */
public class IOUtil {
	private IOUtil() { }

	/**
	 * UTF-8 caracter set name.
	 */
	public static final String CHARSET_NAME_UTF_8 = "UTF-8";

	/**
	 * Write text to a file.
	 * @param file The file to write the text to
	 * @param text The text to write
	 * @param encoding The caracter set to use as file encoding (e.g. "UTF-8")
	 * @throws IOException in case of an io error
	 * @throws FileNotFoundException if the file exists but is a directory
	 *                   rather than a regular file, does not exist but cannot
	 *                   be created, or cannot be opened for any other reason
	 * @throws UnsupportedEncodingException If the named encoding is not supported
	 */
	public static void writeTextFile(File file, String text, String encoding)
	throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		FileOutputStream out = null;
		OutputStreamWriter w = null;
		try {
			out = new FileOutputStream(file);
			w = new OutputStreamWriter(out, encoding);
			w.write(text);
		} finally {
			if (w != null) w.close();
			if (out != null) out.close();
		}
	}

	/**
	 * Write text to a file using UTF-8 encoding.
	 * @param file The file to write the text to
	 * @param text The text to write
	 * @throws IOException in case of an io error
	 * @throws FileNotFoundException if the file exists but is a directory
	 *                   rather than a regular file, does not exist but cannot
	 *                   be created, or cannot be opened for any other reason
	 */
	public static void writeTextFile(File file, String text)
	throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		writeTextFile(file, text, CHARSET_NAME_UTF_8);
	}
}
