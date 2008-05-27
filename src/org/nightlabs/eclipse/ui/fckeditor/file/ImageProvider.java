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

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ImageProvider implements IImageProvider
{
	private Display device;
	private Map<String, Image> imagesByContentType;
	private Map<Long, Image> thumbnails;
	private int thumbnailSize = 64;
	private ThumbnailLoaderThread thumbnailLoaderThread;

	public ImageProvider(Display device)
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
		//System.out.println("resource filename: "+filename);
		URL resource = Activator.getDefault().getBundle().getResource(filename);
		//System.out.println("resource url: "+resource);
		if(resource == null)
			return null;
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(resource);
		image = imageDescriptor.createImage();
		imagesByContentType.put(contentType, image);
		return image;
	}

	private Image getThumbnail(IFCKEditorContentFile file)
	{
		Image image = new Image(device, new ByteArrayInputStream(file.getData()));
		ImageData imageData = image.getImageData();
		int width = imageData.width;
		int height = imageData.height;
		int myThumbnailSize;
		synchronized (this) {
			myThumbnailSize = thumbnailSize;
		}
		if(width > myThumbnailSize || height > myThumbnailSize) {
			float wx = (float)width / (float)myThumbnailSize;
			float wy = (float)height / (float)myThumbnailSize;
			float x = Math.max(wx, wy);
			ImageData thumbnailData = imageData.scaledTo(Math.round(width / x), Math.round(height / x));
			Image thumbnail = new Image(device, thumbnailData);
			image.dispose();
			image = thumbnail;
		}
		synchronized(this) {
			if(thumbnails == null)
				thumbnails = new HashMap<Long, Image>();
			thumbnails.put(file.getFileId(), image);
		}
		return image;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IImageProvider#getImage(org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile)
	 */
	public Image getImage(IFCKEditorContentFile file)
	{
		return getImage(file, null);
	}

	private static class ThumbnailLoaderThread extends Thread
	{
		private ImageProvider imageProvider;
		private volatile boolean shutdown;

		public ThumbnailLoaderThread(ImageProvider imageProvider)
		{
			this.imageProvider = imageProvider;
			setPriority(Thread.MIN_PRIORITY);
		}

		private static class File
		{
			IImageCallback imageCallback;
			IFCKEditorContentFile file;
		}
		private LinkedList<File> files = new LinkedList<File>();

		public void addFile(IImageCallback imageCallback, IFCKEditorContentFile file)
		{
			File toAdd = new File();
			toAdd.imageCallback = imageCallback;
			toAdd.file = file;
			//System.out.println("Have file: "+file);
			synchronized (files) {
				files.add(toAdd);
				files.notifyAll();
			}
		}

		public void shutdown()
		{
			//System.out.println("shutdown");
			this.shutdown = true;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			while(!shutdown) {
				File _file = null;
				synchronized (files) {
					if(!files.isEmpty())
						_file = files.poll();
					else
						try {
							files.wait(100);
						} catch (InterruptedException e) {
							return;
						}
				}
				if(_file != null) {
					final IFCKEditorContentFile file = _file.file;
					final IImageCallback imageCallback = _file.imageCallback;
					// TODO: remove debug
					System.out.println("Thumbnail loader thread loading: "+file.getName());
					final Image thumbnail = imageProvider.getThumbnail(file);
					if(thumbnail != null) {
						imageProvider.device.asyncExec(new Runnable() {
							/* (non-Javadoc)
							 * @see java.lang.Runnable#run()
							 */
							@Override
							public void run()
							{
								imageCallback.updateImage(file, thumbnail);
							}
						});
					}
				}
			}
			// TODO: remove debug
			System.out.println("Thumbnail loader thread done.");
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IImageProvider#getImage(org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile, org.nightlabs.eclipse.ui.fckeditor.file.IImageCallback)
	 */
	public Image getImage(IFCKEditorContentFile file, IImageCallback imageCallback)
	{
		String contentType = "application/unknown";
		if(file != null && file.getContentType() != null)
			contentType = file.getContentType();
		if(file.isImageFile()) {
			synchronized (this) {
				if(thumbnails != null) {
					Image thumbnail = thumbnails.get(file.getFileId());
					if(thumbnail != null)
						 return thumbnail;
				}
			}
			if(imageCallback == null)
				return getThumbnail(file);
			else {
				synchronized(this) {
					if(thumbnailLoaderThread == null) {
						thumbnailLoaderThread = new ThumbnailLoaderThread(this);
						thumbnailLoaderThread.start();
					}
				}
				thumbnailLoaderThread.addFile(imageCallback, file);
			}
		}
		Image image = getImage(contentType);
		if(image == null)
			image = getImage("application/unknown");
		return image;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IImageProvider#dispose()
	 */
	public void dispose()
	{
		stopThumbnailing();
		disposeIcons();
		disposeThumbnails();
	}

	private synchronized void disposeIcons()
	{
		if(imagesByContentType != null) {
			for (Image image : imagesByContentType.values())
				image.dispose();
			imagesByContentType = null;
		}
	}

	private synchronized void disposeThumbnails()
	{
		if(thumbnails != null) {
			for (Image image : thumbnails.values())
				image.dispose();
			thumbnails = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IImageProvider#stopThumbnailing()
	 */
	public synchronized void stopThumbnailing()
	{
		if(thumbnailLoaderThread != null) {
			thumbnailLoaderThread.shutdown();
			thumbnailLoaderThread = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IImageProvider#getThumbnailSize()
	 */
	public synchronized int getThumbnailSize()
	{
		return thumbnailSize;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IImageProvider#setThumbnailSize(int)
	 */
	public void setThumbnailSize(int thumbnailSize)
	{
		stopThumbnailing();
		disposeThumbnails();
		synchronized (this) {
			this.thumbnailSize = thumbnailSize;
		}
	}
}
