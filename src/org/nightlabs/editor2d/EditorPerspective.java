/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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
package org.nightlabs.editor2d;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.editor2d.views.LayerView;
import org.nightlabs.editor2d.views.QuickOptionsView;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPerspective 
implements IPerspectiveFactory 
{
	public static final String ID_PERSPECTIVE = EditorPerspective.class.getName();
	
	public EditorPerspective() {
		super();
	}

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) 
	{
    layout.setEditorAreaVisible(true);
    String editorArea = layout.getEditorArea();
    layout.addView(IPageLayout.ID_EDITOR_AREA, IPageLayout.TOP, IPageLayout.RATIO_MAX, editorArea);
    
    rightTop = layout.createFolder("rightTop", IPageLayout.RIGHT, 0.75f, editorArea); //$NON-NLS-1$
    rightTop.addView(IPageLayout.ID_PROP_SHEET);
    rightMiddle = layout.createFolder("rightMiddle", IPageLayout.BOTTOM, 0.33f, "rightTop"); //$NON-NLS-1$ //$NON-NLS-2$
    rightMiddle.addView(IPageLayout.ID_OUTLINE);    
    rightBottom = layout.createFolder("rightBottom", IPageLayout.BOTTOM, 0.5f, "rightMiddle"); //$NON-NLS-1$ //$NON-NLS-2$
    rightBottom.addView(LayerView.ID_VIEW);
    rightBottom.addView(QuickOptionsView.ID);
               
    RCPUtil.addAllPerspectiveShortcuts(layout);
    layout.addPerspectiveShortcut(ID_PERSPECTIVE);  
    layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
    layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    layout.addShowViewShortcut(LayerView.ID_VIEW);
    layout.addShowViewShortcut(QuickOptionsView.ID);
	}

	private IFolderLayout rightBottom;
	protected IFolderLayout getRightBottom() {
		return rightBottom;
	}
	
	private IFolderLayout rightMiddle;
	protected IFolderLayout getRightMiddle() {
		return rightMiddle;
	}
	
	private IFolderLayout rightTop;
	protected IFolderLayout getRightTop() {
		return rightTop;
	}
}
