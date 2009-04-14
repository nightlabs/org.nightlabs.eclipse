package org.nightlabs.base.ui.entity.editor.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.MessageSectionPart;
import org.nightlabs.base.ui.form.NightlabsFormsToolkit;
import org.nightlabs.base.ui.toolkit.IToolkit;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class DefaultOverviewPageSection extends MessageSectionPart {

	private FormEditor formEditor;
	private String pageId;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public DefaultOverviewPageSection(IFormPage page, Composite parent, int style, String title, 
			FormEditor formEditor, String pageId) 
	{
		super(page, parent, style, title);
		this.formEditor = formEditor;
		this.pageId = pageId;
		createComposite(getContainer());
	}

	protected void createComposite(Composite parent) {
		Composite wrapper = new XComposite(parent, SWT.NONE);
		wrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
		Hyperlink hyperlink = getToolkit().createHyperlink(wrapper, getSection().getText(), SWT.NONE);
		hyperlink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hyperlink.addHyperlinkListener(new IHyperlinkListener(){
			@Override
			public void linkExited(HyperlinkEvent e) {
				// do nothing
			}
			@Override
			public void linkEntered(HyperlinkEvent e) {
				// do nothing
			}
			@Override
			public void linkActivated(HyperlinkEvent e) {
				formEditor.setActivePage(pageId);
			}
		});
	}
	
	protected IToolkit getToolkit() {
		return new NightlabsFormsToolkit(getSection().getDisplay());
	}
}
