/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
 ******************************************************************************/
package org.nightlabs.base.editor;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.form.NightlabsFormsToolkit;

/**
 * An abstract form editor that allows commiting
 * its form pages.
 * 
 * @version $Revision$ - $Date$
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class CommitableFormEditor extends FormEditor
{
	@Override
	public void init(IEditorSite arg0, IEditorInput arg1) throws PartInitException {
		super.init(arg0, arg1);
		super.firePropertyChange(PROP_TITLE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation does not save anything
	 * but commits all form pages in this editor.
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		commitFormPages(true);
	}

	/**
	 * Call commit on all managed from pages.
	 * @param onSave <code>true</code> to indicate that
	 * 		we are commiting because of an editor save.
	 */
	protected void commitFormPages(boolean onSave) 
	{
		IFormPage[] pages = getFormPages();
		for (int i = 0; i < pages.length; i++) {
			IFormPage page = pages[i];
			IManagedForm mform = page.getManagedForm();
			if (mform != null && mform.isDirty())
				mform.commit(true);
		}
	}

	/**
	 * Get all form pages in this editor.
	 * @return All form pages in this editor.
	 */
	protected IFormPage[] getFormPages() 
	{
		ArrayList<IFormPage> formPages = new ArrayList<IFormPage>(pages.size());
		for (int i = 0; i < pages.size(); i++) {
			Object page = pages.get(i);
			if (page instanceof IFormPage)
				formPages.add((IFormPage)page);
		}
		return formPages.toArray(new IFormPage[formPages.size()]);
	}
	
	@Override
	protected FormToolkit createToolkit(Display display) {
		return new NightlabsFormsToolkit(display);
	}
	
}
