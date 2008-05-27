package org.nightlabs.eclipse.ui.fckeditor.file.image;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.file.ContentFileBasePage;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ContentImageFileBasePage extends ContentFileBasePage implements DisposeListener
{
	private Image previewImage;
	private Label imageLabel;
	private boolean disposeListenerRegistered;

	public ContentImageFileBasePage()
	{
		super(ContentImageFileBasePage.class.getName(), Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ContentImageFileBasePage.pageTitle"), null); //$NON-NLS-1$
	}

	@Override
	public void widgetDisposed(DisposeEvent e)
	{
		if(previewImage != null) {
			previewImage.dispose();
			previewImage = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.ContentFileBasePage#applySourceFile()
	 */
	@Override
	protected void applySourceFile()
	{
		super.applySourceFile();
		if(getSourceFile() != null && imageLabel != null) {
			imageLabel.setImage(null);
			if(previewImage != null) {
				previewImage.dispose();
				previewImage = null;
			}
			try {
				ImageData id = ImageUtil.loadImage(getSourceFile(), new NullProgressMonitor());
				//ImageData id = new ImageData(new FileInputStream(getSourceFile()));
				float m = Math.max(id.width / 200f, id.height / 200f);
				int width = Math.round(id.width / m);
				int height = Math.round(id.height / m);
				previewImage = new Image(getShell().getDisplay(), id.scaledTo(width, height));
				imageLabel.setImage(previewImage);
				if(!disposeListenerRegistered) {
					getShell().addDisposeListener(this);
					disposeListenerRegistered = true;
				}
			} catch (Exception e) {
				Activator.err("Error loading image preview", e); //$NON-NLS-1$
				previewImage = null;
				imageLabel.setText(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ContentImageFileBasePage.previewLoadingErrorText")); //$NON-NLS-1$
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.ContentFileBasePage#createCustomControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createCustomControls(Composite parent)
	{
		Label l = new Label(parent, SWT.NONE);
		l.setText(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ContentImageFileBasePage.previewLabelText")); //$NON-NLS-1$
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		imageLabel = new Label(parent, SWT.NONE);
//		GridData gd = new GridData();
//		gd.widthHint = 200;
//		gd.heightHint = 200;
//		gd.horizontalSpan = 2;
//		imageLabel.setLayoutData(gd);
	}
}
