/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.print.pref;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.composite.AbstractListComposite;
import org.nightlabs.base.composite.LabeledText;
import org.nightlabs.base.composite.XComboComposite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.print.DocumentPrinterDelegateConfig;
import org.nightlabs.print.DelegatingDocumentPrinterCfMod.ExternalEngineDelegateConfig;
import org.nightlabs.print.DelegatingDocumentPrinterCfMod.SystemCallDelegateConfig;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EditDocumentPrinterConfigComposite extends XComposite {

	private XComposite typeWrapper;
	private Group typeGroup;
	private Button typeSysCall;
	private Button typeExtEngine;
	
	private Composite editWrapper;
	private StackLayout stackLayout;	
	private XComposite sysCallEditComposite;
	private XComposite firstLine;
	private LabeledText expectedReturnValue;
	private AbstractListComposite<String> templates;
	private LabeledText commandPattern;
	private LabeledText parameterPattern;
	
	private XComposite extEngineEditComposite;
	private LabeledText className;
	
	/**
	 * @param parent
	 * @param style
	 */
	public EditDocumentPrinterConfigComposite(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 */
	public EditDocumentPrinterConfigComposite(Composite parent, int style,
			LayoutMode layoutMode) {
		super(parent, style, layoutMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public EditDocumentPrinterConfigComposite(Composite parent, int style,
			LayoutDataMode layoutDataMode) {
		super(parent, style, layoutDataMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public EditDocumentPrinterConfigComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param cols
	 */
	public EditDocumentPrinterConfigComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, int cols) {
		super(parent, style, layoutMode, layoutDataMode, cols);
		initGUI();
	}

	private static final String ACROBAT_WINDOWS = "Acrobat Reader Windows /t"; //$NON-NLS-1$
	private static final String ACROBAT_WINDOWS_ONLY_DEFAULT = "Acrobat Reader Windows /p/h (Only default printer)"; //$NON-NLS-1$
	
	private static final List<String> options;
	static {
		 options = new LinkedList<String>();
		 options.add(ACROBAT_WINDOWS);
		 options.add(ACROBAT_WINDOWS_ONLY_DEFAULT);
	}
	
	protected void initGUI() {
		typeWrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		typeGroup = new Group(typeWrapper, SWT.NONE);
		typeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeGroup.setText(Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterConfigComposite.typeGroup.text")); //$NON-NLS-1$
		typeGroup.setLayout(new RowLayout(SWT.VERTICAL));

		typeSysCall = new Button(typeGroup, SWT.RADIO);
		typeSysCall.setText(Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterConfigComposite.typeSysCall.text")); //$NON-NLS-1$
		typeSysCall.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				updateTypeView();
			}
		});
		typeSysCall.setSelection(true);

		typeExtEngine = new Button(typeGroup, SWT.RADIO);
		typeExtEngine.setText(Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterConfigComposite.typeExtEngine.text")); //$NON-NLS-1$
		typeExtEngine.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				updateTypeView();
			}
		});

		editWrapper = new Composite(this, SWT.NONE);
		stackLayout = new StackLayout();
		editWrapper.setLayout(stackLayout);
		editWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));

		sysCallEditComposite = new XComposite(editWrapper, SWT.NONE);
		firstLine = new XComposite(sysCallEditComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		firstLine.getGridData().grabExcessVerticalSpace = false;
		firstLine.getGridLayout().numColumns = 2;
		expectedReturnValue = new LabeledText(firstLine, Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterConfigComposite.expectedReturnValue.caption")); //$NON-NLS-1$
		templates = new XComboComposite<String>(firstLine, XComboComposite.getDefaultWidgetStyle(firstLine), Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterConfigComposite.templates.caption")); //$NON-NLS-1$
		for (String option : options) {
			templates.addElement(option);
		}

		templates.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				String selected = templates.getSelectedElement();
				if ( ACROBAT_WINDOWS.equals(selected) ) {
					expectedReturnValue.setText("1"); //$NON-NLS-1$
					commandPattern.setText("C:\\Programme\\Adobe\\Acrobat 7.0\\Reader\\AcroRd32.exe"); //$NON-NLS-1$
					parameterPattern.setText("/t ${FILE} ${$PRINTSERVICE}"); //$NON-NLS-1$
				} else if ( ACROBAT_WINDOWS_ONLY_DEFAULT.equals(selected) ){
					expectedReturnValue.setText("1"); //$NON-NLS-1$
					commandPattern.setText("C:\\Programme\\Adobe\\Acrobat 7.0\\Reader\\AcroRd32.exe"); //$NON-NLS-1$
					parameterPattern.setText("/h/p ${FILE}"); //$NON-NLS-1$
				} 
				else
					throw new IllegalStateException("Some bogus was selected"); //$NON-NLS-1$
			}
		});

		commandPattern = new LabeledText(sysCallEditComposite, Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterConfigComposite.commandPattern.caption")); //$NON-NLS-1$
		parameterPattern = new LabeledText(sysCallEditComposite, Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterConfigComposite.parameterPattern.caption")); //$NON-NLS-1$

		extEngineEditComposite = new XComposite(editWrapper, SWT.NONE);
		className = new LabeledText(extEngineEditComposite, Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterConfigComposite.className.caption")); //$NON-NLS-1$
		stackLayout.topControl = sysCallEditComposite;
	}

	private void clearAll() {
		typeSysCall.setSelection(false);
		typeExtEngine.setSelection(false);
		expectedReturnValue.setText(""); //$NON-NLS-1$
		commandPattern.getTextControl().setText(""); //$NON-NLS-1$
		parameterPattern.getTextControl().setText(""); //$NON-NLS-1$
		className.getTextControl().setText(""); //$NON-NLS-1$
	}
	
	private void updateTypeView() {
		if (typeSysCall.getSelection())
			stackLayout.topControl = sysCallEditComposite;
		else if (typeExtEngine.getSelection())
			stackLayout.topControl = extEngineEditComposite;
		editWrapper.layout(true, true);
	}
	
	public void setDelegateConfig(DocumentPrinterDelegateConfig printerConfig) {
		clearAll();
		if (printerConfig != null) {
			if (printerConfig instanceof SystemCallDelegateConfig) {
				typeSysCall.setSelection(true);
				SystemCallDelegateConfig config = (SystemCallDelegateConfig)printerConfig;
				expectedReturnValue.setText(Integer.toString(config.getExpectedReturnValue()));
				if (config.getCommandPattern() != null)
					commandPattern.getTextControl().setText(config.getCommandPattern());
				if (config.getParameterPattern() != null)
					parameterPattern.getTextControl().setText(config.getParameterPattern());
			}
			else if (printerConfig instanceof ExternalEngineDelegateConfig) {
				ExternalEngineDelegateConfig config = (ExternalEngineDelegateConfig) printerConfig;
				typeExtEngine.setSelection(true);
				if (config.getClassName() != null)
					className.getTextControl().setText(config.getClassName());
			}
			updateTypeView();
		}
	}
	
	public DocumentPrinterDelegateConfig readDelegateConfig() {
		DocumentPrinterDelegateConfig result = null;
		if (typeSysCall.getSelection()) {
			result = new SystemCallDelegateConfig();
			((SystemCallDelegateConfig)result).setExpectedReturnValue(Integer.parseInt(expectedReturnValue.getText()));
			((SystemCallDelegateConfig)result).setCommandPattern(commandPattern.getTextControl().getText());
			((SystemCallDelegateConfig)result).setParameterPattern(parameterPattern.getTextControl().getText());
		}
		else if (typeExtEngine.getSelection()) {
			result = new ExternalEngineDelegateConfig();
			((ExternalEngineDelegateConfig)result).setClassName(className.getTextControl().getText());
		}
		return result;
	}
	
}
