/**
 * 
 */
package org.nightlabs.jseditor.ui.rap;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jseditor.ui.IJSEditor;
import org.nightlabs.jseditor.ui.IJSEditorFactory;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 *
 */
public class JSEditorFactoryRAP implements IJSEditorFactory {

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.IJSEditorFactory#createJSEditor(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public IJSEditor createJSEditor(Composite parent) {
		return new JSEditorRAP(parent);
	}

}
