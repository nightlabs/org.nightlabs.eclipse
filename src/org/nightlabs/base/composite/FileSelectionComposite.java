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

package org.nightlabs.base.composite;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.util.RCPUtil;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class FileSelectionComposite 
	extends XComposite 
{
	public static final int OPEN_FILE = 1;
	public static final int OPEN_DIR = 1 << 1;
	
	/**
	 * @param parent
	 * @param compositeStyle
	 */
	public FileSelectionComposite(Composite parent, int compositeStyle, int dialogStyle, 
			String fileDialogCaption, String dirDialogCaption) 
	{
		this(parent, compositeStyle, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA, dialogStyle, 
				fileDialogCaption, dirDialogCaption);
	}
	
	/**
	 * @param parent
	 * @param compositeStyle
	 */
	public FileSelectionComposite(Composite parent, int compositeStyle, LayoutMode layoutMode, 
			LayoutDataMode layoutDataMode, int dialogStyle, String fileDialogCaption, String dirDialogCaption) 
	{
		super(parent, compositeStyle, layoutMode, layoutDataMode);
		createContents(dialogStyle, fileDialogCaption, dirDialogCaption);
	}	
	
	private Text fileTextForFiles;
	private Button browseButtonForFiles;
	private Text fileTextForFolders;
	private Button browseButtonForFolders;
	private String fileText = ""; //$NON-NLS-1$
	
	private boolean updating = false;
	
	private void createContents(int dialogStyle, String fileSelectionCaption, 
			String dirSelectionCaption) 
	{
		if ((OPEN_DIR & dialogStyle) != 0) {
			if (dirSelectionCaption != null)
				new Label(this, SWT.NONE).setText(dirSelectionCaption); //$NON-NLS-1$
			
			XComposite fileComp = new XComposite(this, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);		
			fileComp.getGridLayout().numColumns = 3;
			fileTextForFolders = new Text(fileComp, fileComp.getBorderStyle());
			fileTextForFolders.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fileTextForFolders.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e)
				{
					if (updating)
						return;
					fileText = fileTextForFolders.getText();
					FileSelectionComposite.this.modifyText(e);
				}
			});

			browseButtonForFolders = new Button(fileComp, SWT.PUSH);
			browseButtonForFolders.setText(Messages.getString("org.nightlabs.base.composite.FileSelectComposite.browseButton.text")); //$NON-NLS-1$
			browseButtonForFolders.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					DirectoryDialog fileDialog = new DirectoryDialog(RCPUtil.getActiveWorkbenchShell());
					setUpFileDialog(fileDialog);
					String selectedFile = fileDialog.open();
					if (selectedFile != null) {
						fileTextForFolders.setText(selectedFile);
						if (fileTextForFiles != null) {
							updating = true;
							fileTextForFiles.setText("");
							updating = false;
						}
					}
				}
			});
			
		} // ((OPEN_DIR & dialogStyle) != 0)
		
		if ((OPEN_FILE & dialogStyle) != 0) {
			if (fileSelectionCaption != null)
				new Label(this, SWT.NONE).setText(fileSelectionCaption); //$NON-NLS-1$

			XComposite fileCompFile = new XComposite(this, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);		
			fileCompFile.getGridLayout().numColumns = 3;
			fileTextForFiles = new Text(fileCompFile, fileCompFile.getBorderStyle());
			fileTextForFiles.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fileTextForFiles.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e)
				{
					if (updating)
						return;
					fileText = fileTextForFiles.getText();
					FileSelectionComposite.this.modifyText(e);
				}
			});


			browseButtonForFiles = new Button(fileCompFile, SWT.PUSH);
			browseButtonForFiles.setText(Messages.getString("org.nightlabs.base.composite.FileSelectComposite.browseButton.text")); //$NON-NLS-1$
			browseButtonForFiles.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell());
					setUpFileDialog(fileDialog);
					String selectedFile = fileDialog.open();
					if (selectedFile != null) {
						fileTextForFiles.setText(selectedFile);
						if (fileTextForFolders != null) {
							updating = true;
							fileTextForFolders.setText("");
							updating = false;
						}
					}
				}
			});
		} // ((OPEN_FILE & dialogStyle) != 0)
		
	}
	
	private List<String> filterNames;
	private List<String> filterExtensions;
	private String filterPath;
	private boolean filtersInitialised = false;
	
	/**
	 * Depending on the type of dialog, the filters from {@link #doSetUpFileDialog(List, List)} are
	 * set if possible.
	 * 
	 * @param dialog the file or directory dialog.
	 */
	private void setUpFileDialog(Dialog dialog) {
		if (! filtersInitialised) {
			filterNames = new LinkedList<String>();
			filterExtensions = new LinkedList<String>();
			filterPath = doSetUpFileDialog(filterNames, filterExtensions);
		}
			
		if (dialog instanceof FileDialog) {
			FileDialog fileDialog = (FileDialog) dialog;
			fileDialog.setFilterNames(filterNames.toArray(new String[filterNames.size()]));
			fileDialog.setFilterExtensions(filterExtensions.toArray(new String[filterExtensions.size()]));
			fileDialog.setFilterPath(filterPath);
			
		} else if (dialog instanceof DirectoryDialog) {
			DirectoryDialog directoryDialog = (DirectoryDialog) dialog;
			directoryDialog.setFilterPath(filterPath);
		}
	}
	
	/**
	 * Override this method to setup up specific Filters for files or a filter path.
	 * Default implementation adds '*.*' filter for files.
	 * <p>Note: 
	 * <ul>
	 *  <li>The <code>i</code>th filterName is the description of the <code>i</code>th filter.</li>
	 * 	<li>The <code>filterExtensions</code> and their corresponding names are ignored in case of an
	 * 			DirectoryDialog.</li>
	 * </ul> 
	 * </p>
	 * 
	 * @param filterNames the names of the filters given in <code>filterExtensions</code>.
	 * @param filterExtensions the extensions of files that shall be shown.
	 * @return the path from which to start showing files/folders. 
	 */
	protected String doSetUpFileDialog(List<String> filterNames, List<String> filterExtensions) {
		filterNames.add(Messages.getString("org.nightlabs.base.composite.FileSelectComposite.filterName_allFiles")); //$NON-NLS-1$
		filterExtensions.add("*.*");
		return null;
	}
	
	public String getFileText() {
		return fileText;
	}
	
	public File getFile() {
		return new File(getFileText());
	}
	
	public void setFileText(String fileText) {
		updating = true;
		try {
			this.fileText = fileText;
			if (fileTextForFiles != null && (!fileTextForFiles.isDisposed()))
				if (fileText != null)
					fileTextForFiles.setText(fileText);
		}
		finally {
			updating = false;
		}
	}
	
	protected abstract void modifyText(ModifyEvent e);

	public Text getFileTextControl() {
		return fileTextForFiles;
	}
}
