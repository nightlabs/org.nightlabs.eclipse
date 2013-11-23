package org.nightlabs.base.ui.entity.editor.overview;

import org.eclipse.core.internal.runtime.IRuntimeConstants;
import org.nightlabs.base.ui.entity.editor.EntityEditor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public abstract class AbstractOverviewPageStatusProvider 
implements IOverviewPageStatusProvider 
{
	private EntityEditor entityEditor;
	
	/**
	 * 
	 */
	public AbstractOverviewPageStatusProvider() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.overview.IOverviewPageStatusProvider#setEntityEditor(org.nightlabs.base.ui.entity.editor.EntityEditor)
	 */
	@Override
	public void setEntityEditor(EntityEditor entityEditor) {
		this.entityEditor = entityEditor;
	}

	protected EntityEditor getEntityEditor() {
		return entityEditor;
	}
	
	protected String getStatusPluginId() {
		return IRuntimeConstants.PI_RUNTIME;
	}
}
