/**
 * 
 */
package org.nightlabs.base.action.registry.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.action.registry.AbstractActionRegistry;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DefaultEditorActionBarContributionRegistry 
extends AbstractActionRegistry 
{

	public DefaultEditorActionBarContributionRegistry() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.action.registry.AbstractActionRegistry#createActionOrContributionItem(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected Object createActionOrContributionItem(
			IExtension extension,
			IConfigurationElement element
		) 
	throws EPProcessorException 
	{
		try {
			return element.createExecutableExtension("class"); //$NON-NLS-1$
		} catch (CoreException e) {
			throw new EPProcessorException(e.getMessage(), extension, e);
		}
	}
	
	@Override
	protected String getActionElementName() {
		return EditorActionBarContributorRegistry.ELEMENT_EDITOR_ACTION_BAR_CONTRIBUTION;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return DefaultEditorActionBarContributionRegistry.class.getName();
	}

}
