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

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.composite.LabeledText;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.dialog.CenteredDialog;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.print.DocumentPrinterDelegateConfig;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class EditDocumentPrinterTypeRegsComposite extends XComposite {

	private List fileExtList;
	private XComposite buttonWrapper;
	private Button addDelegation;
	private Button removeDelegation;
	
	private Map<String, DocumentPrinterDelegateConfig> delegateConfigs = new HashMap<String, DocumentPrinterDelegateConfig>();
	
	/**
	 * @param parent
	 * @param style
	 */
	public EditDocumentPrinterTypeRegsComposite(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 */
	public EditDocumentPrinterTypeRegsComposite(Composite parent, int style,
			LayoutMode layoutMode) {
		super(parent, style, layoutMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public EditDocumentPrinterTypeRegsComposite(Composite parent, int style,
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
	public EditDocumentPrinterTypeRegsComposite(Composite parent, int style,
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
	public EditDocumentPrinterTypeRegsComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, int cols) {
		super(parent, style, layoutMode, layoutDataMode, cols);
		initGUI();
	}

	protected void initGUI() {
		getGridLayout().numColumns = 2;
		fileExtList = new List(this, SWT.BORDER);
		fileExtList.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		buttonWrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		buttonWrapper.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		addDelegation = new Button(buttonWrapper, SWT.PUSH);
		addDelegation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addDelegation.setText(Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterTypeRegsComposite.addDelegation.text")); //$NON-NLS-1$
		addDelegation.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				String fileExt = openFileExtDlg();
				if (fileExt != null && !"".equals(fileExt)) { //$NON-NLS-1$
					addTypeReg(fileExt);
					// TODO: Removed, as the SelectionListener does not get notified. Why not? 
//					fileExtList.setSelection(new String[]{fileExt});
				}
			}
		});
		
		removeDelegation = new Button(buttonWrapper, SWT.PUSH);
		removeDelegation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeDelegation.setText(Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterTypeRegsComposite.removeDelegation.text")); //$NON-NLS-1$
		removeDelegation.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				removeTypeReg(getSelectedFileExt());
			}
		});
	}
	
	public String getSelectedFileExt() {
		String[] selection = fileExtList.getSelection();
		if (selection.length > 0)
			return selection[0];
		else
			return null;
	}

	/**
	 * @param arg0
	 * @see org.eclipse.swt.widgets.Button#addSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void addSelectionListener(SelectionListener listener) {
		fileExtList.addSelectionListener(listener);
	}
	
	
	
	public void setDelegateConfigs(Map<String, DocumentPrinterDelegateConfig> _delegateConfigs) {
		delegateConfigs.clear();
		if (_delegateConfigs != null) {
			for (Entry<String, DocumentPrinterDelegateConfig> entry : _delegateConfigs.entrySet()) {
				delegateConfigs.put(entry.getKey(), entry.getValue());
			}
		}
		updateList();
	}
	
	public DocumentPrinterDelegateConfig getDelegateConfig(String fileExt) {
		return delegateConfigs.get(fileExt);
	}
	
	public boolean hasRegistration(String fileExt) {
		return delegateConfigs.containsKey(fileExt);
	}
	
	public Map<String, DocumentPrinterDelegateConfig> getDelegateConfigs() {
		return delegateConfigs;
	}

	public void setDelegateConfig(String fileExt, DocumentPrinterDelegateConfig delegateConfig) {
		delegateConfigs.put(fileExt, delegateConfig);
		updateList();
	}
	
	public void updateList() {
		String[] selection = fileExtList.getSelection();
		fileExtList.removeAll();
		SortedSet<String> keys = new TreeSet<String>();
		keys.addAll(delegateConfigs.keySet());
		for (String fileExt : keys) {
			fileExtList.add(fileExt);
		}
		fileExtList.setSelection(selection);
	}
	
	public void addTypeReg(String fileExt) {
		delegateConfigs.put(fileExt, null);
		updateList();
	}
	
	public void removeTypeReg(String fileExt) {
		delegateConfigs.remove(fileExt);
		updateList();
	}
	
	public static class FileExtDialog extends CenteredDialog {
		public LabeledText fileExtText;
		private String fileExt;
		public FileExtDialog(Shell parentShell) {
			super(parentShell);
		}
		@Override
		protected Control createDialogArea(Composite parent) {
			XComposite wrapper = new XComposite(parent, SWT.NONE);
			fileExtText = new LabeledText(wrapper, Messages.getString("org.nightlabs.base.print.pref.EditDocumentPrinterTypeRegsComposite.fileExtText.caption")); //$NON-NLS-1$
			fileExtText.getTextControl().addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent evt) {
					fileExt = fileExtText.getTextControl().getText();					
				}
			});
			return wrapper;
		}
		public String getFileExt() {
			return fileExt;
		}
	}

	public static String openFileExtDlg() {
		FileExtDialog dlg = new FileExtDialog(RCPUtil.getActiveWorkbenchShell());
		if (dlg.open() == Dialog.OK)
			return dlg.getFileExt();
		else
			return null;
	}
	
}
