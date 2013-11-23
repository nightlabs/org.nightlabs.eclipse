package org.nightlabs.eclipse.ui.control.export.model;

import org.eclipse.swt.graphics.ImageData;

public class Column
{
	private String name;
	private ImageData imageData;

	public Column(String name, ImageData imageData) {
		this.name = name;
		this.imageData = imageData;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setImageData(ImageData imageData) {
		this.imageData = imageData;
	}

	public ImageData getImageData() {
		return imageData;
	}
}
