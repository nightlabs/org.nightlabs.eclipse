/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.nightlabs.eclipse.preferences.ui;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

/**
 * Project selection dialog.
 * <p>
 * This class was originally taken from the Eclipse JDT project where
 * it was a Java Project selection dialog.
 * </p>
 * @author unascribed
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision: 1734 $ - $Date: 2008-01-08 17:02:20 +0100 (Di, 08 Jan 2008) $
 */
public class ProjectSelectionDialog extends SelectionStatusDialog {

	// the visual selection widget group
	private TableViewer fTableViewer;
	private Set<IProject> fProjectsWithSpecifics;

	// sizing constants
	private final static int SIZING_SELECTION_WIDGET_HEIGHT= 250;
	private final static int SIZING_SELECTION_WIDGET_WIDTH= 300;
	
	//private final static String DIALOG_SETTINGS_SHOW_ALL= "ProjectSelectionDialog.show_all"; //$NON-NLS-1$

	private ViewerFilter fFilter;

	/**
	 * Create a new ProjectSelectionDialog.
	 * @param parentShell The parent shell
	 * @param projectsWithSpecifics projects with specific preferences
	 */
	public ProjectSelectionDialog(Shell parentShell, Set<IProject> projectsWithSpecifics) {
		super(parentShell);
		setTitle("Project Specific Configuration");  
		setMessage("&Select the project to configure:"); 
		fProjectsWithSpecifics= projectsWithSpecifics;
		
        int shellStyle = getShellStyle();
        setShellStyle(shellStyle | SWT.MAX | SWT.RESIZE);
		
		fFilter= new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return fProjectsWithSpecifics.contains(element);
			}
		};
		
	}

	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {
		// page group
		Composite composite= (Composite) super.createDialogArea(parent);

		Font font= parent.getFont();
		composite.setFont(font);

		createMessageArea(composite);

		fTableViewer= new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				doSelectionChanged(((IStructuredSelection) event.getSelection()).toArray());
			}
		});
		fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
                okPressed();
			}
		});
		GridData data= new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint= SIZING_SELECTION_WIDGET_HEIGHT;
		data.widthHint= SIZING_SELECTION_WIDGET_WIDTH;
		fTableViewer.getTable().setLayoutData(data);

		fTableViewer.setLabelProvider(new LabelProvider() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element)
			{
				return ((IProject)element).getName();
			}
		});
		fTableViewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement)
			{
				return (IProject[])inputElement;
			}
			public void dispose()
			{
			}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
			}
		});
		fTableViewer.getControl().setFont(font);

		Button checkbox= new Button(composite, SWT.CHECK);
		checkbox.setText("Show only &projects with project specific settings"); 
		checkbox.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		checkbox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateFilter(((Button) e.widget).getSelection());
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				updateFilter(((Button) e.widget).getSelection());
			}
		});
//		IDialogSettings dialogSettings= JavaPlugin.getDefault().getDialogSettings();
//		boolean doFilter= !dialogSettings.getBoolean(DIALOG_SETTINGS_SHOW_ALL) && !fProjectsWithSpecifics.isEmpty();
//		checkbox.setSelection(doFilter);
//		updateFilter(doFilter);
		
		fTableViewer.setInput(ResourcesPlugin.getWorkspace().getRoot().getProjects());
		
		doSelectionChanged(new Object[0]);
		Dialog.applyDialogFont(composite);
		return composite;
	}
	
	/**
	 * Update the project filter.
	 * @param selected <code>true</code> to filter - <code>false</code>
	 * 		not to filter.
	 */
	protected void updateFilter(boolean selected) {
		if (selected) {
			fTableViewer.addFilter(fFilter);
		} else {
			fTableViewer.removeFilter(fFilter);
		}
//		JavaPlugin.getDefault().getDialogSettings().put(DIALOG_SETTINGS_SHOW_ALL, !selected);
	}

	private void doSelectionChanged(Object[] objects) {
		if (objects.length != 1) {
			updateStatus(new StatusInfo(IStatus.ERROR, "")); //$NON-NLS-1$
			setSelectionResult(null);
		} else {
			updateStatus(new StatusInfo()); 
			setSelectionResult(objects);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
	 */
	protected void computeResult() {
	}
}
