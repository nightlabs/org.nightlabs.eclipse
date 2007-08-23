package org.nightlabs.keyreader.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.config.Config;
import org.nightlabs.keyreader.KeyReaderMan;

public class KeyReaderUseCaseRegistry
		extends AbstractEPProcessor
{
	private static KeyReaderUseCaseRegistry _sharedInstance = null;
	public synchronized static KeyReaderUseCaseRegistry sharedInstance()
	{
		if (_sharedInstance == null) {
			try {
				KeyReaderMan.createSharedInstance(Config.sharedInstance());
				_sharedInstance = new KeyReaderUseCaseRegistry();
				_sharedInstance.process();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return _sharedInstance;
	}

	@Implement
	public String getExtensionPointID()
	{
		return "org.nightlabs.keyreader.ui.keyReaderUseCase"; //$NON-NLS-1$
	}

	private Map<String, KeyReaderUseCase> keyReaderUseCases = new HashMap<String, KeyReaderUseCase>();

	@Implement
	public void processElement(IExtension extension, IConfigurationElement element)
			throws Exception
	{
		String keyReaderID = element.getAttribute("keyReaderID"); //$NON-NLS-1$
		if (keyReaderID == null || "".equals(keyReaderID)) //$NON-NLS-1$
			throw new EPProcessorException("The attribute 'keyReaderID' is missing or empty!", extension); //$NON-NLS-1$

		if (keyReaderUseCases.containsKey(keyReaderID))
			throw new EPProcessorException("Duplicate definition of keyReaderID: " + keyReaderID, extension); //$NON-NLS-1$

		String name = element.getAttribute("name"); //$NON-NLS-1$
		if (name == null) name = ""; //$NON-NLS-1$
		String description = element.getAttribute("description"); //$NON-NLS-1$
		if (description == null) description = ""; //$NON-NLS-1$

		keyReaderUseCases.put(keyReaderID, new KeyReaderUseCase(keyReaderID, name, description));
	}

	public KeyReaderUseCase getKeyReaderUseCase(String keyReaderID, boolean throwExceptionIfNotFound)
	{
		KeyReaderUseCase res = keyReaderUseCases.get(keyReaderID);
		if (throwExceptionIfNotFound && res == null)
			throw new IllegalArgumentException("No KeyReaderUseCase registered for keyReaderID=" + keyReaderID); //$NON-NLS-1$
		return res;
	}

	public Collection<KeyReaderUseCase> getKeyReaderUseCases()
	{
		return keyReaderUseCases.values();
	}
}
