package org.nightlabs.keyreader.ui;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.config.Config;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.eclipse.extension.EPProcessorException;
import org.nightlabs.keyreader.KeyReader;
import org.nightlabs.keyreader.KeyReaderImplementation;
import org.nightlabs.keyreader.KeyReaderMan;

public class KeyReaderImplementationRegistry
extends AbstractEPProcessor
{
	private static KeyReaderImplementationRegistry _sharedInstance = null;
	public synchronized static KeyReaderImplementationRegistry sharedInstance()
	{
		if (_sharedInstance == null) {
			try {
				KeyReaderMan.createSharedInstance(Config.sharedInstance());
				_sharedInstance = new KeyReaderImplementationRegistry();
				_sharedInstance.process();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return _sharedInstance;
	}

	@Override
	public String getExtensionPointID()
	{
		return "org.nightlabs.keyreader.ui.keyReaderImplementation"; //$NON-NLS-1$
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
			throws Exception
	{
		try {
			KeyReader keyReader = (KeyReader) element.createExecutableExtension("class"); //$NON-NLS-1$
			KeyReaderMan.sharedInstance().getKeyReaderImplementations().add(new KeyReaderImplementation(keyReader));
		} catch (Throwable e) {
			throw new EPProcessorException("Loading extension failed!", extension, e); //$NON-NLS-1$
		}
	}

	public List<KeyReaderImplementation> getKeyReaderImplementations()
	{
		try {
			return KeyReaderMan.sharedInstance().getKeyReaderImplementations();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
