//$Id: OverviewPage.java 1734 2008-01-08 16:02:20Z marc $
package org.nightlabs.eclipse.preferences.ui;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * Overview page for preference and property dialogs.
 * This page shows a list of links for sub pages.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision: 1734 $ - $Date: 2008-01-08 17:02:20 +0100 (Di, 08 Jan 2008) $
 */
public class OverviewPage extends PreferencePage implements IWorkbenchPreferencePage, IWorkbenchPropertyPage
{
	private IProject fProject; // project or null

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite composite)
	{
		noDefaultAndApplyButton();
		
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

		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPropertyPage#getElement()
	 */
	public IAdaptable getElement() {
		return fProject;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	public void setElement(IAdaptable element) {
		fProject= (IProject) element.getAdapter(IResource.class);
	}

	/**
	 * Is this a project preference (property) page?
	 * @return <code>true</code> if this a project preference (property)
	 * 		page
	 */
	protected boolean isProjectPreferencePage() {
		return fProject != null;
	}
	
	/**
	 * Get the project or <code>null</code> if no project is assigned.
	 * @return the project or <code>null</code> if no project is assigned
	 */
	protected IProject getProject() {
		return fProject;
	}
	
	/**
	 * Get the pages introduction text. Subclasses may override to
	 * return another text.
	 * @return The introduction text
	 */
	protected String getIntroductionText()
	{
		return "Chose one of the sub-pages:";
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
		if(!(getContainer() instanceof PreferenceDialog))
			return new String[0];
		String myPageId = getMyPageId();
		PreferenceDialog dlg = (PreferenceDialog)getContainer();
		IPreferenceNode myNode = dlg.getPreferenceManager().find(myPageId);
		if(myNode == null)
			return new String[0];
		IPreferenceNode[] subNodes = myNode.getSubNodes();
		String[] result = new String[subNodes.length];
		for (int i = 0; i < subNodes.length; i++)
	    result[i] = subNodes[i].getId();
		return result;
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
	
	@SuppressWarnings("unchecked")
  private String getMyPageId()
	{
		if(!(getContainer() instanceof PreferenceDialog))
			return null;
		PreferenceDialog dlg = (PreferenceDialog)getContainer();
		List<IPreferenceNode> nodes = dlg.getPreferenceManager().getElements(PreferenceManager.PRE_ORDER);
		for (Iterator<IPreferenceNode> i = nodes.iterator(); i.hasNext();) {
			IPreferenceNode node = i.next();
			if(node.getPage() == this)
				return node.getId();
		}
		return null;
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
		if(!(getContainer() instanceof PreferenceDialog))
			return null;
		PreferenceDialog dlg = (PreferenceDialog)getContainer();
		List<IPreferenceNode> nodes = dlg.getPreferenceManager().getElements(PreferenceManager.POST_ORDER);
		for (Iterator<IPreferenceNode> i = nodes.iterator(); i.hasNext();) {
			IPreferenceNode node = i.next();
			if (node.getId().equals(nodeId)) {
				return node;
			}
		}
		return null;
	}
	
	private void openPage(String id)
	{
		if(getContainer() instanceof IWorkbenchPreferenceContainer)
			((IWorkbenchPreferenceContainer)getContainer()).openPage(id, null);
	}
}
