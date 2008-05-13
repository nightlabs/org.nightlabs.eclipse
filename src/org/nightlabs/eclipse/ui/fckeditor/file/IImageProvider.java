// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface IImageProvider
{
	Image getImage(IFCKEditorContentFile file);

	Image getImage(IFCKEditorContentFile file, IImageCallback imageCallback);

	void dispose();

	void stopThumbnailing();

	int getThumbnailSize();

	void setThumbnailSize(int thumbnailSize);
}