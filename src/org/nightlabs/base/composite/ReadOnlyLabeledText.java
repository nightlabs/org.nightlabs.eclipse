/**
 * 
 */
package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * A LabledText that is {@link SWT#READ_ONLY}, but still with a white background.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReadOnlyLabeledText extends LabeledText {

	/**
	 * @param parent
	 * @param caption
	 * @param textStyle
	 * @param labelStyle
	 * @param wrapperStyle
	 * @param setLayoutData
	 */
	public ReadOnlyLabeledText(Composite parent, String caption,
			int labelStyle, int wrapperStyle, boolean setLayoutData) {
		super(parent, caption, labelStyle, wrapperStyle, SWT.BORDER | SWT.READ_ONLY, setLayoutData);
		initComposite();
	}

	/**
	 * @param parent
	 * @param caption
	 * @param textStyle
	 * @param setLayoutData
	 */
	public ReadOnlyLabeledText(Composite parent, String caption, boolean setLayoutData) {
		super(parent, caption, SWT.BORDER | SWT.READ_ONLY, setLayoutData);
		initComposite();
	}

	/**
	 * @param parent
	 * @param caption
	 * @param textStyle
	 */
	public ReadOnlyLabeledText(Composite parent, String caption) {
		super(parent, caption, SWT.BORDER | SWT.READ_ONLY);
		initComposite();
	}
	
	/**
	 * @param parent
	 * @param caption
	 * @param textStyle
	 */
	public ReadOnlyLabeledText(Composite parent, String caption, int textStyle) {
		super(parent, caption, textStyle | SWT.READ_ONLY);
		initComposite();
	}
	
	private void initComposite() {
		getTextControl().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}
}
