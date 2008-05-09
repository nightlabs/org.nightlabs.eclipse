// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface IImageCallback
{
	void updateImage(IFCKEditorContentFile file, Image image);
}
