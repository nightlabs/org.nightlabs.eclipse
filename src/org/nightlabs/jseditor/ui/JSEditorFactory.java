/**
 * 
 */
package org.nightlabs.jseditor.ui;


import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.slf4j.LoggerFactory;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 *
 */
public class JSEditorFactory extends AbstractEPProcessor {

	private static JSEditorFactory sharedInstance;
	
	private IJSEditorFactory factory;
	
	/**
	 * 
	 */
	public JSEditorFactory() {
	}
	
	public static IJSEditor createJSEditor(Composite parent) {
		if (sharedInstance == null) {
			synchronized (JSEditorFactory.class) {
				if (sharedInstance == null) {
					sharedInstance = new JSEditorFactory();
					sharedInstance.process();
				}
			}
		}
		return sharedInstance._createJSEditor(parent);
	}

	private IJSEditor _createJSEditor(Composite parent) {
		if (factory != null) {
			return factory.createJSEditor(parent);
		}
		throw new IllegalStateException("Can't create new JSEditor because no Factory is set.");
	}
	
	@Override
	public String getExtensionPointID() {
		return "org.nightlabs.jseditor.ui.jsEditorFactory";
	}

	@Override
	public void processElement(IExtension extension,
			IConfigurationElement element) throws Exception {
		if (element.getName().equals("jsEditorFactory")) {
			if (factory != null) {
				LoggerFactory.getLogger(JSEditorFactory.class).warn("More than one jsEditorFactory were registered, the first one will be used.", new Exception());
			} else {
				factory = (IJSEditorFactory) element.createExecutableExtension("class");
			}
		}
	}

}
