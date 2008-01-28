/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.ui.composite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Chairat Kongarayawetchakun <chairat[AT]nightlabs[DOT]de>
 *
 */
public class FileListSelectionComposite 
extends XComposite {
	private Map<String, InputStream> fileInputStreamMap = new HashMap<String, InputStream>();
	private org.eclipse.swt.widgets.List fileListWidget;

	private int type;

	public static int ADD = 1;
	public static int LIST = 2;

	public FileListSelectionComposite(Composite parent, int compositeStyle, LayoutMode layoutMode, int type) {
		super(parent, compositeStyle, layoutMode);
		this.type = type;
		createContents();
	}	

	private void createContents() {
		XComposite fileListComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		fileListComposite.getGridLayout().numColumns = 2;

		fileListWidget = new org.eclipse.swt.widgets.List(fileListComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		fileListWidget.setLayoutData(gridData);

		XComposite buttonComposite = new  XComposite(fileListComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		if(type == ADD) {
			Button addButton = new Button(buttonComposite, SWT.PUSH);
			addButton.setText("Add");
			addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			addButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell(), SWT.OPEN);
					String selectedFile = fileDialog.open();
					if (selectedFile != null) {
						File file = getFile(selectedFile);
						try {
							fileInputStreamMap.put(file.getName(), new FileInputStream(file));
						} catch (FileNotFoundException e1) {
							throw new RuntimeException(e1);
						}
						fileListWidget.add(file.getName());
					}
				}
			});

			Button removeButton = new Button(buttonComposite, SWT.PUSH);
			removeButton.setText("Remove");
			removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			removeButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					fileInputStreamMap.remove(fileListWidget.getItem(fileListWidget.getSelectionIndex()));
					fileListWidget.remove(fileListWidget.getSelectionIndex());
				}
			});
		}
		else {
			Button download = new Button(buttonComposite, SWT.PUSH);
			download.setText("Download");
			download.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			download.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					if (fileListWidget.getSelectionIndex() != -1) {
						InputStream is = fileInputStreamMap.get(fileListWidget.getItem(fileListWidget.getSelectionIndex()));
						if (is != null) {
							try {
								FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell(), SWT.SAVE);
								String selectedFile = fileDialog.open();
								setFile(is, selectedFile);
							} catch (Exception ex) {
								throw new RuntimeException(ex);
							}
						}
					}
				}
			});
		}
		
		buttonComposite.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		fileListComposite.setLayoutData(gridData);
	}

	public void setInputStreamMap(Map<String, InputStream> iMap){
		fileListWidget.removeAll();
		this.fileInputStreamMap = iMap;
		for(String name : iMap.keySet()) {
			fileListWidget.add(name);
		}
	}

	public void addFile(String fileName, InputStream is) {
		fileInputStreamMap.put(fileName, is);
		fileListWidget.add(fileName);
	}
	
	public boolean removeFile(String fileName) {
		if (fileInputStreamMap.containsKey(fileName)) {
			fileInputStreamMap.remove(fileName);
			fileListWidget.remove(fileName);
			return true;
		}
		return false;
	}
	
	public boolean removeFiles(String[] fileNames) {
		boolean result = false;
		if (fileNames != null) {
			for (int i = 0; i < fileNames.length; i++) {
				result = result | removeFile(fileNames[i]);
			}
		}
		return result;
	}
	
	public List<FileInputStream> getFileInputStreamList() {
		Collection<FileInputStream> c = CollectionUtil.castCollection(fileInputStreamMap.values());
		List<FileInputStream> l = new ArrayList<FileInputStream>(c);
		return l;
	}

	public File getFile(String fileText) {
		return new File(fileText);
	}

	public Map<String, InputStream> getInputStreamMap() {
		return fileInputStreamMap;
	}
	
    public void setFile(InputStream io, String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        byte[] buf = new byte[256];
        int read = 0;
        while ((read = io.read(buf)) > 0) {
            fos.write(buf, 0, read);
        }
    }
    
    public org.eclipse.swt.widgets.List getFileListWidget() {
		return fileListWidget;
	}
}
