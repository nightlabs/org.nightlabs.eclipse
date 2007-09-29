package org.nightlabs.base.celleditor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.version.MalformedVersionException;
import org.nightlabs.version.Version;
import org.nightlabs.version.VersionRangeEndPoint;
import org.nightlabs.version.VersionRangeEndPoint.EndPointLocation;

/**
 * @author Marius Heinzmann -- Marius[at]NightLabs[dot]de
 *
 */
public class VersionRangeCellEditor 
	extends XCellEditor 
{
//	public VersionCellEditor() {
//		setValidator(new VersionValidator());
//	}
	
	/**
	 * @param parent
	 */
	public VersionRangeCellEditor(Composite parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public VersionRangeCellEditor(Composite parent, int style) {
		this(parent, style, false);
	}

	/**
	 * @param parent
	 * @param style
	 * @param readOnly
	 */
	public VersionRangeCellEditor(Composite parent, int style, boolean readOnly) {
		super(parent, style, readOnly);
//		setValidator(null);
	}

	protected XComposite wrapper;
	protected Button checkBox;
	protected Text text;
	
	private EndPointLocation location;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createControl(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.NONE, 2);
		checkBox = new Button(wrapper, SWT.CHECK);
		text = new Text(wrapper, SWT.SINGLE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addVerifyListener(new VerifyListener() {
		
			public void verifyText(VerifyEvent event) {
				// if old value was valid
				boolean oldValidState = true;
				try {
					new Version(text.getText());
				} catch (MalformedVersionException mve) {
					oldValidState = false;
				}
				
				// check new value
				try {
					StringBuffer newVersionString = new StringBuffer(((Text)event.widget).getText());
					newVersionString.replace(event.start, event.end, event.text);
					new Version(newVersionString.toString());
				} catch (MalformedVersionException e) {
					setErrorMessage(e.getMessage());
					valueChanged(oldValidState, false);
					return;
				}
				setErrorMessage(null);
				valueChanged(oldValidState, true);
			}
		
		});
		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				keyReleaseOccured(e);
			}
		});
		return wrapper;
	}

	@Override
	protected void keyReleaseOccured(KeyEvent keyEvent) {
		if (keyEvent.character == '\u001b') { // Escape character
			fireCancelEditor();
		} else if (getErrorMessage() == null || "".equals(getErrorMessage())) { //$NON-NLS-1$
			super.keyReleaseOccured(keyEvent);
		}
	}
	
	protected class VersionValidator implements ICellEditorValidator {

		public String isValid(Object value) {
			if (value instanceof Version)
				return null;
			
			final String versionString = (String) value;
			try {
				new Version(versionString);
			} catch (MalformedVersionException e) {
				return e.getMessage();
			}
			return null;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doGetValue()
	 */
	@Override
	protected Object doGetValue() {
		try {
			return new VersionRangeEndPoint(new Version(text.getText()), checkBox.getSelection(), 
					location);
		} catch (MalformedVersionException e) {
			throw new RuntimeException("The stored String representation of a Version is invalid! "+ //$NON-NLS-1$
					"This should never happen since its validity is checked on change!",e); //$NON-NLS-1$
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
	 */
	@Override
	protected void doSetValue(Object value) {
		if (isReadOnly())
			return;
		
  	Assert.isTrue(checkBox != null &&  text != null && (value instanceof VersionRangeEndPoint));
  	final VersionRangeEndPoint endPoint = (VersionRangeEndPoint) value;
		checkBox.setSelection(endPoint.isInclusive());
		text.setText(endPoint.getEndPoint().toString());
		location = endPoint.getLocation();
	}

	@Override
	protected void doSetFocus() {
		if (text != null && ! text.isDisposed()) {
  		text.selectAll();
			text.setFocus();
		}
	}

}
