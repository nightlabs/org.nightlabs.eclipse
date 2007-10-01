/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.dialog;

import java.awt.Font;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.editor2d.request.TextCreateRequest;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.util.FontUtil;

public class CreateTextDialog 
extends CenteredDialog
{  
	private static final Logger logger = Logger.getLogger(CreateTextDialog.class);
	
  public CreateTextDialog(Shell parentShell, TextCreateRequest request) 
  {
    super(parentShell);
    this.request = request;
    setShellStyle(getShellStyle() | SWT.RESIZE);
  }
  
  private TextCreateRequest request;  
  private int defaultSize = 18;
  private Combo fontCombo;
  private Text text;
  private Combo sizeCombo;
  private Button italicButton; 
  private Button boldButton;
  private String[] fonts;
  private Composite dialogComp;
  private Text previewText;
  
  protected Control createDialogArea(Composite parent) 
  {
    getShell().setText(Messages.getString("org.nightlabs.editor2d.dialog.CreateTextDialog.title")); //$NON-NLS-1$
    
    dialogComp = new XComposite(parent, SWT.NONE);    
    dialogComp.setLayout(new GridLayout(2, false));    
    
    // name
    Label nameLabel = new Label(dialogComp, SWT.NONE);
    nameLabel.setText(Messages.getString("org.nightlabs.editor2d.dialog.CreateTextDialog.name")); //$NON-NLS-1$
    text = new Text(dialogComp, SWT.BORDER);
    text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    text.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent evt) {
				updatePreview();
			}
		});
    
    // font
    Label fontLabel = new Label(dialogComp, SWT.NONE);
    fontLabel.setText(Messages.getString("org.nightlabs.editor2d.dialog.CreateTextDialog.font")); //$NON-NLS-1$
    Composite detailComp = new XComposite(dialogComp, SWT.NONE, 
    		LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 4);    
    
    createFontCombo(detailComp);
    createSizeCombo(detailComp);
    boldButton = new Button(detailComp, SWT.TOGGLE);
    boldButton.setText(Messages.getString("org.nightlabs.editor2d.dialog.CreateTextDialog.bold")); //$NON-NLS-1$
    italicButton = new Button(detailComp, SWT.TOGGLE);
    italicButton.setText(Messages.getString("org.nightlabs.editor2d.dialog.CreateTextDialog.italic")); //$NON-NLS-1$
    
    // preview
    Label previewLabel = new Label(dialogComp, SWT.NONE);
    previewLabel.setText(Messages.getString("org.nightlabs.editor2d.dialog.CreateTextDialog.preview")); //$NON-NLS-1$
    previewText = new Text(dialogComp, SWT.READ_ONLY | SWT.BORDER | SWT.WRAP);    
    previewText.setText(previewString);
    previewText.setLayoutData(new GridData(GridData.FILL_BOTH));
    
    fontCombo.addSelectionListener(previewListener);
    sizeCombo.addSelectionListener(previewListener);
    boldButton.addSelectionListener(previewListener);
    italicButton.addSelectionListener(previewListener);
    updatePreview();
    
    logger.info("getDefaultFont() = "+getDefaultFont());        //$NON-NLS-1$
    return dialogArea;
  }  
    
  protected void checkEmptyString() 
  {
  	if (getButton(Dialog.OK) != null) {
  		if (text.getText().trim().equals("")) //$NON-NLS-1$
  			getButton(Dialog.OK).setEnabled(false);
  		else
  			getButton(Dialog.OK).setEnabled(true);  		
  	}
  }
  
  private SelectionListener previewListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			updatePreview();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
  
	private String previewString = Messages.getString("org.nightlabs.editor2d.dialog.CreateTextDialog.previewString"); //$NON-NLS-1$
  protected void updatePreview() 
  {
  	checkEmptyString();
  	previewText.setText(text.getText());
		previewText.setFont(getSelectedFont());
		previewText.redraw();
		if (getDialogArea() != null)
			((Composite)getDialogArea()).layout(true, true);
  }
  
  protected void createFontCombo(Composite parent) 
  {
    fontCombo = new Combo(parent, SWT.READ_ONLY);
    fontCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));    
    
    fonts = FontUtil.getSystemFonts();
    fontCombo.setItems(fonts);  
    
    for (int i=0; i<fonts.length; i++) {
      String f = fonts[i];
      if (f.equals(getDefaultFont().getName())) {
        fontCombo.select(i);
      }
    }
    
    if (fontCombo.getSelectionIndex() < 0)
    	fontCombo.select(0);
  }
  
  protected String[] getFontSizes() {
    return FontUtil.getFontSizes();
  }
  
  protected Font getDefaultFont() {
  	FontData[] fontDatas = Display.getDefault().getSystemFont().getFontData();
  	FontData fontData = fontDatas[0];
  	return new Font(fontData.getName(), fontData.getStyle(), fontData.getHeight());
  }
  
  protected void createSizeCombo(Composite parent) 
  {
    sizeCombo = new Combo(parent, SWT.NONE);
    String[] sizes = getFontSizes();
    sizeCombo.setItems(sizes);
    
    for (int i=0; i<sizes.length; i++) {
      String f = sizes[i];
      if (f.equals(Integer.toString(defaultSize))) {
        sizeCombo.select(i);
      }
    }
    
    sizeCombo.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent evt) {
				String text = sizeCombo.getText();
				try {
					Integer.parseInt(text);
					getButton(Dialog.OK).setEnabled(true);
				} catch (NumberFormatException e) {
					getButton(Dialog.OK).setEnabled(false);
				}
			}
		});
  }
    
  public org.eclipse.swt.graphics.Font getSelectedFont() {
    int size = Integer.parseInt(sizeCombo.getText());    
    int fontIndex = fontCombo.getSelectionIndex();
    String fontName = fontCombo.getItem(fontIndex);
    int fontStyle = getSWTFontStyle();
    return new org.eclipse.swt.graphics.Font(Display.getDefault(),
    		fontName, size, fontStyle);
  }
  
  private int getFontStyle() 
  {
  	int fontStyle = Font.PLAIN;
  	
    if (boldButton.getSelection())
      fontStyle = Font.BOLD;
    
    if (italicButton.getSelection())
      fontStyle = fontStyle | Font.ITALIC;

    return fontStyle;
  }
  
  private int getSWTFontStyle() {
  	int fontStyle = SWT.NORMAL;
  	
    if (boldButton.getSelection())
      fontStyle = fontStyle | SWT.BOLD;
    
    if (italicButton.getSelection())
      fontStyle = fontStyle | SWT.ITALIC;

    return fontStyle;  	
  }
  
  protected void okPressed() 
  {
  	int fontStyle = getFontStyle();
    int size = Integer.parseInt(sizeCombo.getText());    
    int fontIndex = fontCombo.getSelectionIndex();
    String fontName = fontCombo.getItem(fontIndex);
    
    String text = this.text.getText();
    request.setText(text);
    request.setFontName(fontName);
    request.setFontSize(size);
    request.setFontStyle(fontStyle);
    
    super.okPressed();
  }

	@Override
	protected Control createContents(Composite parent) {
		Control ctrl = super.createContents(parent);
		getButton(Dialog.OK).setEnabled(false);
		return ctrl;
	}
}
