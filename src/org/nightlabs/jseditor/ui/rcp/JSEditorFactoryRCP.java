/**
 * 
 */
package org.nightlabs.jseditor.ui.rcp;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jseditor.ui.IJSEditor;
import org.nightlabs.jseditor.ui.IJSEditorFactory;
import org.nightlabs.jseditor.ui.rcp.editor.JSEditorComposite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 *
 */
public class JSEditorFactoryRCP implements IJSEditorFactory {

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditorFactory#createJSEditor(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public IJSEditor createJSEditor(Composite parent) {
		return new JSEditorComposite(parent);
	}

}
