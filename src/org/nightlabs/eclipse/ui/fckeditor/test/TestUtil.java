/* ************************************************************************
 * org.nightlabs.eclipse.ui.fckeditor - Eclipse RCP FCKeditor Integration *
 * Copyright (C) 2008 NightLabs - http://NightLabs.org                    *
 *                                                                        *
 * This library is free software; you can redistribute it and/or          *
 * modify it under the terms of the GNU Lesser General Public             *
 * License as published by the Free Software Foundation; either           *
 * version 2.1 of the License, or (at your option) any later version.     *
 *                                                                        *
 * This library is distributed in the hope that it will be useful,        *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU      *
 * Lesser General Public License for more details.                        *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with this library; if not, write to the                  *
 *     Free Software Foundation, Inc.,                                    *
 *     51 Franklin St, Fifth Floor,                                       *
 *     Boston, MA  02110-1301  USA                                        *
 *                                                                        *
 * Or get it online:                                                      *
 *     http://www.gnu.org/copyleft/lesser.html                            *
 **************************************************************************/
package org.nightlabs.eclipse.ui.fckeditor.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorContent;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorContentFile;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContent;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public class TestUtil
{
	public static List<IFCKEditorContentFile> getFiles() throws IOException
	{
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

		file = new FCKEditorContentFile();
		file.setContentType("image/jpeg");
		file.setName("Daniel");
		contents = getFileContents("/icons/test/DSC00304.JPG");
		file.setData(contents);
		files.add(file);
		
		file = new FCKEditorContentFile();
		file.setContentType("image/jpeg");
		file.setName("NightLabs Österreich");
		contents = getFileContents("/icons/test/DSC00311.JPG");
		file.setData(contents);
		files.add(file);
		
		file = new FCKEditorContentFile();
		file.setContentType("image/jpeg");
		file.setName("Österreich");
		contents = getFileContents("/icons/test/DSC00313.JPG");
		file.setData(contents);
		files.add(file);
		
		return files;
	}
	
	private static byte[] getFileContents(String bundleFilePath) throws IOException
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
	
	public static IFCKEditorContent getContent() throws IOException
	{
		IFCKEditorContent content = new FCKEditorContent();
		double rand = Math.random();
		content.setHtml("<p>Bla bla bla, <b>mein</b> Text</p><p>Hallo! "+rand+"</p>");
		content.setFiles(getFiles());
		return content;
	}
}
