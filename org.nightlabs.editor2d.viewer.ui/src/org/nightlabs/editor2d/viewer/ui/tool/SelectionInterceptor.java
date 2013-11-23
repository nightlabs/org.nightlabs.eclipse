package org.nightlabs.editor2d.viewer.ui.tool;

import org.nightlabs.editor2d.DrawComponent;

public interface SelectionInterceptor {
	/**
	 * {@link SelectTool#selectionInterceptor_canSelect(DrawComponent)} calls this method
	 * (if a <code>SelectionInterceptor</code> has been assigned using
	 * {@link SelectTool#setSelectionInterceptor(SelectionInterceptor)}) in
	 * order to find out whether the given {@link DrawComponent} <code>dc</code>
	 * shall be ignored or can be selected.
	 *
	 * @param dc The <code>DrawComponent</code> to check.
	 * @return Returns <code>true</code> if the given <code>dc</code> is selectable and
	 *		<code>false</code> if it shall be ignored (applies to both, selection and
	 *		UNselection).
	 */
	boolean canSelect(DrawComponent dc);
}
