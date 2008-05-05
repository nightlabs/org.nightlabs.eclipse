package org.nightlabs.eclipse.ui.fckeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorActionBarContributer extends EditorActionBarContributor
{
	private static class EditorAction extends Action
	{
		private IFCKEditor editor;
		public void setEditor(IFCKEditor editor)
		{
			this.editor = editor;
		}
		@Override
		public void run()
		{
			if(editor != null)
				editor.print();
		}
	}
	
	EditorAction print = new EditorAction() {
	};

	public void setActiveEditor(IEditorPart part) {
		IActionBars bars= getActionBars();
		if (bars == null)
			return;
		if(!(part instanceof IFCKEditor))
			return;
		print.setEditor((IFCKEditor)part);
		bars.setGlobalActionHandler(ActionFactory.PRINT.getId(), print);
		bars.updateActionBars();
	}
}
