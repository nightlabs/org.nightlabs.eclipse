package org.nightlabs.base.ui.composite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.util.ImageUtil;
import org.nightlabs.util.IOUtil;

/**
 * A small composite that is capable of displaying an icon and selecting a new one from disk.
 *  
 * @author abieber
 */
public class IconEditComposite extends XComposite {
	
	private Label imageLabel;
	private FileSelectionComposite fileSelectionComposite;
	private byte[] iconData;
	private byte[] fallbackData;
	private Image currentImage;
	
	private ListenerList modifyListeners = new ListenerList();
	private boolean settingFallback = false;

	/**
	 * Create a new {@link IconEditComposite} to edit a 16x16 icon.
	 * 
	 * @param parent The parent to use.
	 * @param style The style of the widget (note that within a {@link Group} will be created)
	 * @param title The title of the surrounding group
	 */
	public IconEditComposite(Composite parent, int style, String title) {
		this(parent, style, title, Messages.getString("org.nightlabs.base.ui.composite.IconEditComposite.fileSelection.title"));
	}
	
	/**
	 * Create a new {@link IconEditComposite} to edit a 16x16 icon.
	 * 
	 * @param parent The parent to use.
	 * @param style The style of the widget (note that within a {@link Group} will be created)
	 * @param title The title of the surrounding group
	 * @param fileSelectionTitle A title for the file-selection-text
	 */
	public IconEditComposite(Composite parent, int style, String title, String fileSelectionTitle) {
		this(parent, style, title, fileSelectionTitle, 16, 16);
	}
	
	
	/**
	 * Create a new {@link IconEditComposite}.
	 * 
	 * @param parent The parent to use.
	 * @param style The style of the widget (note that within a {@link Group} will be created)
	 * @param title The title of the surrounding group
	 * @param width The width of the icon (different sizes will be scaled)
	 * @param height The height of the icon (different sizes will be scaled)
	 */
	public IconEditComposite(Composite parent, int style, String title, String fileSelectionTitle, int width, int height) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		Group group = new Group(this, SWT.NONE);
		group.setText(title != null ? title : ""); //$NON-NLS-1$
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gl = new GridLayout();
		group.setLayout(gl);
		gl.numColumns = 2;
		gl.makeColumnsEqualWidth = false;
		imageLabel = new Label(group, SWT.NONE);
		imageLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
		fileSelectionComposite = new FileSelectionComposite(group, SWT.NONE, FileSelectionComposite.OPEN_FILE, fileSelectionTitle, null); //$NON-NLS-1$
		fileSelectionComposite.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				File file = fileSelectionComposite.getFile();
				if (file.exists()) {
					refresh(readImage(file));
				} else {
					refresh(null);
				}
				if (!settingFallback)
					notifyModifyListeners();
			}
		});
		Image fallBackImage = ImageUtil.createColorImage(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	    fallbackData = getImageData(fallBackImage);
	    fallBackImage.dispose();
	    
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				if (currentImage != null) {
					currentImage.dispose();
				}
			}
		});
		refresh(null);
	}
	
	private byte[] readImage(File file) {
		try {
			FileInputStream fin = new FileInputStream(file);
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				IOUtil.transferStreamData(fin, out);
				return out.toByteArray();
			} finally {
				fin.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void refresh(byte[] _iconData) {
		iconData = _iconData;
		if (iconData == null) {
			iconData = fallbackData;
		}
		ImageData imageData = new ImageData(new ByteArrayInputStream(iconData));
		if (imageLabel.getImage() != null) {
			imageLabel.getImage().dispose();
		}
		currentImage = new Image(getDisplay(), imageData);
		if (currentImage.getBounds().height != 16 || currentImage.getBounds().width != 16) {
			currentImage = ImageUtil.resize(currentImage, 16, 16, false);
			iconData = getImageData(currentImage);
		}
		imageLabel.setImage(currentImage);
		layout(true, true);
	}
	
	private byte[] getImageData(Image image) {
		ImageLoader loader = new ImageLoader();
	    loader.data = new ImageData[] {image.getImageData()};
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    loader.save(out, SWT.IMAGE_PNG);
	    return out.toByteArray();			
	}
	
	/**
	 * @return The data of the icon currently displayed.
	 */
	public byte[] getIconData() {
		return iconData;
	}
	
	/**
	 * Set the icon-data that should be returned and displayed, when no icon was selected.
	 * 
	 * @param fallbackData The fallback-data to set.
	 * @param refresh Whether the edit composite should be refreshed
	 */
	public void setFallbackData(byte[] fallbackData, boolean refresh) {
		settingFallback = true;
		try {
			if (fallbackData != null) {
				this.fallbackData = fallbackData;
			}
			if (refresh) {
				refresh(null);
			}
		} finally {
			settingFallback = false;
		}
	}
	
	/**
	 * Set the icon that should be returned and displayed, when no icon was selected.
	 * 
	 * @param fallbackIcon The fallback-icon to set.
	 * @param refresh Whether the edit composite should be refreshed
	 */
	public void setFallbackIcon(Image fallbackIcon, boolean refresh) {
		setFallbackData(getImageData(fallbackIcon), refresh);
	}
	
	public void addModifyListener(ModifyListener listener) {
		modifyListeners.add(listener);
	}
	
	public void removeModifyListener(ModifyListener listener) {
		modifyListeners.remove(listener);
	}
	
	private void notifyModifyListeners() {
		Object[] listeners = modifyListeners.getListeners();
		// NoSuchMethodError for RCP? compiled against RAP?
//		ModifyEvent event = new ModifyEvent(this);
		for (Object listener : listeners) {
			if (listener instanceof ModifyListener) {
				((ModifyListener) listener).modifyText(null);
			}
		}
	}
	
}