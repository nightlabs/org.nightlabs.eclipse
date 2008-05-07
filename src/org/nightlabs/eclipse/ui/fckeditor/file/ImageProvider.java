package org.nightlabs.eclipse.ui.fckeditor.file;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ImageProvider
{
	private Device device;
	private Map<String, Image> imagesByContentType;
	private Collection<Image> thumbnails;
	private int thumbnailSize = 64;

	public ImageProvider(Device device)
	{
		this.device = device;
	}

	private synchronized Image getImage(String contentType)
	{
		if(imagesByContentType == null)
			imagesByContentType = new HashMap<String, Image>();
		Image image = imagesByContentType.get(contentType);
		if(image != null)
			return image;
		String dir = thumbnailSize >= 128 ? "mimetypes_128" : "mimetypes_64";
		String filename = "/icons/"+dir+"/"+contentType.toLowerCase().replaceAll("[^a-z0-9]", "_")+".png";
		System.out.println("resource filename: "+filename);
		URL resource = Activator.getDefault().getBundle().getResource(filename);
		System.out.println("resource url: "+resource);
		if(resource == null)
			return null;
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(resource);
		image = imageDescriptor.createImage();
		imagesByContentType.put(contentType, image);
		return image;
	}

	private synchronized Image getThumbnail(IFCKEditorContentFile file)
	{
		Image image = new Image(device, new ByteArrayInputStream(file.getData()));
		int width = image.getImageData().width;
		int height = image.getImageData().height;
		if(width > thumbnailSize || height > thumbnailSize) {
			float wx = (float)width / (float)thumbnailSize;
			float wy = (float)height / (float)thumbnailSize;
			float x = Math.max(wx, wy);
			ImageData thumbnailData = image.getImageData().scaledTo(Math.round(width / x), Math.round(height / x));
			Image thumbnail = new Image(device, thumbnailData);
			image.dispose();
			image = thumbnail;
		}
		if(thumbnails == null)
			thumbnails = new ArrayList<Image>();
		thumbnails.add(image);
		return image;
	}

	public Image getImage(IFCKEditorContentFile file)
	{
		String contentType = "application/unknown";
		if(file != null && file.getContentType() != null)
			contentType = file.getContentType();
		if("image/jpeg".equals(contentType))
			return getThumbnail(file);
		else if("image/png".equals(contentType))
			return getThumbnail(file);
		else if("image/gif".equals(contentType))
			return getThumbnail(file);
		Image image = getImage(contentType);
		if(image == null)
			image = getImage("application/unknown");
		return image;
	}

	public synchronized void dispose()
	{
		if(imagesByContentType != null) {
			for (Image image : imagesByContentType.values())
				image.dispose();
			imagesByContentType = null;
		}
		if(thumbnails != null) {
			for (Image image : thumbnails)
				image.dispose();
			thumbnails = null;
		}
	}

	public int getThumbnailSize()
	{
		return thumbnailSize;
	}

	public void setThumbnailSize(int thumbnailSize)
	{
		this.thumbnailSize = thumbnailSize;
	}
}
