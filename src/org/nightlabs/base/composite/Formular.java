package org.nightlabs.base.composite;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * An {@link XComposite} to easily add and hold formular like widget 
 * combinations.
 * 
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class Formular extends XComposite implements ModifyListener, SelectionListener
{
	private Set<FormularChangeListener> listeners = null;
	
	public Formular(Composite parent, int style, FormularChangeListener formularChangedListener)
	{
		super(parent, style, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 2);
		addFormularChangeListener(formularChangedListener);
	}

	public void addFormularChangeListener(FormularChangeListener listener)
	{
		if(listener != null) {
			if(listeners == null)
				listeners = Collections.synchronizedSet(new HashSet<FormularChangeListener>(1));
			listeners.add(listener);
		}
	}
	
	public void removeFormularChangeListener(FormularChangeListener listener)
	{
		if(listener != null && listeners != null)
			listeners.remove(listener);
	}

	public Formular(Composite parent, int style)
	{
		this(parent, style, null);
	}
	
	public Text addTextInput(String labelText, String value)
	{
    Label label = new Label(this, SWT.NULL);
    if(labelText != null)
    	label.setText(labelText);
    else
    	label.setText(""); //$NON-NLS-1$
    Text text = new Text(this, SWT.BORDER | SWT.SINGLE);
    if(value != null)
    	text.setText(value);
    text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    text.addModifyListener(this);
    return text;
	}
	
	public Button addCheckBox(String text, boolean checked)
	{
    Button button = new Button(this, SWT.CHECK);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    button.setLayoutData(gd);
    if(text != null)
    	button.setText(text);
		button.setSelection(checked);
		button.addSelectionListener(this);
		return button;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose()
	{
		super.dispose();
		listeners = null;
	}

	private void fireFormularChangedEvent(Object source)
	{
		if(listeners != null) {
			FormularChangedEvent event = new FormularChangedEvent(this, (Widget)source);
			for (FormularChangeListener listener : listeners) {
				listener.formularChanged(event);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e)
	{
		fireFormularChangedEvent(e.getSource());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e)
	{
		fireFormularChangedEvent(e.getSource());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e)
	{
		fireFormularChangedEvent(e.getSource());
	}
}
