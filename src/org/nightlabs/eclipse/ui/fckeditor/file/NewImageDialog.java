package org.nightlabs.eclipse.ui.fckeditor.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class NewImageDialog extends TitleAreaDialog
{
	private Text fileText;
	private Button browseButton;
	private Text targetWidth;
	private Text targetHeight;
	private Text selectionWidth;
	private Text selectionHeight;
	private Text originalWidth;
	private Text originalHeight;
	private Text scaleText;
	private Scale scale;

	private ImageClippingArea imageView;
	private Image image;
	private String filename;

	/**
	 * Create a new NewImageDialog instance.
	 * @param parentShell
	 */
	public NewImageDialog(Shell parentShell)
	{
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText("New Image");
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		// create a composite with standard margins and spacing
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);

		createFileInput(composite);
		createResizeArea(composite);

		setTitle("Add a new image");
		applyFile();

		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	protected void applyFile()
	{
		if(image != null) {
			image.dispose();
			image = null;
		}
		File f = new File(fileText.getText());
		if(!f.isFile()) {
			setErrorMessage("Please enter an existing file path or click the '...' button to choose a file.");
			return;
		}

		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
			image = new Image(getShell().getDisplay(), in);
			imageView.setSourceImage(image);
			String width = String.valueOf(image.getImageData().width);
			String height = String.valueOf(image.getImageData().height);
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
			setErrorMessage(null);
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {
			try {
				if(in != null) in.close();
			} catch (IOException e1) {}
		}
	}

	protected void browseButtonPressed()
	{
		FileDialog fileDialog = new FileDialog(browseButton.getShell());
		fileDialog.setText("Open File");
		String filepath = fileDialog.open();
		if(filepath != null) {
			fileText.setText(filepath);
			applyFile();
		}
	}

	protected Control createFileInput(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayoutData(gridData);

		new Label(composite, SWT.NONE).setText("File:");
		fileText = new Text(composite, SWT.BORDER);
		fileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fileText.addModifyListener(new ModifyListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e)
			{
				applyFile();
			}
		});
		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				browseButtonPressed();
			}
		});

		return composite;
	}

	private void applyScale()
	{
		int x = Integer.parseInt(scaleText.getText());
		targetWidth.setText(String.valueOf(Math.round(Integer.parseInt(selectionWidth.getText()) * (x / 100f))));
		targetHeight.setText(String.valueOf(Math.round(Integer.parseInt(selectionHeight.getText()) * (x / 100f))));
	}

	protected Control createResizeArea(Composite parent)
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

		new Label(composite, SWT.NONE).setText("%");

		scale = new Scale(composite, SWT.NONE);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		scale.setMinimum(1);
		scale.setMaximum(1000);
		scale.setPageIncrement(10);
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
		scale.setEnabled(false);

		Label targetSizeLabel = new Label(composite, SWT.NONE);
		targetSizeLabel.setText("Target size:");
		targetWidth = new Text(composite, SWT.BORDER);
		targetWidth.setEnabled(false);
		new Label(composite, SWT.NONE).setText("x");
		targetHeight = new Text(composite, SWT.BORDER);
		targetHeight.setEnabled(false);

		createImageView(composite);

		return composite;
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

		// TODO
//		try {
//			Image img = new Image(getShell().getDisplay(), Activator.getDefault().getBundle().getResource("/icons/test/DSC00313.JPG").openStream());
//			imageView.setSourceImage(img);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		return imageView;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TrayDialog#close()
	 */
	@Override
	public boolean close()
	{
		if(getReturnCode() != OK) {
			if(image != null) {
				image.dispose();
				image = null;
			}
		}
		return super.close();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		Rectangle clippingArea = imageView.getClippingAreaForSource();
		if(clippingArea.x != 0 || clippingArea.y != 0 || clippingArea.width != image.getImageData().width || clippingArea.height != image.getImageData().height) {
			Image croppedImage = crop(image, clippingArea.x, clippingArea.y, clippingArea.width, clippingArea.height);
			image.dispose();
			image = croppedImage;
		}
		int x = Integer.parseInt(scaleText.getText());
		if(x != 100) {
			Image scaledImage = new Image(getShell().getDisplay(),
					image.getImageData().scaledTo(
							Math.round(clippingArea.width * (x / 100f)),
							Math.round(clippingArea.height * (x / 100f))));
			image.dispose();
			image = scaledImage;
		}

		String filepath = fileText.getText();
		int idx = filepath.lastIndexOf(File.separatorChar);
		if(idx == -1)
			filename = filepath;
		else
			filename = filepath.substring(idx+1);

//		ImageLoader imageLoader = new ImageLoader();
//		imageLoader.data = new ImageData[] { image.getImageData() };
//		imageLoader.save("/tmp/test.jpg", SWT.IMAGE_JPEG);

		super.okPressed();
	}

	private static Image crop(Image src, int x, int y, int w, int h) {
		Image cropImage = new Image(src.getDevice(), w, h);

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

		return cropImage;
	}

	/**
	 * Get the result image. This value is only available after the
	 * Ok button was pressed.
	 * @return The cropped and scaled image
	 */
	public Image getImage()
	{
		return image;
	}

	/**
	 * Get the simple name of the selected file. This value is only
	 * available after the Ok button was pressed.
	 * @return The filename
	 */
	public String getFilename()
	{
		return filename;
	}
}
