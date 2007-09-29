/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.wizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;

/**
 * WizardHop for the reusage of MultiPageFormEditor {@link FormPage}s.
 * You can use this WizardHop like an {@link FormEditor} and add 
 * {@link FormPage}s to it. Where the FormEditor creates tabs for the
 * different {@link FormPage}s this hop will add a new {@link WizardHopPage}s
 * with a {@link ScrolledForm} as parent component.
 * <p>
 * This Hop is intended to be subclassed and filled with {@link FormPage}s
 * as you would use the {@link FormEditor}.
 * <p>
 * Use the method {@link #addFormPage(FormPage)} to add the given {@link FormPage}.
 * You can obtain the {@link FormEditor} parameter used to create {@link FormPage}s
 * via {@link #getEditor()}.
 * <p>
 * The implementation of your {@link FormPage}s can be shared for FormEditors
 * and Wizards, they simply have to implement 
 * {@link FormPage#createFormContent(IManagedForm)}
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class FormPageWizardHop extends WizardHop {

	/**
	 * A dummy editor serving a new {@link FormToolkit}.
	 */
	private static class Editor extends FormEditor {

		private FormToolkit _toolkit; 
		
		@Override
		public FormToolkit getToolkit() {
			return _toolkit;
		}
		
		public void initToolkit(Display display) {
			_toolkit = new FormToolkit(Display.getDefault());
		}
		
		public void dispose() {
			super.dispose();
			_toolkit.dispose();
		}
		
		@Override
		protected void addPages() {
		}
		@Override
		public void doSave(IProgressMonitor monitor) {
		}
		@Override
		public void doSaveAs() {
		}

		@Override
		public boolean isSaveAsAllowed() {
			return false;
		}
	}
	
	/**
	 * This page is added to the hop per {@link FormPage}.
	 * It simply lets the {@link FormPage} create its contents
	 * into a fresh {@link Form}.
	 */
	private static class FormPageWizardPage extends WizardHopPage {

		private FormPage page;
		
		public FormPageWizardPage(FormPage page) {
			super(page.getClass().getName());
			this.page = page;
			if (page != null) {
				setTitle(page.getTitle());
				setMessage(page.getTitleToolTip());
			}
		}

		@Override
		public Control createPageContents(Composite parent) {
			if (getWizardHop().getEntryPage().equals(this)) {
				parent.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						page.getEditor().dispose();
					}
				});
			}
			XComposite wrapper = new XComposite(parent, SWT.BORDER, LayoutMode.TIGHT_WRAPPER);
			if (!(page.getEditor() instanceof Editor)) {
				page.initialize(new Editor());
			}
			((Editor)page.getEditor()).initToolkit(wrapper.getDisplay());
			page.createPartControl(wrapper);
			return wrapper;
		}
		
	}
	
	/**
	 * Creates a new {@link FormPageWizardHop}. 
	 * Note that the hop is not ready before at least one call
	 * to {@link #addFormPage(FormPage)} was done.
	 */
	public FormPageWizardHop() {
		super();
	}
	
	/**
	 * You can create a {@link FormPageWizardHop} with an other
	 * entry page.
	 * <p> 
	 * Note that you also might add other pages to the hop by
	 * {@link #addHopPage(IWizardHopPage)}.
	 * 
	 * @param entryPage The entry page of the new hop.
	 */
	public FormPageWizardHop(IWizardHopPage entryPage) {
		super(entryPage);
	}

	/**
	 * Adds a new {@link WizardHopPage} that will have 
	 * the contents created by the given {@link FormPage}.
	 * <p>
	 * The first {@link FormPage} added here will be the 
	 * entry page of the wizard hop and can be referenced
	 * by {@link WizardHop#getEntryPage()}.
	 *  
	 * @param formPage The {@link FormPage} to add a {@link WizardHopPage} for.
	 */
	public void addFormPage(FormPage formPage) {
		FormPageWizardPage page = new FormPageWizardPage(formPage);
		if (getEntryPage() == null) {
			setEntryPage(page);
		}
		else
			addHopPage(page);
	}

	/**
	 * The dummy editor for all pages.
	 */
	private Editor editor;

	/**
	 * Get the dummy implementation of {@link FormEditor}
	 * for this hop. This should be used to create new
	 * {@link FormPage}s that afterwards can be added with
	 * {@link #addFormPage(FormPage)}.
	 * 
	 * @return The editor for this hop.
	 */
	public Editor getEditor() {
		if (editor == null)
			editor = new Editor();
		return editor;
	}

}
