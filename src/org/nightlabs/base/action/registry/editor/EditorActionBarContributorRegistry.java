/**
 * 
 */
package org.nightlabs.base.action.registry.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.action.registry.AbstractActionRegistry;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EditorActionBarContributorRegistry 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.editorActionBarContribution"; //$NON-NLS-1$
	public static final String ELEMENT_REGISTRY = "editorActionBarContributionRegistry"; //$NON-NLS-1$
	public static final String ATTRIBUTE_TARGET_EDITOR_ID = "targetEditorID"; //$NON-NLS-1$
	public static final String ELEMENT_EDITOR_ACTION_BAR_CONTRIBUTION = "editorActionBarContribution"; //$NON-NLS-1$

	private Map<String, AbstractActionRegistry> editorID2ActionRegistry = new HashMap<String, AbstractActionRegistry>();
	
	protected EditorActionBarContributorRegistry() {
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}
	
	public AbstractActionRegistry getActionRegistry(String targetEditorID) {
		checkProcessing(false);
		return editorID2ActionRegistry.get(targetEditorID);
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element) 
	throws Exception {
		if (element.getName().equalsIgnoreCase(ELEMENT_REGISTRY)) {
			String targetEditorID = element.getAttribute(ATTRIBUTE_TARGET_EDITOR_ID);
			if (!checkString(targetEditorID))
				throw new EPProcessorException("The attribute targetEditorID must be set!", extension); //$NON-NLS-1$
			String className = element.getAttribute("class"); //$NON-NLS-1$
			if (!checkString(className))
				className = null;			
			AbstractActionRegistry registry = null;
			if (className != null)
				try {
					registry = (AbstractActionRegistry) element.createExecutableExtension("class"); //$NON-NLS-1$
				} catch (CoreException e) {
					throw new EPProcessorException("Could not instantiate given class "+className, extension, e); //$NON-NLS-1$
				}
			else
				registry = new DefaultEditorActionBarContributionRegistry();
			
			IConfigurationElement[] children = element.getChildren();
			for (IConfigurationElement childElement : children) {
				registry.processElement(extension, childElement);
			}
			editorID2ActionRegistry.put(targetEditorID, registry);
		}
	}
	
	private static EditorActionBarContributorRegistry sharedInstance;	
	public static EditorActionBarContributorRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new EditorActionBarContributorRegistry();
		return sharedInstance;
	}

}
