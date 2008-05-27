// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file.image;

import javax.imageio.ImageIO;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class Test
{
	public static void main(String[] args)
	{
//		IIORegistry.getDefaultInstance().registerServiceProvider(new PCXImageReaderSPI());
//		IIORegistry.getDefaultInstance().registerServiceProvider(new PCXImageWriterSPI());

		String[] x;
		x = ImageIO.getWriterMIMETypes();
		for (String s : x) {
			System.out.println(s);
		}

		System.out.println();

		x = ImageIO.getWriterFileSuffixes();
		for (String s : x) {
			System.out.println(s);
		}
	}

}
