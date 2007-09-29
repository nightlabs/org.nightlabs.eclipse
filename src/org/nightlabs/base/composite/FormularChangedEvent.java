package org.nightlabs.base.composite;

import java.util.EventObject;

import org.eclipse.swt.widgets.Widget;

/**
 * An event that is fired when the contents of a {@link Formular}
 * have changed.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FormularChangedEvent extends EventObject
{
	/**
	 * The serial version of this class.
	 */
	private static final long serialVersionUID = 1L;
	
	private Formular formular;
	
	FormularChangedEvent(Formular formular, Widget source)
	{
		super(source);
		this.formular = formular;
	}

	/**
	 * Get the formular.
	 * @return the formular
	 */
	public Formular getFormular()
	{
		return formular;
	}
}
