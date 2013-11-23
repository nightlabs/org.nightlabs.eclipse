package org.nightlabs.eclipse.ui.control.export.model;

import org.eclipse.swt.graphics.ImageData;

public class Cell
{
	private String text;
	private ImageData imageData;

	public Cell(String text, ImageData imageData) {
		this.text = text;
		this.imageData = imageData;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setImageData(ImageData imageData) {
		this.imageData = imageData;
	}

	public ImageData getImageData() {
		return imageData;
	}
}
