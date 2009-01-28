package org.nightlabs.base.ui.exceptionhandler.errorreport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.nightlabs.config.Config;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReportSenderPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
	/**
	 * Create a new ErrorReportSenderPreferencePage instance.
	 */
	public ErrorReportSenderPreferencePage()
	{
	}

	/**
	 * Create a new ErrorReportSenderPreferencePage instance.
	 * @param title
	 */
	public ErrorReportSenderPreferencePage(String title)
	{
		super(title);
	}

	/**
	 * Create a new ErrorReportSenderPreferencePage instance.
	 * @param title
	 * @param image
	 */
	public ErrorReportSenderPreferencePage(String title, ImageDescriptor image)
	{
		super(title, image);
	}

	private static ErrorReportSenderDescriptor autoSelectErrorReportSender = new ErrorReportSenderDescriptor(null) 
	{
		/* (non-Javadoc)
		 * @see org.nightlabs.base.ui.exceptionhandler.errorreport.ErrorReportSenderDescriptor#getName()
		 */
		@Override
		public String getName()
		{
			return "Automatic selection";
		}
	};
	private ComboViewer senderCombo;
	private Link link;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);
		composite.setFont(parent.getFont());

		
		Label l = new Label(composite, SWT.WRAP);
		l.setText("Error report sender module: ");
		
		senderCombo = new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		senderCombo.setContentProvider(new ArrayContentProvider());
		senderCombo.setLabelProvider(new LabelProvider() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element)
			{
				if(element == null)
					return "Automatic selection";
				if(element instanceof ErrorReportSenderDescriptor)
					return ((ErrorReportSenderDescriptor)element).getName();
				return super.getText(element);
			}
		});
		senderCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				ErrorReportSenderDescriptor sd = getSelection();
				link.setEnabled(sd != null && sd.getPreferencePageId() != null);
			}
		});
		
		link = new Link(composite, SWT.WRAP);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalIndent = 32;
		link.setLayoutData(gd);
		link.setFont(composite.getFont());
		link.setText("<A>Configure selected error report sender...</A>");  //$NON-NLS-1$
		link.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				doLinkActivated((Link) e.widget);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				doLinkActivated((Link) e.widget);
			}
		});
		
		// build the content
		Map<String, ErrorReportSenderDescriptor> senders = ErrorReportSenderRegistry.getRegistry().getSenders();
		List<ErrorReportSenderDescriptor> realValues = new ArrayList<ErrorReportSenderDescriptor>(senders.values());
		Collections.sort(realValues, new Comparator<ErrorReportSenderDescriptor>() {
			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(ErrorReportSenderDescriptor o1, ErrorReportSenderDescriptor o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		List<ErrorReportSenderDescriptor> values = new ArrayList<ErrorReportSenderDescriptor>(senders.values().size() + 1);
		values.add(autoSelectErrorReportSender);
		values.addAll(realValues);
		senderCombo.setInput(values.toArray(new ErrorReportSenderDescriptor[senders.size()]));
		
		// set selection
		Config configuration = Config.sharedInstance();
		ErrorReportSenderCfMod cfMod  = configuration.createConfigModule(ErrorReportSenderCfMod.class);
		String errorReportSenderId = cfMod.getErrorReportSenderId();
		if(errorReportSenderId == null) {
			senderCombo.setSelection(new StructuredSelection(autoSelectErrorReportSender));
		} else {
			ErrorReportSenderDescriptor selected = senders.get(errorReportSenderId);
			if(selected != null)
				senderCombo.setSelection(new StructuredSelection(selected));
			else
				senderCombo.setSelection(new StructuredSelection(autoSelectErrorReportSender));
		}

		Dialog.applyDialogFont(composite);
		return composite;
	}

	private void doLinkActivated(Link widget)
	{
		ErrorReportSenderDescriptor sd = getSelection();
		if(sd != null) {
			String ppid = sd.getPreferencePageId();
			if(ppid != null) {
				openPage(ppid);
			}
		}
	}
	
	private void openPage(String id)
	{
		if(getContainer() instanceof IWorkbenchPreferenceContainer)
			((IWorkbenchPreferenceContainer)getContainer()).openPage(id, null);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench arg0)
	{
	}
	
	private ErrorReportSenderDescriptor getSelection()
	{
		IStructuredSelection s = (IStructuredSelection)senderCombo.getSelection();
		ErrorReportSenderDescriptor sd = (ErrorReportSenderDescriptor)s.getFirstElement();
		if(sd == null || sd == autoSelectErrorReportSender)
			return null;
		return sd;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk()
	{
		String id = null;
		ErrorReportSenderDescriptor sd = getSelection();
		if(sd != null)
			id = sd.getId();
		Config configuration = Config.sharedInstance();
		ErrorReportSenderCfMod cfMod  = configuration.createConfigModule(ErrorReportSenderCfMod.class);
		cfMod.setErrorReportSenderId(id);
		configuration.save();
		return super.performOk();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults()
	{
		senderCombo.setSelection(new StructuredSelection(autoSelectErrorReportSender));
		super.performDefaults();
	}
}
