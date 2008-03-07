package org.nightlabs.base.ui.editor;


/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class FormOverviewEditorWrapper
	extends CommitableFormEditor
{
	
	public FormOverviewEditorWrapper()
	{
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

}
