package org.nightlabs.base.ui.entity.editor.overview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.MessageSectionPart;
import org.nightlabs.base.ui.entity.EntityEditorRegistry;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageSettings;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.base.ui.form.NightlabsFormsToolkit;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class OverviewSection extends MessageSectionPart {

	private FormEditor formEditor;
	private List<StatusComposite>  statusComposites;

	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public OverviewSection(IFormPage page, Composite parent, FormEditor formEditor)
	{
		super(page, parent, ExpandableComposite.EXPANDED, ""); //$NON-NLS-1$
		this.formEditor = formEditor;

		statusComposites = new ArrayList<StatusComposite>();
//		createComposite(getContainer());
		ScrolledForm form = getToolkit().createScrolledForm(getContainer());
		form.getBody().setLayout(new GridLayout());
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
		createComposite(form.getBody());
	}

	protected void createComposite(Composite parent) {
		if (formEditor instanceof EntityEditor) {
			EntityEditor entityEditor = (EntityEditor) formEditor;
			EntityEditorRegistry registry = EntityEditorRegistry.sharedInstance();
			for (EntityEditorPageSettings pageSettings : registry.getPageSettingsOrdered(entityEditor.getEditorID())) {
				IEntityEditorPageFactory pageFactory = pageSettings.getPageFactory();
				IOverviewPageStatusProvider statusProvider = registry.createOverviewPageStatusProvider(pageFactory);
				if (statusProvider != null) {
					statusProvider.setEntityEditor(entityEditor);
				}
				IFormPage page = entityEditor.getController().getPage(pageFactory);
				if (!(page instanceof OverviewPage))
					createEntry(parent, pageSettings, statusProvider, page);
			}
		}
	}

	protected void createEntry(Composite parent, final EntityEditorPageSettings pageSettings,
			final IOverviewPageStatusProvider statusProvider, final IFormPage page)
	{
		Composite wrapper = new XComposite(parent, SWT.NONE);
		wrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		wrapper.setLayout(new GridLayout(4, false));

		IHyperlinkListener hyperlinkListener = new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				formEditor.setActivePage(page.getId());
			}
		};

		ImageHyperlink imageHyperlink = getToolkit().createImageHyperlink(wrapper, SWT.LEFT);
		if (pageSettings.getIconDesc() != null) {
			imageHyperlink.setImage(pageSettings.getIconDesc().createImage());
		}
		imageHyperlink.addHyperlinkListener(hyperlinkListener);
		GridData imageData = new GridData(64, 64);
		imageHyperlink.setLayoutData(imageData);

		Composite textWrapper = new XComposite(wrapper, SWT.NONE);
		GridData textWrapperData = new GridData(250, SWT.DEFAULT);
		textWrapper.setLayoutData(textWrapperData);
		Hyperlink hyperlink = getToolkit().createHyperlink(textWrapper, page.getTitle(), SWT.NONE);
		hyperlink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hyperlink.addHyperlinkListener(hyperlinkListener);
		Label descriptionLabel = new Label(textWrapper, SWT.NONE);
		descriptionLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		if (pageSettings.getDescription() != null) {
			descriptionLabel.setText(pageSettings.getDescription());
		}

//		getToolkit().createSeparator(wrapper, SWT.NONE);

		StatusComposite statusComposite = new StatusComposite(wrapper, SWT.NONE, statusProvider);
		statusComposites.add(statusComposite);
	}

	protected FormToolkit getToolkit() {
		return new NightlabsFormsToolkit(getSection().getDisplay());
	}

	@Override
	public void refresh() {
		super.refresh();
		for (StatusComposite statusComposite : statusComposites) {
			if (!statusComposite.isDisposed()) {
				statusComposite.refresh();
			}
		}
		getContainer().layout(true, true);
	}
}
