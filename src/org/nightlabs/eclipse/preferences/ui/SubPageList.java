package org.nightlabs.eclipse.preferences.ui;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class SubPageList extends Composite
{
	private PreferenceDialog preferenceDialog;
	private String pageId;

	/**
	 * Create a new SubPageList instance.
	 */
	public SubPageList(Composite parent, int style, PreferenceDialog preferenceDialog, String pageId)
	{
		super(parent, style);
		this.preferenceDialog = preferenceDialog;
		this.pageId = pageId;

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		createSubPageContents(this);
	}

	/**
	 * Get the page ids to link to on the overview page.
	 * The default implementation returns all sub pages.
	 * Subclassers may override to show only a filtered
	 * set of pages.
	 * @return The sub page ids
	 */
	protected String[] getSubPageIds()
	{
		IPreferenceNode preferenceNode = preferenceDialog.getPreferenceManager().find(pageId);
		if(preferenceNode == null)
			return new String[0];
		IPreferenceNode[] subNodes = preferenceNode.getSubNodes();
		String[] result = new String[subNodes.length];
		for (int i = 0; i < subNodes.length; i++)
	    result[i] = subNodes[i].getId();
		return result;
	}

	/**
	 * Create the sub-page link list.
	 * @param composite The parent composite
	 */
	protected void createSubPageContents(Composite composite)
	{
		GridData gd;
		Label l = new Label(composite, SWT.WRAP);
		l.setText(getIntroductionText());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		l.setLayoutData(gd);

		Composite linkComposite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		linkComposite.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 0;
		gd.horizontalIndent = 5;
		linkComposite.setLayoutData(gd);

		for (final String subPageId : getSubPageIds()) {
			Label ll = new Label(linkComposite, SWT.NONE);
			gd = new GridData();
			gd.verticalIndent = 5;
			ll.setLayoutData(gd);
			ll.setFont(composite.getFont());
			ll.setText("- ");

			Link link = new Link(linkComposite, SWT.NONE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalIndent = 5;
			link.setLayoutData(gd);
			link.setFont(composite.getFont());
			link.setText("<A>" + getPageLabel(subPageId) + "</A>");  //$NON-NLS-1$//$NON-NLS-2$
			link.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					openPage(subPageId);
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					openPage(subPageId);
				}
			});
		}
	}

	private void openPage(String id)
	{
		if(preferenceDialog instanceof IWorkbenchPreferenceContainer)
			((IWorkbenchPreferenceContainer)preferenceDialog).openPage(id, null);
	}

	private String getPageLabel(String pageId)
	{
		IPreferenceNode node = findNodeMatching(pageId);
		if(node != null) {
			String labelText = node.getLabelText();
			if(labelText != null)
				return labelText;
		}
		return pageId;
	}

	/**
	 * Find the <code>IPreferenceNode</code> that has data the same id as the
	 * supplied value.
	 *
	 * @param nodeId
	 *            the id to search for.
	 * @return <code>IPreferenceNode</code> or <code>null</code> if not
	 *         found.
	 */
	@SuppressWarnings("unchecked")
	protected IPreferenceNode findNodeMatching(String nodeId) {
		List<IPreferenceNode> nodes = preferenceDialog.getPreferenceManager().getElements(PreferenceManager.POST_ORDER);
		for (Iterator<IPreferenceNode> i = nodes.iterator(); i.hasNext();) {
			IPreferenceNode node = i.next();
			if (node.getId().equals(nodeId)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Get the pages introduction text. Subclasses may override to
	 * return another text.
	 * @return The introduction text
	 */
	protected String getIntroductionText()
	{
		return "Choose one of the sub-pages:";
	}
}
