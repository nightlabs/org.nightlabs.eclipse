/* ************************************************************************
 * org.nightlabs.eclipse.ui.fckeditor - Eclipse RCP FCKeditor Integration *
 * Copyright (C) 2008 NightLabs - http://NightLabs.org                    *
 *                                                                        *
 * This library is free software; you can redistribute it and/or          *
 * modify it under the terms of the GNU Lesser General Public             *
 * License as published by the Free Software Foundation; either           *
 * version 2.1 of the License, or (at your option) any later version.     *
 *                                                                        *
 * This library is distributed in the hope that it will be useful,        *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU      *
 * Lesser General Public License for more details.                        *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with this library; if not, write to the                  *
 *     Free Software Foundation, Inc.,                                    *
 *     51 Franklin St, Fifth Floor,                                       *
 *     Boston, MA  02110-1301  USA                                        *
 *                                                                        *
 * Or get it online:                                                      *
 *     http://www.gnu.org/copyleft/lesser.html                            *
 **************************************************************************/
package org.nightlabs.eclipse.ui.fckeditor.file;


import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public class FileListEntry extends Composite implements IImageCallback
{
	private IFCKEditorContentFile file;
	IImageProvider imageProvider;
	private Label imageLabel;
	private List<IAction> actions;

	public FileListEntry(Composite parent, int style, IFCKEditorContentFile file, IImageProvider imageProvider, List<IAction> actions)
	{
		super(parent, style);
		this.file = file;
		this.imageProvider = imageProvider;
		this.actions = actions;
		createContents();
	}

	protected void createContents()
	{
		setBackground(getParent().getBackground());
		int cols = actions == null || actions.isEmpty() ? 2 : 3;
		GridLayout gridLayout = new GridLayout(cols, false);
		gridLayout.horizontalSpacing = 20;
		setLayout(gridLayout);

		Composite imageWrapper = new Composite(this, SWT.NONE);
		imageWrapper.setBackground(imageWrapper.getParent().getBackground());
		gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		imageWrapper.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		gridData.widthHint = imageProvider.getThumbnailSize();
		gridData.heightHint = imageProvider.getThumbnailSize();
		imageWrapper.setLayoutData(gridData);

		imageLabel = new Label(imageWrapper, SWT.NONE);
		imageLabel.setBackground(imageLabel.getParent().getBackground());
		imageLabel.setImage(imageProvider.getImage(file, this));
		gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
//		gridData.widthHint = imageProvider.getThumbnailSize();
//		gridData.heightHint = imageProvider.getThumbnailSize();
		imageLabel.setLayoutData(gridData);

		Composite right = new Composite(this, SWT.NONE);
		right.setBackground(right.getParent().getBackground());
		gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		right.setLayout(gridLayout);
		gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		right.setLayoutData(gridData);

		createEntry(right, Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileListEntry.nameLabelText"), file.getName()); //$NON-NLS-1$
		createEntry(right, Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileListEntry.typeLabelText"), file.getContentType()); //$NON-NLS-1$
		createEntry(right, Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileListEntry.sizeLabelText"), String.format(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileListEntry.sizeText"), file.getData().length)); //$NON-NLS-1$ //$NON-NLS-2$
		createEntry(right, Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileListEntry.descriptionLabelText"), file.getDescription()); //$NON-NLS-1$

//		Label nameLabelLabel = new Label(right, SWT.NONE);
//		nameLabelLabel.setBackground(right.getParent().getBackground());
//		nameLabelLabel.setText("Name:");
//		Label nameLabel = new Label(right, SWT.NONE);
//		nameLabel.setBackground(right.getParent().getBackground());
//		nameLabel.setText(file.getName());
//		gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
//		nameLabel.setLayoutData(gridData);
//
//		Label contentTypeLabelLabel = new Label(right, SWT.NONE);
//		contentTypeLabelLabel.setBackground(right.getParent().getBackground());
//		contentTypeLabelLabel.setText("Type:");
//		Label contentTypeLabel = new Label(right, SWT.NONE);
//		contentTypeLabel.setBackground(right.getParent().getBackground());
//		contentTypeLabel.setText(file.getContentType());
//		gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
//		contentTypeLabel.setLayoutData(gridData);
//
//		Label sizeLabelLabel = new Label(right, SWT.NONE);
//		sizeLabelLabel.setBackground(right.getParent().getBackground());
//		sizeLabelLabel.setText("Size:");
//		Label sizeLabel = new Label(right, SWT.NONE);
//		sizeLabel.setBackground(right.getParent().getBackground());
//		sizeLabel.setText(String.format("%d bytes", file.getData().length));
//		gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
//		sizeLabel.setLayoutData(gridData);

		if(actions != null && !actions.isEmpty())
			createActions(this);
	}

	private void createActions(Composite parent)
	{
		Composite buttonWrapper = new Composite(parent, SWT.NONE);
		buttonWrapper.setBackground(buttonWrapper.getParent().getBackground());
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		buttonWrapper.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.END, SWT.BEGINNING, false, false);
		buttonWrapper.setLayoutData(gridData);

		for (final IAction action : actions) {
			Button b = new Button(buttonWrapper, SWT.PUSH);
			b.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			b.setText(action.getText());
			b.setToolTipText(action.getToolTipText());
			b.setEnabled(action.isEnabled());
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					Event eventToFire = new Event();
					eventToFire.data = file;
					eventToFire.detail = e.detail;
					eventToFire.display = e.display;
					eventToFire.doit = e.doit;
					eventToFire.height = e.height;
					eventToFire.item = e.item;
					eventToFire.stateMask = e.stateMask;
					eventToFire.text = e.text;
					eventToFire.time = e.time;
					eventToFire.widget = e.widget;
					eventToFire.width = e.width;
					eventToFire.x = e.x;
					eventToFire.y = e.y;
					action.runWithEvent(eventToFire);
				}
			});
		}
	}

	private void createEntry(Composite parent, String label, String value)
	{
		Label labelLabel = new Label(parent, SWT.NONE);
		labelLabel.setBackground(parent.getParent().getBackground());
		labelLabel.setText(label);
		Label valueLabel = new Label(parent, SWT.NONE | SWT.WRAP);
		valueLabel.setBackground(parent.getParent().getBackground());
		valueLabel.setText(value == null ? "" : value); //$NON-NLS-1$
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		valueLabel.setLayoutData(gridData);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IImageCallback#updateImage(org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile, org.eclipse.swt.graphics.Image)
	 */
	@Override
	public void updateImage(IFCKEditorContentFile file, final Image image)
	{
		imageLabel.setImage(image);
	}
}