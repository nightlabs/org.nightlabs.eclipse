/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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
package org.nightlabs.base.ui.editor;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.notification.IDirtyStateManager;

/**
 * A section part with the ability to set it undirty.
 *
 * @version $Revision$ - $Date$
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class RestorableSectionPart
extends SectionPart
implements IDirtyStateManager, IFormPartDirtyStateProxy
{
	/**
	 * The default section style consists of a title bar, a twistie and the client of the section
	 * is indented.
	 */
	public static final int DEFAULT_SECTION_STYLE = ExpandableComposite.TITLE_BAR |
		ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT;

	/**
	 * Create an instance of this section part and add
	 * it to a managed form.
	 * @param parent The parent for this section part
	 * @param toolkit The toolkit to create content
	 * @param style The style for this section part
	 */
	public RestorableSectionPart(Composite parent, FormToolkit toolkit, int style)
	{
		super(parent, toolkit, style);
//		adaptSection(toolkit);
	}

	/**
	 * Create an instance of this section part and add
	 * it to a managed form.
	 * @param section The section this part is for
	 * @param managedForm The managed form where this
	 * 		section part should be added or <code>null</code>
	 * 		if the section part should not be added to
	 * 		a managed form.
	 */
	public RestorableSectionPart(Section section, IManagedForm managedForm)
	{
		super(section);
		if(managedForm != null) {
			adaptSection(managedForm.getToolkit());
			managedForm.addPart(this);
		}
	}

	/**
	 * Mark this section dirty.
	 * TODO: workaround for ManagedForm.markDirty() not checking the current dirty state and therefore might mark this editor as undirty.
	 * 	The correct way of handling this situation would be to always check the dirty state before setting a new one. (marius)
	 */
	@Override
	public void markDirty() {
		// fires dirtyStateChangedEvent of the editor
		notifyDirtyStateListeners(true);
		super.markDirty();
//		// if the editor was dirty before the first call it is now undirty -> need to fire it again
//		if (getManagedForm().isDirty())
//			getManagedForm().dirtyStateChanged();
	}

	/**
	 * Mark this section part undirty. Eclipse
	 * SectionPart lacks this feature, so here is
	 * a workaround.
	 */
	public void markUndirty()
	{
		if(UndirtyBehaviour.ENABLED) {
			notifyDirtyStateListeners(false);
			// set dirty = false
			super.commit(false);

			// needs to check if global state was dirty, otherwise this would change the state to dirty,
			// which contradicts the method name and declaration! (marius)

			// TODO: I don't know whether the following check is still necessary... Removing it yields better behaviour in most cases. Tobias.
//			if (getManagedForm().isDirty())
			getManagedForm().dirtyStateChanged();
		}
	}

//	/**
//	 * By default this method is overriden and does nothing.
//	 *
//	 * This is done to avoid undirty state, when section is used in FormEditor
//	 * and page changes, where commit on the page and all included formParts is performed
//	 *
//	 * Inheritans can use method {@link #markUndirty()} to remove dirty state
//	 */
//	@Override
//	public void commit(boolean onSave)
//	{
//		if (onSave)
//			super.commit(onSave);
//	}

	protected void adaptSection(FormToolkit toolkit)
	{
		Section section = getSection();
		FormColors colors = toolkit.getColors();
		int sectionStyle = section.getStyle();
//		Composite parent = section.getParent();
//		if (section.toggle != null) {
//			section.toggle.setHoverDecorationColor(colors
//					.getColor(FormColors.TB_TOGGLE_HOVER));
//			section.toggle.setDecorationColor(colors
//					.getColor(FormColors.TB_TOGGLE));
//		}
//		section.setFont(boldFontHolder.getBoldFont(parent.getFont()));
		if ((sectionStyle & ExpandableComposite.TITLE_BAR) != 0
				|| (sectionStyle & ExpandableComposite.SHORT_TITLE_BAR) != 0) {
			colors.initializeSectionToolBarColors();
			section.setTitleBarBackground(colors.getColor(IFormColors.TB_GBG));
			section.setTitleBarBorderColor(colors
					.getColor(IFormColors.TB_BORDER));
			section.setTitleBarGradientBackground(colors
					.getColor(IFormColors.TB_GBG));
			section.setTitleBarForeground(colors.getColor(IFormColors.TB_FG));
		}
		getSection().setBackgroundMode(SWT.INHERIT_FORCE);
	}

	private ListenerList dirtyStateListeners = new ListenerList();

	@Override
	public void addFormPartDirtyStateProxyListener(
			IFormPartDirtyStateProxyListener listener) {
		dirtyStateListeners.add(listener);
	}

	@Override
	public void removeFormPartDirtyStateProxyListener(
			IFormPartDirtyStateProxyListener listener) {
		dirtyStateListeners.add(listener);
	}

	protected void notifyDirtyStateListeners(boolean dirty) {
		Object[] listeners = dirtyStateListeners.getListeners();
		for (Object listener : listeners) {
			if (listener instanceof IFormPartDirtyStateProxyListener) {
				if (dirty)
					((IFormPartDirtyStateProxyListener) listener).markDiry(this);
				else
					((IFormPartDirtyStateProxyListener) listener).markUndirty(this);
			}
		}
	}

	/**
	 * Notifies the part that an object has been set as overall form's input.
	 * The part can elect to react by revealing or selecting the object, or do
	 * nothing if not applicable.
	 * <p>
	 * The implementation of this method in {@link RestorableSectionPart} calls
	 * {@link #markStale()} and then returns <code>true</code>, assuming that this
	 * default behaviour fits most use-cases in the best way. If this is not desired
	 * in a subclass, it must override without calling the super method.
	 * </p>
	 * <p>
	 * In most use-cases, however, you likely want to override &amp; extend this method in
	 * order to save the <code>input</code> object in a field for later use.
	 * If {@link #markStale()} was called (as it is in the default implementation), the
	 * method {@link #refresh()} will be called afterwards and gives your subclass the
	 * opportunity to load the <code>input</code> object's data into the UI
	 * elements.
	 * </p>
	 *
	 * @return <code>true</code> if the part has selected and revealed the
	 *         input object, <code>false</code> otherwise.
	 *
	 * @see EntityEditorPageWithProgress#handleControllerObjectModified(EntityEditorPageControllerModifyEvent)
	 * @see #refresh()
	 * @see #commit(boolean)
	 */
	@Override
	public boolean setFormInput(Object input) {
		markStale();
		return true;
	}
}
