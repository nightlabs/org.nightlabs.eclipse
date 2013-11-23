/**
 * 
 */
package org.nightlabs.jseditor.ui;

import org.eclipse.swt.widgets.Composite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 *
 */
public interface IJSEditorFactory {

	IJSEditor createJSEditor(Composite parent);
}
