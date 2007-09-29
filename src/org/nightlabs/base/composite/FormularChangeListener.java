package org.nightlabs.base.composite;

import java.util.EventListener;

/**
 * A listener interface for {@link Formular} changes.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface FormularChangeListener extends EventListener
{
	/**
	 * Called whenever contents of a {@link Formular} have changed.
	 * @param event The change event.
	 */
	void formularChanged(FormularChangedEvent event);
}
