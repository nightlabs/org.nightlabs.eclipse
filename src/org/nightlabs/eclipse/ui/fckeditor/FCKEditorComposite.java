package org.nightlabs.eclipse.ui.fckeditor;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorComposite extends Composite
{
	private IFCKEditor editor;

	/**
	 * Create a new FCKEditorComposite instance.
	 * @param parent The parent composite
	 * @param style The composite style
	 */
	public FCKEditorComposite(Composite parent, int style, IFCKEditorInput input)
	{
		super(parent, style);
		GridLayout gl = new GridLayout();
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		setLayout(gl);
		editor = new FCKEditor();
		try {
			editor.init(parent.getShell(), input);
		} catch (PartInitException e) {
			// TODO: different handling?
			throw new RuntimeException(e);
		}
		editor.createControl(this);
	}

	public IFCKEditor getEditor()
	{
		return editor;
	}
}
