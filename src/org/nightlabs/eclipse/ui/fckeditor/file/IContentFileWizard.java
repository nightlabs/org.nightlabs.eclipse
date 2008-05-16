// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file;

import java.io.File;

import org.eclipse.jface.wizard.IWizard;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface IContentFileWizard extends IWizard
{
	void setSourceFile(File file, String mimeType);
	byte[] getData();
	String getMimeType();
	String getName();
	String getDescription();
}
