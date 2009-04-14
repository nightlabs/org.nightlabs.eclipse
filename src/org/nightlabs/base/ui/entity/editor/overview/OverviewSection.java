package org.nightlabs.base.ui.entity.editor.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.MessageSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.form.NightlabsFormsToolkit;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.toolkit.IToolkit;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class OverviewSection extends MessageSectionPart {

	private FormEditor formEditor;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public OverviewSection(IFormPage page, Composite parent, FormEditor formEditor) 
	{
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED, "Overview");
		this.formEditor = formEditor;
		createComposite(getContainer());
	}

	protected void createComposite(Composite parent) {
		if (formEditor instanceof EntityEditor) {
			EntityEditor entityEditor = (EntityEditor) formEditor;
			for (IFormPage page : entityEditor.getPages()) {
				if (!OverviewPage.PAGE_ID.equals(page.getId()))
					createHyperLink(parent, page);
			}
		}
	}
	
	protected void createHyperLink(Composite parent, final IFormPage page) {
		Composite wrapper = new XComposite(parent, SWT.NONE);
		wrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		wrapper.setLayout(new GridLayout(2, false));
		String message = null;
		if (page instanceof IStatusFormPage) {
			wrapper.setLayout(new GridLayout(3, false));
			IStatusFormPage statusFormPage = (IStatusFormPage) page;
			message = statusFormPage.isComplete();
			Label statusImageLabel = new Label(wrapper, SWT.NONE);
			Image statusImage = null;
			if (message == null) {
				statusImage = SharedImages.getSharedImage(NLBasePlugin.getDefault(), OverviewSection.class, "Complete");
			}
			else {
				statusImage = SharedImages.getSharedImage(NLBasePlugin.getDefault(), OverviewSection.class, "Incomplete");
			}
			statusImageLabel.setImage(statusImage);
//			getManagedForm().getMessageManager().addMessage(message, message, message, IMessageProvider.ERROR, wrapper);
		}
		Hyperlink hyperlink = getToolkit().createHyperlink(wrapper, page.getTitle(), SWT.NONE);
//		hyperlink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
				formEditor.setActivePage(page.getId());
			}
		});
		
		Label messageLabel = new Label(wrapper, SWT.NONE);
		messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (message != null) {
			messageLabel.setText(message);
		}
	}
	
	protected IToolkit getToolkit() {
		return new NightlabsFormsToolkit(getSection().getDisplay());
	}
	
	@Override
	public void initialize(IManagedForm form) {
		super.initialize(form);
//		createComposite(getContainer());
	}
}
