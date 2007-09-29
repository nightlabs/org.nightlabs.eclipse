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

package org.nightlabs.base.entity.editor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * Extension point settings for an entity page extension.
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class EntityEditorPageSettings
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
			throw new EPProcessorException("The class attribute was not valid ", extension); //$NON-NLS-1$
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
}
