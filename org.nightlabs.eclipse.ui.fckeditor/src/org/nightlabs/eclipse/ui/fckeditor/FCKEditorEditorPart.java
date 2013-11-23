package org.nightlabs.eclipse.ui.fckeditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorEditorPart extends EditorPart
{
	private IFCKEditor editor;

	/**
	 * Create a new FCKEditorPart instance.
	 */
	public FCKEditorEditorPart()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor arg0)
	{
		editor.commit();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
		// not supported yet
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		if(!(input instanceof IFCKEditorInput))
			throw new PartInitException("Invalid FCKeditor input"); //$NON-NLS-1$

		setSite(site);
		setInput(input);
		// TODO: choose another name...
		setPartName(input.getName());

		editor = new FCKEditor();
		// listener list is automatically cleared in FCKEditor.dispose()
		editor.addPropertyListener(new IPropertyListener() {
			@Override
			public void propertyChanged(Object object, int propertyId)
			{
				firePropertyChange(propertyId);
			}
		});
		editor.init(site.getShell(), (IFCKEditorInput)input);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return editor.isDirty();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		GridLayout gl = new GridLayout();
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		parent.setLayout(gl);
		editor.createControl(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
	}
}
