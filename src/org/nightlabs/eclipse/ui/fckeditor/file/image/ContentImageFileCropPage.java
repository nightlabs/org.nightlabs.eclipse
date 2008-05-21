// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file.image;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.file.ClippingAreaListener;
import org.nightlabs.eclipse.ui.fckeditor.file.ContentTypeUtil;
import org.nightlabs.eclipse.ui.fckeditor.file.ImageClippingArea;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ContentImageFileCropPage extends WizardPage
{
	private Text targetWidth;
	private Text targetHeight;
	private Text selectionWidth;
	private Text selectionHeight;
	private Text originalWidth;
	private Text originalHeight;
	private Text scaleText;
	private Scale scale;

	private ImageClippingArea imageView;
//	private Image image;
	private ImageData imageData;

	private File sourceFile;
	private byte[] imageBinaryData;
	private String mimeType;

	/**
	 * Create a new ContentImageFileCropPage instance.
	 */
	public ContentImageFileCropPage()
	{
		this(ContentImageFileCropPage.class.getName(), "Resize and Crop the Image", null);
	}

	/**
	 * Create a new ContentImageFileCropPage instance.
	 */
	public ContentImageFileCropPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
	}

	public void setSourceFile(File sourceFile)
	{
		this.sourceFile = sourceFile;
		applySourceFile();
	}

	private void applySourceFile()
	{
		if(sourceFile != null && imageView != null) {
			try {
				imageData = ImageUtil.loadImage(sourceFile, new NullProgressMonitor());
			} catch (Exception e) {
				Activator.err("Loading image failed", e);
				MessageDialog.openError(getShell(), "Error", "Loading image failed: "+e.getLocalizedMessage());
			}
//			FileInputStream in = null;
//			try {
//				in = new FileInputStream(sourceFile);
//				ImageLoader imageLoader = new ImageLoader();
//				imageData = imageLoader.load(in)[0];
//			} catch(IOException e) {
//				// TODO
//				throw new RuntimeException(e);
//			} finally {
//				if(in != null)
//					try {	in.close();	} catch (IOException e) {}
//			}
			imageView.setSourceImage(imageData);
			String width = String.valueOf(imageData.width);
			String height = String.valueOf(imageData.height);
			originalWidth.setText(width);
			originalHeight.setText(height);
			selectionWidth.setText(width);
			selectionHeight.setText(height);
			scaleText.setText("100");
			scaleText.setEnabled(true);
			scale.setSelection(100);
			scale.setEnabled(true);
			targetWidth.setText(width);
			targetHeight.setText(height);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayoutData(gridData);

		Label originalSizeLabel = new Label(composite, SWT.NONE);
		originalSizeLabel.setText("Original size:");
		originalWidth = new Text(composite, SWT.BORDER);
		originalWidth.setEnabled(false);
		new Label(composite, SWT.NONE).setText("x");
		originalHeight = new Text(composite, SWT.BORDER);
		originalHeight.setEnabled(false);

		Label selectionSizeLabel = new Label(composite, SWT.NONE);
		selectionSizeLabel.setText("Selection size:");
		selectionWidth = new Text(composite, SWT.BORDER);
		selectionWidth.setEnabled(false);
		new Label(composite, SWT.NONE).setText("x");
		selectionHeight = new Text(composite, SWT.BORDER);
		selectionHeight.setEnabled(false);

		Label scaleLabel = new Label(composite, SWT.NONE);
		scaleLabel.setText("Scale:");

		scaleText = new Text(composite, SWT.BORDER);
		scaleText.setEnabled(false);

		new Label(composite, SWT.NONE).setText("%");

		scale = new Scale(composite, SWT.NONE);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		scale.setMinimum(1);
		scale.setMaximum(1000);
		scale.setPageIncrement(10);
		scale.setEnabled(false);

		Label targetSizeLabel = new Label(composite, SWT.NONE);
		targetSizeLabel.setText("Target size:");
		targetWidth = new Text(composite, SWT.BORDER);
		targetWidth.setEnabled(false);
		new Label(composite, SWT.NONE).setText("x");
		targetHeight = new Text(composite, SWT.BORDER);
		targetHeight.setEnabled(false);

		createImageView(composite);

		applySourceFile();

		setControl(composite);

		scaleText.addModifyListener(new ModifyListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e)
			{
				scale.setSelection(Integer.parseInt(scaleText.getText()));
				applyScale();
			}
		});

		scale.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				scaleText.setText(String.valueOf(scale.getSelection()));
			}
		});

	}

	private Control createImageView(Composite composite)
	{
		imageView = new ImageClippingArea(composite, SWT.NONE);
		imageView.addClippingAreaListener(new ClippingAreaListener() {
			@Override
			public void clippingAreaChanged(Rectangle newClippingArea)
			{
				selectionWidth.setText(String.valueOf(newClippingArea.width));
				selectionHeight.setText(String.valueOf(newClippingArea.height));
				applyScale();
			}
		});
//		imageView.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		imageView.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 4;
		gridData.widthHint = 300;
		gridData.heightHint = 300;
		imageView.setLayoutData(gridData);

		return imageView;
	}

	private void applyScale()
	{
		int x = Integer.parseInt(scaleText.getText());
		targetWidth.setText(String.valueOf(Math.round(Integer.parseInt(selectionWidth.getText()) * (x / 100f))));
		targetHeight.setText(String.valueOf(Math.round(Integer.parseInt(selectionHeight.getText()) * (x / 100f))));
	}

	public boolean performFinish(IProgressMonitor monitor)
	{
		monitor.beginTask("Preparing image", 4);
		monitor.subTask("Cropping");

		Rectangle clippingArea = imageView.getClippingAreaForSource();
		if(clippingArea.x != 0 || clippingArea.y != 0 || clippingArea.width != imageData.width || clippingArea.height != imageData.height) {
			imageData = crop(imageData, clippingArea.x, clippingArea.y, clippingArea.width, clippingArea.height);
		}
		monitor.worked(1);
		monitor.subTask("Scaling");
		int x = Integer.parseInt(scaleText.getText());
		if(x != 100) {
			imageData = imageData.scaledTo(
							Math.round(clippingArea.width * (x / 100f)),
							Math.round(clippingArea.height * (x / 100f)));
		}
		monitor.worked(1);

		monitor.subTask("Saving");
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		mimeType = ContentTypeUtil.getContentType(sourceFile.getName());
		if(!ContentTypeUtil.IMAGE_JPEG.equals(mimeType)) {
			mimeType = ContentTypeUtil.IMAGE_PNG;
		}
		try {
			ImageUtil.saveImage(imageData, mimeType, out, new SubProgressMonitor(monitor, 2));
		} catch (Exception e) {
			Activator.err("Saving image failed", e);
			MessageDialog.openError(getShell(), "Error", "Saving image failed: "+e.getLocalizedMessage());
			monitor.done();
			return false;
		}
		imageBinaryData = out.toByteArray();

//		ImageLoader imageLoader = new ImageLoader();
//		imageLoader.data = new ImageData[] { imageData };
//		mimeType = ContentTypeUtil.getContentType(sourceFile.getName());
//		if(ContentTypeUtil.IMAGE_JPEG.equals(mimeType)) {
//			imageLoader.save(out, SWT.IMAGE_JPEG);
//		} else {
//			mimeType = ContentTypeUtil.IMAGE_PNG;
//			imageLoader.save(out, SWT.IMAGE_PNG);
//		}
//		imageBinaryData = out.toByteArray();
//		monitor.worked(2);

		monitor.done();

		return true;
	}

	private ImageData crop(ImageData srcData, int x, int y, int w, int h)
	{
		Image src = new Image(getShell().getDisplay(), srcData);
		Image cropImage = new Image(getShell().getDisplay(), w, h);

		// Redefine w and h to void them to be too big
		if (x+w > src.getBounds().width) {
			w = src.getBounds().width - x;
		}
		if (y+h > src.getBounds().height) {
			h = src.getBounds().height - y;
		}

		GC cropGC = new GC(cropImage);
		cropGC.drawImage(src,
				x, y,
				w, h,
				0, 0,
				w, h);
		cropGC.dispose();
		src.dispose();

		ImageData cropImageData = cropImage.getImageData();
		cropImage.dispose();
		return cropImageData;
	}

	public byte[] getBinaryImageData()
	{
		return imageBinaryData;
	}

	public String getMimeType()
	{
		return mimeType;
	}
}
