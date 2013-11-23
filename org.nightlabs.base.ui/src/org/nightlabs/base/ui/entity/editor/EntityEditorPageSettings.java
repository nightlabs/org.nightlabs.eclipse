/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.ui.entity.editor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.eclipse.extension.EPProcessorException;
import org.nightlabs.util.Util;

/**
 * Extension point settings for an entity page extension.
 *
 * @version $Revision$ - $Date$
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class EntityEditorPageSettings implements Comparable<EntityEditorPageSettings>
{
	/**
	 * Page factory implementation
	 */
	private IEntityEditorPageFactory pageFactory;

	/**
	 * The editor ID for this entity editor page.
	 */
	private String editorID;

	/**
	 * The index hint for this entity editor page telling
	 * the system where to position this page in the multi
	 * page editor.
	 */
	private int indexHint;

	/**
	 * The {@link ImageDescriptor} for the small (16x16) icon.
	 */
	private ImageDescriptor smallIconDesc;

	/**
	 * The {@link ImageDescriptor} for the (48x48) icon.
	 */
	private ImageDescriptor iconDesc;

	/**
	 * The description of the page
	 */
	private String description;

	/**
	 *
	 * @param editorID
	 * @param indexHint
	 * @param factory
	 * @param smallIconDesc
	 * @param iconDesc
	 * @param description
	 */
	public EntityEditorPageSettings(String editorID, int indexHint, IEntityEditorPageFactory factory,
			ImageDescriptor smallIconDesc, ImageDescriptor iconDesc, String description)
	{
		this.editorID = editorID;
		this.indexHint = indexHint;
		this.pageFactory = factory;
		this.smallIconDesc = smallIconDesc;
		this.iconDesc = iconDesc;
		this.description = description;
	}

	/**
	 * Create an instance of EntityEditorPageSettings for
	 * an extension point entry.
	 * @param cfg The extension point config element.
	 */
	public EntityEditorPageSettings(IExtension extension, IConfigurationElement cfg)
	throws EPProcessorException
	{
		try {
			this.pageFactory = (IEntityEditorPageFactory)cfg.createExecutableExtension("class"); //$NON-NLS-1$
		} catch (Exception e) {
			throw new EPProcessorException("The class attribute was not valid ", extension, e); //$NON-NLS-1$
		}

//		this.pageClass = cfg.getAttribute("class");
		this.editorID = cfg.getAttribute("editorID"); //$NON-NLS-1$
		if (editorID == null || "".equals(editorID)) //$NON-NLS-1$
			throw new EPProcessorException("The editorID is not defined.", extension); //$NON-NLS-1$
		String indexHintStr = cfg.getAttribute("indexHint"); //$NON-NLS-1$
		if(indexHintStr != null)
			try {
				this.indexHint = Integer.parseInt(indexHintStr);
			} catch (Exception e) {
				this.indexHint = Integer.MAX_VALUE / 2;
			}
		else
			this.indexHint = Integer.MAX_VALUE / 2;

		String smallIconPath = cfg.getAttribute("icon16x16"); //$NON-NLS-1$
		if (smallIconPath != null) {
			this.smallIconDesc = AbstractUIPlugin.imageDescriptorFromPlugin(
					cfg.getNamespaceIdentifier(), smallIconPath);
		}
		String iconPath = cfg.getAttribute("icon48x48"); //$NON-NLS-1$
		if (iconPath != null) {
			this.iconDesc = AbstractUIPlugin.imageDescriptorFromPlugin(
					cfg.getNamespaceIdentifier(), iconPath);
		}
		this.description = cfg.getAttribute("description"); //$NON-NLS-1$
	}

//	/**
//	 * Create an instance of the page class via reflection.
//	 * @param editor The editor for which to create the page.
//	 * @return A new form page instance
//	 */
//	public IFormPage createPage(FormEditor editor)
//	{
//		try {
//			Class c = Class.forName(pageClass);
//			Constructor constr = c.getConstructor(new Class[] { FormEditor.class });
//			IFormPage page = (IFormPage)constr.newInstance(new Object[] { editor });
//			return page;
//		} catch(Exception e) {
//			throw new RuntimeException("Creation of editor page failed", e);
//		}
//	}

	/**
	 * Get the editorID.
	 * @return the editorID
	 */
	public String getEditorID()
	{
		return editorID;
	}

	/**
	 * Set the editorID.
	 * @param editorID the editorID to set
	 */
	public void setEditorID(String editorID)
	{
		this.editorID = editorID;
	}

	/**
	 * Get The index hint for this entity editor page telling
	 * the system where to position this page in the multi
	 * page editor.
	 * @return the indexHint
	 */
	public int getIndexHint()
	{
		return indexHint;
	}

	/**
	 * Set the indexHint.
	 * @param indexHint the indexHint to set
	 */
	public void setIndexHint(int indexHint)
	{
		this.indexHint = indexHint;
	}

	/**
	 * Returns the implementation of {@link IEntityEditorPageFactory}
	 * registered with this extension.
	 *
	 * @return the pageFactory The implementation of {@link IEntityEditorPageFactory}
	 * registered with this extension.
	 */
	public IEntityEditorPageFactory getPageFactory() {
		return pageFactory;
	}

	/**
	 * Sets the implementation of {@link IEntityEditorPageFactory}
	 * for this extension.
	 * @param pageFactory the pageFactory to set
	 */
	public void setPageFactory(IEntityEditorPageFactory pageFactory) {
		this.pageFactory = pageFactory;
	}

	/**
	 * Returns the smallIconDesc.
	 * @return the smallIconDesc
	 */
	public ImageDescriptor getSmallIconDesc() {
		return smallIconDesc;
	}

	/**
	 * Sets the smallIconDesc.
	 * @param smallIconDesc the smallIconDesc to set
	 */
	public void setSmallIconDesc(ImageDescriptor smallIconDesc) {
		this.smallIconDesc = smallIconDesc;
	}

	/**
	 * Returns the iconDesc.
	 * @return the iconDesc
	 */
	public ImageDescriptor getIconDesc() {
		return iconDesc;
	}

	/**
	 * Sets the iconDesc.
	 * @param iconDesc the iconDesc to set
	 */
	public void setIconDesc(ImageDescriptor iconDesc) {
		this.iconDesc = iconDesc;
	}

	/**
	 * Returns the description.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return
			this.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) +
			"[" + (pageFactory != null ? pageFactory.getClass().getName() : "no-page-factory") + "," +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			editorID + ", " + indexHint + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Sorts using first the editorID (alphabetically) then the  indexHint
	 * and finally the class-name of the pageFactory.
	 */
	@Override
	public int compareTo(EntityEditorPageSettings o) {
		if (o == null)
			return -1;
		// null-checks of members is in constructor.
		if (!Util.equals(editorID, o.editorID))
			this.editorID.compareTo(o.editorID);
		if (indexHint == o.indexHint)
			return pageFactory.getClass().getName().compareTo(o.pageFactory.getClass().getName());
		return indexHint - o.indexHint;
	}
}
