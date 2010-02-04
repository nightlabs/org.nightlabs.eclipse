package org.nightlabs.base.ui.timepattern.input;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.timepattern.InputTimePattern;

/**
 * Composite to edit an {@link InputTimePattern} using an {@link InputTimePatternDialog}. It will
 * show a Label with the definition-string of the current time pattern and let the user open an
 * {@link InputTimePatternDialog} to create a new pattern.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class InputTimePatternEditComposite extends XComposite {
	
	/** The {@link InputTimePattern} of this composite */
	private InputTimePattern inputTimePattern;
	/** The {@link Label} displaying the definition-string */
	private Text displayText;
	
	/** Optional caption, set in the constructor. If set a label will be created */
	private final String caption;
	
	/**
	 * Create a new {@link InputTimePatternEditComposite} that fills horizontal.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use.
	 */
	public InputTimePatternEditComposite(Composite parent, int style) {
		this(parent, style, LayoutDataMode.GRID_DATA_HORIZONTAL);
	}

	/**
	 * Create a new {@link InputTimePatternEditComposite} that fills horizontal.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use.
	 * @param caption An optional caption for the Composite 
	 */
	public InputTimePatternEditComposite(Composite parent, int style, String caption) {
		this(parent, style, LayoutDataMode.GRID_DATA_HORIZONTAL, caption);
	}
	
	/**
	 * Create a new {@link InputTimePatternEditComposite} with the given {@link LayoutDataMode}.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use.
	 * @param layoutDataMode The {@link LayoutDataMode} to use.
	 */
	public InputTimePatternEditComposite(Composite parent, int style, LayoutDataMode layoutDataMode) {
		this(parent, style, layoutDataMode, null);
	}
	
	/**
	 * Create a new {@link InputTimePatternEditComposite} with the given {@link LayoutDataMode}.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use.
	 * @param layoutDataMode The {@link LayoutDataMode} to use.
	 * @param caption An optional caption for the Composite 
	 */
	public InputTimePatternEditComposite(Composite parent, int style, LayoutDataMode layoutDataMode, String caption) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER, layoutDataMode);
		this.caption = caption;
		init();
	}

	/**
	 * Creates the widgets and listeners.
	 */
	private void init() {
		getGridLayout().numColumns = 2;
		getGridLayout().makeColumnsEqualWidth = false;
		if (caption != null && !caption.isEmpty()) {
			Label l = new Label(this, SWT.WRAP);
			l.setText(caption);
			GridData lgd = new GridData(GridData.FILL_HORIZONTAL);
			lgd.horizontalSpan = 2;
			l.setLayoutData(lgd);
		}
		displayText = new Text(this, getBorderStyle());
		displayText.setEditable(false);
		displayText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		updateLabel();
		final Button dlgButton = new Button(this, SWT.PUSH);
		dlgButton.setText("...");
		dlgButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputTimePattern dlgResult = InputTimePatternDialog.open(dlgButton.getShell(), inputTimePattern);
				if (dlgResult != null) {
					inputTimePattern = dlgResult;
					updateLabel();
				}
			}
		});
	}

	/**
	 * Called to update the displayText with the current {@link InputTimePattern}.
	 */
	private void updateLabel() {
		if (inputTimePattern == null) {
			displayText.setText("No pattern defined");
		} else {
			displayText.setText(String.format("Pattern: %s", inputTimePattern.getDefinitionString()));
		}
	}

	/**
	 * Set the {@link InputTimePattern} this Composite should display and edit.
	 * 
	 * @param inputTimePattern The {@link InputTimePattern} to set.
	 */
	public void setInputTimePattern(InputTimePattern inputTimePattern) {
		this.inputTimePattern = inputTimePattern;
		getDisplay().syncExec(new Runnable() {
			public void run() {
				updateLabel();
			}
		});
	}

	/**
	 * @return the current {@link InputTimePattern}, might be <code>null</code> if not yet initialized.
	 */
	public InputTimePattern getInputTimePattern() {
		return inputTimePattern;
	};

}
